//package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;
//
//import ca.bc.gov.nrs.cmdb.api.features.servers.crawl.LinuxCrawler;
//import com.pastdev.jsch.DefaultSessionFactory;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.nio.file.FileSystem;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.Assert.assertFalse;
//
//
//public class LinuxCrawler_getProjectFolders_Tests extends SessionFactoryBase
//{
////    @Test
////    public void shouldReturnProperties() throws IOException, URISyntaxException
////    {
////        LinuxCrawler sut = LinuxCrawlerFactory.getLinuxCrawler();
////
////        Map<String, Object> environment = new HashMap<String, Object>();
////        environment.put("defaultSessionFactory", sessionFactory);
////        try (java.nio.file.FileSystem sshfs = FileSystems.newFileSystem(new URI("ssh.unix://" + sessionFactory.getUsername() + "@" + sessionFactory.getHostname() + "/"), environment))
////        {
////            List<Path> projectFolders = sut.getProjectFolders(sshfs);
////
////            assertFalse(projectFolders.size() == 0);
////        }
////
////    }
//}
