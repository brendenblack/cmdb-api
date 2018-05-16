package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.features.servers.crawl.LinuxCrawler;
import ca.bc.gov.nrs.infra.cmdb.domain.models.FileSystem;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LinuxCrawler_ReadDfResults_Tests
{
    @Test
    public void shouldReturnProperties() throws IOException
    {
        LinuxCrawler sut = LinuxCrawlerFactory.getLinuxCrawler();
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/LinuxDf.txt")));

        Set<FileSystem> filesystems = sut.readDfResults(content);

        assertNotNull(filesystems);
        assertEquals(14, filesystems.size());
    }
}
