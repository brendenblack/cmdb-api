package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Relies on the /apps_ux/&lt;PROJECT&gt;/&lt;COMPONENT&gt;/&lt;VERSION&gt; naming scheme to discover
 * {@link ComponentInstallation} details.
 */
@Service
public class StandardComponentFinderImpl implements ComponentInstallationFinder
{
    private static final Logger log = LoggerFactory.getLogger(StandardComponentFinderImpl.class);

    private List<String> knownRoots = Arrays.asList("/fs/u02/apps_ux");

    /**
     * This component finder is only suitable for Linux (specifically RHEL) hosts
     *
     * @param osFamily
     * @return
     */
    @Override
    public boolean isSuitableFor(String osFamily)
    {
        return (osFamily.equalsIgnoreCase("Linux"));
    }

    @Override
    public String getStrategyDescription()
    {
        return "Retrieves components from a known set of locations based on an installation naming convention";
    }

    @Override
    public Set<ComponentInstallation> getComponentInstallations(FileSystem fs) throws IOException
    {
        Set<ComponentInstallation> installations = new HashSet<>();

        for (String rootPath : this.knownRoots)
        {
            if (!Files.exists(fs.getPath(rootPath)))
            {
                log.debug("Standard application root location {} does not exist, skipping", rootPath);
                continue;
            }

            log.debug("Checking {} for expected installation folders", rootPath);
            List<Path> projectFolders = getProjectFolders(fs, rootPath);
            log.debug("Found {} projects: {}", projectFolders.size(), projectFolders);

            for (Path projectFolder : projectFolders)
            {
                String projectKey = projectFolder.getFileName().toString();
                log.debug("Folder {} looks like project {}",
                          projectFolder.toString(),
                          projectKey);

                Set<ComponentInstallation> components = getInstalledApplicationsForProject(projectKey, projectFolder);
                log.debug("Found {} installed components in project {}", components.size(), projectKey);
                installations.addAll(components);
            }
        }

        log.debug("Found {} total installations", installations.size());
        return installations;
    }


    public List<Path> getProjectFolders(FileSystem fs, String rootPath) throws IOException
    {
        List<String> excludedProjectFolders = Arrays.asList(
                "s6_services",
                "nrscdua",
                "wwwsvr",
                "wwwadm",
                "logs",
                "liferay",
                "jmeter");

        log.debug("Looking for project folders in {}, ignoring folders: {}",
                  rootPath,
                  excludedProjectFolders);

        Path path = fs.getPath(rootPath);
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        List<Path> projectFolders = new ArrayList<>();
        for (Path p : directoryStream)
        {
            if (!excludedProjectFolders.contains(p.getFileName().toString())) // ignore folders that are marked for exclusion
            {
                if (p.getFileName().toString().equals(p.getFileName().toString().toUpperCase())) // pick only folders that are all uppercase
                {
                    log.trace("Folder {} looks like project {}",
                              p.toString(),
                              p.getFileName().toString());

                    projectFolders.add(p);
                }
            }
        }
        return projectFolders;
    }

    public Set<ComponentInstallation> getInstalledApplicationsForProject(String projectKey, Path projectFolder) throws IOException
    {
        log.debug("Looking for component folders in {}", projectFolder.toString());
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(projectFolder);
        Set<ComponentInstallation> apps = new HashSet<>();

        for (Path p : directoryStream)
        {
            String componentName = p.getFileName().toString();
            log.debug("Folder {} looks like component {}",
                      p.toString(),
                      componentName);

            List<ComponentInstallation> versions = new ArrayList<>();

            DirectoryStream<Path> versionStream = Files.newDirectoryStream(p);
            String currentVersion = null;
            for (Path v : versionStream)
            {
                if (!v.getFileName().toString().equalsIgnoreCase("current"))
                {
                    ComponentInstallation app = new ComponentInstallation();
                    app.setProjectKey(projectKey);
                    app.setComponentName(componentName);
                    app.setComponentVersion(v.getFileName().toString());
                    app.setInstallPath(v);

                    log.debug("App: [project: {}] [name: {}] [version: {}] [install: {}]",
                              app.getProjectKey(),
                              app.getComponentName(),
                              app.getComponentVersion(),
                              app.getInstallPath());

                    versions.add(app);
                }
                else
                {

                    log.trace("Found current symlink: {}", v.toString());
                    try
                    {
                        Path realPath = v.toRealPath();
                        log.trace("Real path: {}", realPath.toString());

                        currentVersion = realPath.getFileName().toString();
                        log.trace("Setting current version to {}", currentVersion);
                    }
                    catch (UnsupportedOperationException e)
                    {
                        log.warn("Unable to resolve current version from path {}; this feature is not yet implemented " +
                                         "in jsch-nio",
                                 v.toString(),
                                 e.getMessage());
                    }
                }
            }

            apps.addAll(versions);
        }

        return apps;
    }

}
