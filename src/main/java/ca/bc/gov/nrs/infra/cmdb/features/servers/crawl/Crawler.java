package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.models.components.ComponentInstance;
import ca.bc.gov.nrs.infra.cmdb.models.FileSystem;
import ca.bc.gov.nrs.infra.cmdb.models.OperatingSystem;
import com.pastdev.jsch.DefaultSessionFactory;

import java.util.Set;

public interface Crawler
{
    /**
     * An indication of what family of target server this crawler is meant for. This value should match what is returned
     * by running <code>uname -s</code> on the target server if it is Linux, or ... if it is Windows.
     *
     * @return
     */
    String getCrawlFor();

    /**
     * Retrieves a basic set of facts about the target host's operating system, to populate an {@link OperatingSystem}
     * object.
     *
     * @param sessionFactory
     * @return
     */
    OperatingSystem getOsFacts(DefaultSessionFactory sessionFactory);

    Set<FileSystem> getFileSystems(DefaultSessionFactory sessionFactory);

    String getArchitecture(DefaultSessionFactory sessionFactory);

    Set<ComponentInstance> getComponents(DefaultSessionFactory defaultSessionFactory, Set<FileSystem> filesystems);
}
