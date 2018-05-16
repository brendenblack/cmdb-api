package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.features.servers.crawl.LinuxCrawler;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.*;

public class LinuxCrawler_ReadEtcReleaseResults_Tests
{
    private LinuxCrawler sut;

    @Test
    public void shouldReturnProperties() throws IOException
    {
        this.sut = LinuxCrawlerFactory.getLinuxCrawler();

        File file = new File("src/test/resources/LinuxEtcRelease.txt");
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/LinuxEtcRelease.txt")));

        Properties p = this.sut.readEtcReleaseResults(content);

        assertNotNull(p);
        assertEquals(16, p.size());
    }

    @Test
    public void propertiesShouldNotHaveSurroundingQuotes() throws IOException
    {
        this.sut = LinuxCrawlerFactory.getLinuxCrawler();
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/LinuxEtcRelease.txt")));

        Properties properties = this.sut.readEtcReleaseResults(content);

        assertNotNull(properties);
        assertTrue(properties.size() > 0);

        for (String key : properties.stringPropertyNames())
        {
            String value = properties.getProperty(key);
            assertFalse(value.startsWith("\""));
            assertFalse(value.endsWith("\""));
        }
    }
}
