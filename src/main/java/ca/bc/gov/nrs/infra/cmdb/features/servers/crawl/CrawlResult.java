package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.domain.models.FileSystem;
import ca.bc.gov.nrs.infra.cmdb.domain.models.OperatingSystem;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Secret;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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

    @Getter
    @Setter
    private Set<ComponentInstance> componentInstances = new HashSet<>();


}
