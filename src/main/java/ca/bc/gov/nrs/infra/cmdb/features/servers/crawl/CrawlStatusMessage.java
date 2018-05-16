package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.models.Server;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrawlStatusMessage
{
    public final static String STATUS_INFO = "INFO";
    public final static String STATUS_WARNING = "WARN";
    public final static String STATUS_ERROR = "ERROR";


    private boolean finished;
    private String crawlId;
    private String message;
    private String level;

    private Server server;
}
