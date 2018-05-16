package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Set;

public interface ComponentInstallationFinder
{
    /**
     * Identifies whether the implementing finder is appropriate for the target host.
     *
     * @param osFamily
     * @return
     */
    boolean isSuitableFor(String osFamily);

    String getStrategyDescription();

    Set<ComponentInstallation> getComponentInstallations(FileSystem fs) throws IOException;
}
