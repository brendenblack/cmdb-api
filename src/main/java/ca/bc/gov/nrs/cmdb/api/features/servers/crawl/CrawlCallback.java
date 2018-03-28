package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.Server;

public interface CrawlCallback
{
    void doCallback(CrawlStatusMessage message);
}
