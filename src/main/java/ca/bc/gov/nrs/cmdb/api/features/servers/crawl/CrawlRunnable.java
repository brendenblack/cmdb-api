package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.UsernamePasswordSecret;
import ca.bc.gov.nrs.cmdb.api.models.components.ComponentInstance;
import com.pastdev.jsch.DefaultSessionFactory;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A class used to orchestrate the discovery crawl of a target server. Because this is a long running process, it is
 * meant to be run on a background thread.
 */
public class CrawlRunnable implements Runnable
{
    private static Map<String, CrawlRunnable> ongoing = new HashMap<>();
    public static Map<String,CrawlRunnable> getOngoingCrawls()
    {
        return Collections.unmodifiableMap(ongoing);
    }

    //region setup
    private static final Logger log = LoggerFactory.getLogger(CrawlRunnable.class);

    @Getter
    private final String id;

    @Getter
    private final Server server;

    private DefaultSessionFactory sessionFactory;

    @Setter
    private CrawlCallback callback;

    private UsernamePasswordSecret credentialSecret;

    private Map<String,Crawler> availableCrawlers = new HashMap<>();

    private CrawlRunnable(Server server)
    {
        this.id = UUID.randomUUID().toString();
        this.server = server;
    }
    //endregion

    private boolean cancel = false;
    private final static String CANCELLATION_MESSAGE = "Cancelling crawl by request";

    @Override
    public void run()
    {
        /*
         * This is one hell of a long method that controls all of the auto discovery of a target server, including:
         * - What OS family the server is
         * - Details about the OS
         * - What the architecture is
         * - What filesystems are available
         */
        try
        {
            log.info("Beginning crawl of {} with id {}", this.server.getFqdn(), this.id);
            ongoing.put(this.id, this);
            info("Beginning crawl of " + this.server.getFqdn());

            CrawlResult result = new CrawlResult();

            // Create the DefaultSessionFactory object that will create SSH connections for us
            if (this.credentialSecret != null)
            {
                log.debug("Creating session factory to {} using username & password", this.server.getFqdn());
                // TD: hardcoded port
                DefaultSessionFactory sessionFactory = new DefaultSessionFactory(this.credentialSecret.getUsername(), this.server.getFqdn(), 22);
                sessionFactory.setPassword(this.credentialSecret.getPassword());
                sessionFactory.setConfig("StrictHostKeyChecking", "no"); // https://www.mail-archive.com/jsch-users@lists.sourceforge.net/msg00529.html
                this.sessionFactory = sessionFactory;
                result.setSecret(this.credentialSecret);
            }
            else
            {
                error("Unable to determine a connection method to target server");
            }

            // Before every next step, check to see if a cancel order has been issued.
            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            // Determine the OS family, e.g. Linux, SunOS
            log.debug("Checking OS family of {}", this.server.getFqdn());
            info("Checking OS family");
            String osFamily = determineOsFamily();
            info("OS family: " + osFamily);

            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            // Load in the appropriate crawler for the OS family
            Crawler crawler = this.availableCrawlers.get(osFamily);
            if (crawler == null)
            {
                throw new CrawlException("No Crawler implementation found that supports the OS family '" + osFamily + "'");
            }
            log.debug("Detected OS family {}, Using crawler {} ", osFamily, crawler.getClass().getName());

            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            // Retrieve a set of facts about the OS running on the target server
            log.debug("Gathering facts about target " + osFamily + " OS...");
            info("Gathering facts about target OS");
            OperatingSystem os = crawler.getOsFacts(this.sessionFactory);
            this.server.setOperatingSystem(os);

            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            info("OS facts discovered, checking architecture...");

            // Determine the architecture of the target server, e.g. x86_64, Sun4v
            String architecture = crawler.getArchitecture(this.sessionFactory);
            server.setArchitecture(architecture);
            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            info("Architecture is " + architecture + ", checking filesystems...");

            Set<FileSystem> filesystems = crawler.getFileSystems(this.sessionFactory);
            log.debug("[OS name: {}] [OS version: {}] [architecture: {}] [filesystems: {}]",
                      os.getName(),
                      os.getVersion(),
                      architecture,
                      filesystems.size());
            server.hasFileSystems(filesystems);
            info("Discovered " + filesystems.size() + " filesystems, checking for installed applications...");

            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            Set<ComponentInstance> apps = crawler.getComponents(this.sessionFactory, filesystems);
            result.setComponentInstances(apps);
            info("Discovered " + apps.size() + " application installations");
            if (log.isDebugEnabled())
            {
                for (ComponentInstance app : apps)
                {
                    log.debug("Component instance [project: {}] [name: {}] [version: {}] [path: {}}",
                              app.getComponent().getProject().getKey(),
                              app.getComponent().getName(),
                              app.getVersion(),
                              app.getInstallPath());
                }
            }

            // One final check in case the user wanted to cancel during the previous long running process
            if (this.cancel)
            {
                warn(CANCELLATION_MESSAGE);
                ongoing.remove(this.id);
                return;
            }

            info("Crawl completed");

            result.setCrawlId(this.id);
            result.setServer(this.server);
            result.setSuccess(true);

            if (this.callback != null)
            {
                this.callback.finalizeCrawl(result);
            }
        }
        finally
        {
            // Ensure this crawl is unregistered from the ongoing pool
            log.debug("Removing crawl {} from the ongoing pool", this.id);
            ongoing.remove(this.id);
        }
    }

