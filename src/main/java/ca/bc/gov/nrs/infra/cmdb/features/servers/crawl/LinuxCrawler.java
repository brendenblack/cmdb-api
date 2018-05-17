package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import ca.bc.gov.nrs.infra.cmdb.domain.models.FileSystem;
import ca.bc.gov.nrs.infra.cmdb.domain.models.OperatingSystem;
import com.pastdev.jsch.DefaultSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @deprecated set for extraction in to a more purpose-built service
 */
@Deprecated
@Service("linuxCrawler")
public class LinuxCrawler implements Crawler
{
    private static final String OS_FAMILY = "Linux";
    private static final Logger log = LoggerFactory.getLogger(LinuxCrawler.class);
    private final List<ComponentInstallationFinder> componentFinders;

    @Autowired
    public LinuxCrawler(List<ComponentInstallationFinder> componentFinders)
    {

        this.componentFinders = componentFinders;
    }

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
        String dfResults = JschHelper.doExecuteCommand(sessionFactory, "df -T");
        return readDfResults(dfResults);

    }

    public Set<FileSystem> readDfResults(String dfResults)
    {
        int units = 1024;
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
                    fs.setSize(size * units);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Unable to parse '{}' as a number", cols[2]);
                    fs.setSize(-1);
                }

                try
                {
                    long used = Long.parseLong(cols[3]);
                    fs.setUsed(used * units);
                }
                catch (NumberFormatException e)
                {
                    log.warn("Unable to parse '{}' as a number", cols[3]);
                    fs.setUsed(-1);
                }

                try
                {
                    long avail = Long.parseLong(cols[4]);
                    fs.setAvailable(avail * units);
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
        String result = JschHelper.doExecuteCommand(sessionFactory, command);
        return readUnameIResults(result);
    }

    @Override
    public Set<ComponentInstance> getComponents(DefaultSessionFactory sessionFactory, Set<FileSystem> filesystems)
    {
        Set<ComponentInstallation> componentInstallations = new HashSet<>();

        Map<String, Object> environment = new HashMap<>();
        environment.put("defaultSessionFactory", sessionFactory);
        try (java.nio.file.FileSystem sshfs = FileSystems.newFileSystem(new URI("ssh.unix://" + sessionFactory.getUsername() + "@" + sessionFactory.getHostname() + "/"), environment))
        {
            for (ComponentInstallationFinder finder : componentFinders)
            {
                log.debug("Looking for components using {}", finder.getClass().getName());
                componentInstallations.addAll(finder.getComponentInstallations(sshfs));
            }
        }
        catch (IOException | URISyntaxException e)
        {
            log.error("An exception occurred while fetching components", e);
            throw new CrawlException(e);
        }

        log.info("Component crawling complete, found {} installations. Converting to component instances", componentInstallations.size());

        return convertInstallationsToInstances(componentInstallations, filesystems);
    }

    public Set<ComponentInstance> convertInstallationsToInstances(Set<ComponentInstallation> installations, Set<FileSystem> filesystems)
    {
        // Retrieve all unique project keys and create Project objects
        Set<String> projectKeys = installations
                .stream()
                .map(c -> c.getProjectKey())
                .collect(Collectors.toSet());

        Map<String, Project> projects = new HashMap<>();
        for (String projectKey : projectKeys)
        {
            Project project = Project.withKey(projectKey).build();
            projects.put(projectKey, project);
        }

        // Retrieve all unique component names and create Component objects
        Set<String> componentNames = installations.stream()
                .map(c -> c.getComponentName())
                .collect(Collectors.toSet());

        Map<String, Component> components = new HashMap<>();
        for (String componentName : componentNames)
        {
            if (!components.containsKey(componentName))
            {
                Component component = Component.ofName(componentName)
                        .belongsTo(null)
                        .build();

                components.put(componentName, component);
            }
        }

        // sort filesystems by their "mounted on" length, so we can try to match the most specific one first
        Comparator<FileSystem> fsComparator =Comparator.comparingInt(f -> f.getMountedOn().length());
        List<FileSystem> sortedFilesystems = new ArrayList<>();
        sortedFilesystems.addAll(filesystems);
        sortedFilesystems.sort(fsComparator);

        // transform ComponentInstallation (a local, temporary concept) in to ComponentInstance (persistent domain model)
        Set<ComponentInstance> instances = new HashSet<>();
        for (ComponentInstallation ci : installations)
        {
            ComponentInstance instance = new ComponentInstance();
            Component component = components.get(ci.getComponentName());
            Project project = projects.get(ci.getProjectKey());
            //component.setProject(project);
            project.addComponent(component);
            instance.setComponent(component);
            instance.setVersion(ci.getComponentVersion());
            instance.setInstallPath(ci.getInstallPath().toAbsolutePath().toString());

            for (FileSystem fs : sortedFilesystems)
            {
                if (ci.getInstallPath().toAbsolutePath().toString().startsWith(fs.getMountedOn()))
                {
                    log.info("Component {} is installed at {} which appears to be on file system {} ({})",
                             ci.getComponentName(),
                             ci.getInstallPath(),
                             fs.getName(),
                             fs.getMountedOn());
                    instance.setFilesystem(fs);
                }
            }

            if (instance.getFilesystem() == null)
            {
                log.warn("Unable to determine a filesystem for {}", instance.getComponent().getName());
            }

            instances.add(instance);
        }

        return instances;
    }

    public String readUnameIResults(String unameResult)
    {
        return unameResult.trim();
    }

    public void getInstalledComponents(DefaultSessionFactory sessionFactory)
    {
        // execute a find command to find all instances of apps_ux because of servers with multiple environment
        // installations that would have more than one.
        String command = "find / -type d -name \"apps_ux\" 2>/dev/null";
        String installLocationsResult = JschHelper.doExecuteCommand(sessionFactory, command);
        Set<String> installLocations = readFindInstallDirsResult(installLocationsResult);

        Map<String, Object> environment = new HashMap<String, Object>();
        environment.put("defaultSessionFactory", sessionFactory);
        try (java.nio.file.FileSystem sshfs = FileSystems.newFileSystem(new URI("ssh.unix://" + sessionFactory.getUsername() + "@" + sessionFactory.getHostname() + "/"), environment))
        {
            List<Path> projectFolders = new ArrayList<>(); // getProjectFolders(sshfs);
            log.info("Found {} project folders", projectFolders.size());

            for (Path p : projectFolders)
            {
//                List<Installation> apps = getInstalledApplicationsForProject(p);
//                log.info("Found {} component folders in project {}",
//                         installedApplications.size(),
//                         p.getFileName().toString());
//                installedApplications.addAll(apps);
            }


        }
        catch (URISyntaxException | IOException e)
        {
            log.error("An error occurred while attempting to create a connection", e);
            throw new CrawlException(e);
        }
    }



    public Set<String> readFindInstallDirsResult(String result)
    {
        return new HashSet<String>(Arrays.asList(result.split("\n")));
    }

    public void gatherStandardApps(DefaultSessionFactory sessionFactory) {}


}
