package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.repositories.OperatingSystemRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A simple callback implementation that will handle the persistence of the {@link CrawlResult}.
 */
@Component("simpleCrawlCallback")
public class PersistingCrawlCallback implements CrawlCallback
{
    private static final Logger log = LoggerFactory.getLogger(PersistingCrawlCallback.class);

    private final ServerRepository serverRepository;
    private final OperatingSystemRepository operatingSystemRepository;

    @Autowired
    public PersistingCrawlCallback(ServerRepository serverRepository,
                                   OperatingSystemRepository operatingSystemRepository)
    {
        this.serverRepository = serverRepository;
        this.operatingSystemRepository = operatingSystemRepository;
    }

    @Override
    public void sendMessage(CrawlStatusMessage message)
    {
        log.warn("Triggering the wrong send message!"); // TODO: remove
        // no-op
    }

    @Override
    public void finalizeCrawl(CrawlResult result)
    {
        log.info("Finalizing crawl {}", result.getCrawlId());
        LocalDateTime now = LocalDateTime.now();
        if (result.getSecret() != null)
        {
            // create connection object?
        }

        Optional<Server> existingServer = this.serverRepository.findByFqdn(result.getServer().getFqdn());
        Server server;
        if (existingServer.isPresent())
        {
            server = existingServer.get();
        }
        else
        {
            server = new Server();
            server.setFqdn(result.getServer().getFqdn());
        }

        if (result.getOperatingSystem() != null)
        {
            Optional<OperatingSystem> existingOs = this.operatingSystemRepository.findByNameAndVersion(
                    result.getOperatingSystem().getName(),
                    result.getOperatingSystem().getVersion());

            if (existingOs.isPresent())
            {
                // Check to see if this OS is already represented in the database; if it is add a reference to the existing
                // one to the server
                server.setOperatingSystem(existingOs.get());
            }
            else
            {
                server.setOperatingSystem(result.getServer().getOperatingSystem());
            }
        }

        server.setArchitecture(result.getServer().getArchitecture());

        server.hasFileSystems(result.getFileSystems());

        log.info("Saving server [fqdn: {}] [os: {} - {}] [filesystems: {}]",
                 server.getFqdn(),
                 server.getOperatingSystem().getName(),
                 server.getOperatingSystem().getVersion(),
                 server.getFileSystems().size());

        this.serverRepository.save(server);

    }
}
