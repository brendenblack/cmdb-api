package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlStatusMessage
{
    public final static String STATUS_INFO = "INFO";
    
    private String crawlId;
}
