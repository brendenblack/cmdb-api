package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import ca.bc.gov.nrs.cmdb.api.models.Secret;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class CrawlResult
{
    @Getter
    @Setter
    private Server server;

    @Getter
    @Setter
    private Secret secret;

    @Getter
    @Setter
    private String crawlId;

    @Getter
    @Setter
    private boolean success;

    public OperatingSystem getOperatingSystem()
    {
        return (server == null ) ? null : this.server.getOperatingSystem();
    }

    public Set<FileSystem> getFileSystems()
    {
        return (server == null ) ? null : this.server.getFileSystems();
    }


}
