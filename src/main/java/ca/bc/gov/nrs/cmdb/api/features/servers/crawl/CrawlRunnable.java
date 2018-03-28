package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.UsernamePasswordSecret;
import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.DefaultSessionFactory;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CrawlRunnable implements Runnable
{
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


    @Override
    public void run()
    {
        log.info("Beginning crawl of {} with id {}", this.server.getFqdn(), this.id);

        // Create the DefaultSessionFactory object that will create SSH connections for us
        if (this.credentialSecret != null)
        {
            log.debug("Creating session factory to {} using username & password", this.server.getFqdn());
            // TD: hardcoded port
            DefaultSessionFactory sessionFactory = new DefaultSessionFactory(this.credentialSecret.getUsername(), this.server.getFqdn(), 22);
            sessionFactory.setPassword(this.credentialSecret.getPassword());
            sessionFactory.setConfig("StrictHostKeyChecking", "no"); // https://www.mail-archive.com/jsch-users@lists.sourceforge.net/msg00529.html
            this.sessionFactory = sessionFactory;
        }
        else
        {
            throw new CrawlException("Unable to connect to the target server");
        }

        String osFamily = determineOsFamily();

        Crawler crawler = this.availableCrawlers.get(osFamily);
        if (crawler == null)
        {
            throw new CrawlException("No Crawler implementation found that supports the OS family '" + osFamily + "'");
        }

        log.debug("Detected OS family {}, Using crawler {} ", osFamily, crawler.getClass().getName());

        OperatingSystem os = crawler.getOsFacts(this.sessionFactory);
        this.server.setOperatingSystem(os);
        String architecture = crawler.getArchitecture(this.sessionFactory);
        Set<FileSystem> filesystems = crawler.getFileSystems(this.sessionFactory);
        log.debug("[OS name: {}] [OS version: {}] [architecture: {}] [filesystems: {}]",
                  os.getName(),
                  os.getVersion(),
                  architecture,
                  filesystems.size());



        if (this.callback != null)
        {
            // do callback
        }
    }

    public String determineOsFamily()
    {
        String command = "uname -s";
        try
        {
            String result = JschHelper.doExecuteCommand(this.sessionFactory, command);
            return result.trim();
        }
        catch (IOException | JSchException e)
        {
            throw new CrawlException(e);
        }
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
    //endregion

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
}
