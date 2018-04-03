package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
@EqualsAndHashCode
public class ComponentInstallation
{
    private String projectKey;
    private String componentName;
    private String componentVersion;
    private Path installPath;
    private boolean isCurrent;
}
