package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.pastdev.jsch.DefaultSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Service("linuxCrawler")
public class LinuxCrawler implements Crawler
{
    private static final String OS_FAMILY = "Linux";
    private static final Logger log = LoggerFactory.getLogger(LinuxCrawler.class);

    @Override
    public String getCrawlFor()
    {
        return OS_FAMILY;
    }

    @Override
    public OperatingSystem getOsFacts(DefaultSessionFactory sessionFactory)
    {
        OperatingSystem os = new OperatingSystem();

        String catCommand = "cat /etc/os-release";
        try
        {
            String catResults = JschHelper.doExecuteCommand(sessionFactory, catCommand);
            Properties properties = readEtcReleaseResults(catResults);
            os.setFamily(OS_FAMILY);
            os.setVariantId(properties.getProperty("VARIANT_ID"));
            os.setVersion(properties.getProperty("VERSION_ID"));
            os.setVersionName(properties.getProperty("VERSION"));
            os.setName(properties.getProperty("NAME"));

            log.trace("Host: [family: {}] [variant: {}] [version: {}] [version name: {}] [name: {}]",
                      os.getFamily(),
                      os.getVariantId(),
                      os.getVersion(),
                      os.getVersionName(),
                      os.getName());

            return os;
        }
        catch (IOException | JSchException e)
        {
            throw new CrawlException(e);
        }
    }

    public Properties readEtcReleaseResults(String result)
    {
        Properties p = new Properties();
        String[] lines = result.split("\n");
        for (String line : lines)
        {
            if (line.contains("="))
            {
                log.trace("Line {}", line);
                try
                {
                    p.load(new StringReader(line));
                }
                catch (IOException e)
                {
                    log.error("An error occurred trying to read line: {}", line, e);
                }
            }
        }
        log.trace("Found {} properties", p.size());
        for (String key : p.stringPropertyNames())
        {
            String value = p.getProperty(key);
            if (value.startsWith("\""))
            {
                log.debug("Trimming surrounding quotes from property with key {}", key, value);
                value = value.replaceAll("^\"|\"$", "");
                p.put(key, value);
            }
            log.debug("{}: {}", key, value);
        }

        return p;
    }

    @Override
    public Set<FileSystem> getFileSystems(DefaultSessionFactory sessionFactory)
    {
        String command = "df -T";
        try
        {
            String dfResults = JschHelper.doExecuteCommand(sessionFactory, command);
            return readDfResults(dfResults);

        }
        catch (IOException | JSchException e)
        {
            throw new CrawlException(e);
        }
    }

    public Set<FileSystem> readDfResults(String dfResults)
    {
        String[] lines = dfResults.split("\n");
        Set<FileSystem> filesystems = new HashSet<>();
        for (String line : lines)
        {
            line = line.replaceAll("\\s+", "|");
            String[] cols = line.split("\\|");
            StringBuilder sb = new StringBuilder();
            for (String col : cols)
            {
                sb.append(col).append(" ");
            }
            log.debug(sb.toString());

            if (cols.length == 7)
            {
                FileSystem fs = new FileSystem();

                fs.setName(cols[0]);
                fs.setType(cols[1]);
                try
                {
                    long size = Long.parseLong(cols[2]);
                    fs.setSize(size);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Unable to parse '{}' as a number", cols[2]);
                    fs.setSize(-1);
                }

                try
                {
                    long used = Long.parseLong(cols[3]);
                    fs.setUsed(used);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Unable to parse '{}' as a number", cols[3]);
                    fs.setUsed(-1);
                }

                try
                {
                    long avail = Long.parseLong(cols[4]);
                    fs.setAvailable(avail);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Unable to parse '{}' as a number", cols[3]);
                    fs.setAvailable(-1);
                }

                fs.setMountedOn(cols[6]);

                log.debug("Filesystem [name: {}] [type: {}] [size: {}] [used: {}] [avail: {}] [mounted: {}]",
                          fs.getName(),
                          fs.getType(),
                          fs.getSize(),
                          fs.getUsed(),
                          fs.getAvailable(),
                          fs.getMountedOn());

                filesystems.add(fs);
            }
            else
            {
                log.warn("Unable to parse line '{}', it does not have 7 lines", line);
            }
        }

        log.trace("Found {} filesystems", filesystems.size());

        return filesystems;
    }

    @Override
    public String getArchitecture(DefaultSessionFactory sessionFactory)
    {
        String command = "uname -i";
        try
        {
            String result = JschHelper.doExecuteCommand(sessionFactory, command);

            return readUnameIResults(result);
        }
        catch (IOException | JSchException e)
        {
            throw new CrawlException(e);
        }
    }

    public String readUnameIResults(String unameResult)
    {
        return unameResult.trim();
    }
}
