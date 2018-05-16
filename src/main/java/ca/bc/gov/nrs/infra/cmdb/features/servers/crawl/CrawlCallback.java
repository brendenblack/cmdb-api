package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

public interface CrawlCallback
{
    void sendMessage(CrawlStatusMessage message);
    void finalizeCrawl(CrawlResult result);
}
