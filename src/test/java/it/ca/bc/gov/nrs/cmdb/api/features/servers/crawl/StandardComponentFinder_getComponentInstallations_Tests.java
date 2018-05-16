package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.features.servers.crawl.ComponentInstallation;
import ca.bc.gov.nrs.infra.cmdb.features.servers.crawl.StandardComponentFinderImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;

public class StandardComponentFinder_getComponentInstallations_Tests extends SessionFactoryBase
{
    private static final Logger log = LoggerFactory.getLogger(StandardComponentFinder_getComponentInstallations_Tests.class);

    @Test
    @Ignore("too slow")
    public void should() throws URISyntaxException, IOException
    {
        Map<String, Object> environment = new HashMap<>();
        environment.put("defaultSessionFactory", this.sessionFactory);
        log.info("Session factory [host: {}] [user: {}]", sessionFactory.getHostname(), sessionFactory.getUsername());
        try (java.nio.file.FileSystem sshfs = FileSystems.newFileSystem(new URI("ssh.unix://" + sessionFactory.getUsername() + "@" + sessionFactory.getHostname() + "/"), environment))
        {
            StandardComponentFinderImpl sut = new StandardComponentFinderImpl();
            Set<ComponentInstallation> apps = sut.getComponentInstallations(sshfs);
            assertFalse(apps.size() <= 0);
        }
    }
}
