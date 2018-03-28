package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.DefaultSessionFactory;

import java.io.IOException;
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
     *
     * @param sessionFactory
     * @return
     */
    OperatingSystem getOsFacts(DefaultSessionFactory sessionFactory);

    Set<FileSystem> getFileSystems(DefaultSessionFactory sessionFactory);

    String getArchitecture(DefaultSessionFactory sessionFactory);
}
