package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.WebSocketConfiguration;
import ca.bc.gov.nrs.infra.cmdb.models.Server;
import ca.bc.gov.nrs.infra.cmdb.models.UsernamePasswordSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrawlManager
{
    private static final Logger log = LoggerFactory.getLogger(CrawlManager.class);

    private final Map<String,Crawler> crawlers = new HashMap<>();

    /**
     * A collection of all of the calls that are ongoing
     */
    private static Map<String, CrawlRunnable> crawlsInProgress = new HashMap<>();
    private final List<Crawler> allCrawlers;
    private final CrawlCallback callback;

    @Autowired
    public CrawlManager(List<Crawler> allCrawlers,
                        @Qualifier("websocketCrawlCallback") CrawlCallback callback)
    {
        this.allCrawlers = allCrawlers;
        this.callback = callback;
        for (Crawler crawler : allCrawlers)
        {
            this.crawlers.put(crawler.getCrawlFor(), crawler);
        }
    }

    public String createLocationHeader(String crawlId)
    {
        return WebSocketConfiguration.BROKER_ROOT_PATH + "/crawl/" + crawlId;
    }

    /**
     * Triggers a fact-findigng crawl of the provided server
     *
     * @param server
     * @param secret
     * @return
     */
    public Map<String,String> doCrawl(Server server, UsernamePasswordSecret secret)
    {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-Update-Type", "Websocket");

        // Check to see if this server is already being crawled
        CrawlRunnable crawl = CrawlRunnable.getOngoingCrawls().get(server.getFqdn());
        if (crawl != null)
        {
            log.debug("A crawl is already in progress for {}, returning existing id {}",
                      server.getFqdn(),
                      crawl.getId());

            headers.put("Location", createLocationHeader(crawl.getId()));
            return headers;

        }

        crawl = CrawlRunnable.crawlOf(server)
                .connectUsing(secret)
                .withCrawlers(this.crawlers)
                .withCallback(this.callback);

        new Thread(crawl).start();

        headers.put("Location", createLocationHeader(crawl.getId()));
        return headers;
    }

    public Map<String,CrawlRunnable> getCrawlsInProgress()
    {
        return CrawlRunnable.getOngoingCrawls();
    }
}
