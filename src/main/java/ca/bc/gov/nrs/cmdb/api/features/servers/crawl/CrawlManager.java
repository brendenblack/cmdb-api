package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.UsernamePasswordSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrawlManager implements CrawlCallback
{
    private static final Logger log = LoggerFactory.getLogger(CrawlManager.class);

    private final Map<String,Crawler> crawlers = new HashMap<>();

    private static Map<String, CrawlRunnable> crawlsInProgress = new HashMap<>();
    private final List<Crawler> allCrawlers;
    private final SimpMessagingTemplate template;

    @Autowired
    public CrawlManager(List<Crawler> allCrawlers, SimpMessagingTemplate template)
    {
        this.allCrawlers = allCrawlers;
        this.template = template;
        for (Crawler crawler : allCrawlers)
        {
            this.crawlers.put(crawler.getCrawlFor(), crawler);
        }
    }

    public String doCrawl(Server server, UsernamePasswordSecret secret)
    {
        String fqdn = server.getFqdn();
        CrawlRunnable crawl = crawlsInProgress.get(fqdn);

        if (crawl != null)
        {
            return crawl.getId();
        }

        crawl = CrawlRunnable.crawlOf(server)
                .connectUsing(secret)
                .withCrawlers(this.crawlers)
                .withCallback(this);

        new Thread(crawl).start();

        return crawl.getId();
    }

    @Override
    public void doCallback(CrawlStatusMessage message)
    {

    }
}