    /**
     * Attempt to stop this crawl. Cancellation will only take place between discovery steps. That is, if a connection
     * is in the process of timing out or taking an exceptionally long time to complete, this request will have no
     * effect and will instead cancel the crawl after the executing step returns.
     *
     * Any partial results retrieved prior to cancellation will <b>not</b> be persisted.
     */
    public void cancel()
    {
        log.warn("Received request to cancel crawl of {}", this.getServer().getFqdn());
        this.cancel = true;
    }

    public String determineOsFamily()
    {
        String command = "uname -s";
        String result = JschHelper.doExecuteCommand(this.sessionFactory, command);
        return result.trim();

    }



    //region builder methods
    public static CrawlRunnable crawlOf(Server server)
    {
        log.debug("Creating crawl job for {}", server.getFqdn());
        return new CrawlRunnable(server);
    }

    public CrawlRunnable connectUsing(UsernamePasswordSecret secret)
    {
        log.debug("Adding username & password credential [username: {}]", secret.getUsername());
        this.credentialSecret = secret;
        return this;
    }

    public CrawlRunnable withCrawlers(Map<String,Crawler> crawlers)
    {
        log.debug("Adding {} available crawlers", crawlers.size());
        this.availableCrawlers = crawlers;
        return this;
    }

    /**
     * Adds a {@link Crawler} implementation strategy for the provided Operating System family. This will overwrite any
     * previously assigned crawlers for this OS family.
     *
     * @param osFamily
     * @param crawler
     * @return
     */
    public CrawlRunnable withCrawler(String osFamily, Crawler crawler)
    {
        this.availableCrawlers.put(osFamily, crawler);
        return this;
    }

    /**
     * When this task ends, a {@link CrawlCallback} can optionally be provided to deliver the message.
     *
     * @param callback
     * @return
     */
    public CrawlRunnable withCallback(CrawlCallback callback)
    {
        log.debug("Attaching callback of {}", callback.getClass().getName());
        this.callback = callback;
        return this;
    }
    //endregion

    //region status messaging
    public void info(String message)
    {
        CrawlStatusMessage status = new CrawlStatusMessage();
        status.setMessage(message);
        status.setLevel(CrawlStatusMessage.STATUS_INFO);

        doCallback(status);
    }

    public void warn(String message)
    {
        CrawlStatusMessage status = new CrawlStatusMessage();
        status.setMessage(message);
        status.setLevel(CrawlStatusMessage.STATUS_INFO);

        doCallback(status);
    }

    /**
     * Send an error message to the attached callback, and then terminate the crawl process by throwing a
     * {@link CrawlException} with an included message.
     *
     * @param message
     */
    public void error(String message)
    {
        CrawlStatusMessage status = new CrawlStatusMessage();
        status.setMessage(message);
        status.setLevel(CrawlStatusMessage.STATUS_ERROR);

        doCallback(status);

        throw new CrawlException(message);
    }

    public void doCallback(CrawlStatusMessage message)
    {
        if (this.callback != null)
        {
            message.setCrawlId(this.id);
            message.setServer(this.server);
            this.callback.sendMessage(message);
        }
    }
    //endregion
}
