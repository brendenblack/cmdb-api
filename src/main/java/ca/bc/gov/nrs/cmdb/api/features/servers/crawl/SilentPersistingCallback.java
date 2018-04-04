package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.OperatingSystem;
import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.models.components.ComponentInstance;
import ca.bc.gov.nrs.cmdb.api.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple callback implementation that will handle the persistence of the {@link CrawlResult}.
 */
@org.springframework.stereotype.Component("simpleCrawlCallback")
public class SilentPersistingCallback implements CrawlCallback
{
    private static final Logger log = LoggerFactory.getLogger(SilentPersistingCallback.class);

    private final ServerRepository serverRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;
    private final ComponentInstanceRepository componentInstanceRepository;

    @Autowired
    public SilentPersistingCallback(ServerRepository serverRepository,
                                    OperatingSystemRepository operatingSystemRepository,
                                    ProjectRepository projectRepository,
                                    ComponentRepository componentRepository,
                                    ComponentInstanceRepository componentInstanceRepository)
    {
        this.serverRepository = serverRepository;
        this.operatingSystemRepository = operatingSystemRepository;
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
        this.componentInstanceRepository = componentInstanceRepository;
    }

    @Override
    public void sendMessage(CrawlStatusMessage message)
    {
        log.warn("Triggering the wrong send message!"); // TODO: remove
        // no-op
    }

    @Override
    public void finalizeCrawl(CrawlResult result)
    {
        log.info("Finalizing crawl {}", result.getCrawlId());
        LocalDateTime now = LocalDateTime.now();
        if (result.getSecret() != null)
        {
            // create connection object?
        }

        Server server = saveServer(result);

        Map<String,Project> projects = getOrCreateProjects(result);

        Map<String,Component> components = getOrCreateComponents(projects, result);

        log.info("Creating instances");
        List<ComponentInstance> instances = getOrCreateComponentInstances(components, server.getFileSystems(), result);
        log.info("Done");
    }

    public Map<String, Project> getOrCreateProjects(CrawlResult result)
    {
        List<Project> projects = result.getComponentInstances().stream()
                .map(c -> c.getComponent().getProject())
                .collect(Collectors.toList());

        Map<String,Project> map = new HashMap<>();
        for (Project p : projects)
        {
            Optional<Project> project = this.projectRepository.findByAcronym(p.getAcronym());
            if (project.isPresent())
            {
                log.debug("Using existing project object with key {}", project.get().getAcronym());
                // if this project already exists, return the existing object
                map.put(project.get().getAcronym(), project.get());
            }
            else
            {
                log.debug("No project exists with key {}, creating...", p.getAcronym());
                // if this project doesn't exist already, we'll create it with what little information we have on hand
                Project newlyCreatedProject = this.projectRepository.save(p);
                map.put(newlyCreatedProject.getAcronym(), newlyCreatedProject);
                log.debug("A project with key {} has been persisted with id {}",
                          newlyCreatedProject.getAcronym(),
                          newlyCreatedProject.getId());
            }
        }

        return map;
    }


    public Map<String,Component> getOrCreateComponents(Map<String,Project> projects, CrawlResult result)
    {
        Map<String,Component> map = new HashMap<>();

        for (ComponentInstance instance : result.getComponentInstances())
        {
            Component c = instance.getComponent();
            if (map.containsKey(c.getName()))
            {
                continue;
            }

            Optional<Component> existingComponent = this.componentRepository.findByName(c.getName());
            if (existingComponent.isPresent())
            {
                // TODO: this is NPE bait
                if (!existingComponent.get().getProject().getAcronym().equalsIgnoreCase(c.getProject().getAcronym()))
                {
                    log.warn("This is bad");
                }

                map.put(c.getName(), existingComponent.get());
            }
            else
            {
                log.debug("No component exists with name {}, creating it", c.getName());
                Project p = projects.get(c.getProject().getAcronym());
                if (p == null)
                {
                    log.error("A project with key {} has not been provided, cannot create component {}",
                              c.getProject().getAcronym(),
                              c.getName());
                    continue;
                }

                Component component = new Component();
                component.setName(c.getName());
                component.setProject(p);
                Component newlyCreatedComponent = this.componentRepository.save(component);
                map.put(c.getName(), newlyCreatedComponent);
            }
        }

        return map;
    }

    public List<ComponentInstance> getOrCreateComponentInstances(Map<String,Component> components, Set<FileSystem> filesystems, CrawlResult result)
    {
        List<ComponentInstance> results = new ArrayList<>();

        for (ComponentInstance instance : result.getComponentInstances())
        {
            Component component = components.get(instance.getComponent().getName());

            if (component == null)
            {
                log.warn("No component has been provided with name {}; skipping creation of instance of {} installed at {}",
                         instance.getComponent().getName(),
                         instance.getVersion(),
                         instance.getInstallPath());

                continue;
            }

            Optional<FileSystem> fs = filesystems.stream()
                    .filter(f -> f.getMountedOn().equalsIgnoreCase(instance.getFilesystem().getMountedOn()))
                    .findFirst();

            if (!fs.isPresent())
            {
                log.warn("Unable to find a filesystem mounted on {}, skipping creation of component instance {} {}",
                          instance.getFilesystem().getMountedOn(),
                          instance.getComponent().getName(),
                          instance.getVersion());

                continue;
            }

            Iterable<ComponentInstance> existingInstances = this.componentInstanceRepository.findAllByFilesystem(fs.get());
            ComponentInstance existingInstance = null;
            for (ComponentInstance i : existingInstances)
            {
                if (instance.getInstallPath().equalsIgnoreCase(i.getInstallPath()))
                {
                    log.debug("Instance already exists");
                    existingInstance = i;
                    results.add(i);
                }
            }

            if (existingInstance == null)
            {
                log.debug("An instance record for {} version {} installed at {} does not yet exist, creating it...",
                          instance.getComponent().getName(),
                          instance.getVersion(),
                          instance.getInstallPath());

                ComponentInstance i = new ComponentInstance();
                i.setComponent(component);
                i.setFilesystem(fs.get());
                i.setInstallPath(instance.getInstallPath());
                i.setVersion(instance.getVersion());

                log.debug("Component null check: [component: {}] [fs: {}] [install path: {}] [version: {}] [build: {}]",
                          i.getComponent() == null,
                          i.getFilesystem() == null,
                          i.getInstallPath() == null,
                          i.getVersion() == null,
                          i.getBuild() == null);

                log.debug("Repository null check [repository: {}] [object: {}]",
                          this.componentInstanceRepository == null,
                          i == null);

                ComponentInstance newlyCreatedInstance = this.componentInstanceRepository.save(i);
                if (newlyCreatedInstance == null)
                {
                    log.error("An unknown error occurred creating an instance of {} version {}",
                              i.getComponent().getName(),
                              i.getVersion());
                }
                else
                {
                    log.info("Created component instance [name: {}] [version: {}] [server: {}] [path: {}]",
                             newlyCreatedInstance.getComponent().getName(),
                             newlyCreatedInstance.getVersion(),
                             newlyCreatedInstance.getFilesystem().getServer().getFqdn(),
                             newlyCreatedInstance.getInstallPath());
                    results.add(newlyCreatedInstance);
                }
            }
        }

        return results;
    }


    public Server saveServer(CrawlResult result)
    {
        Optional<Server> existingServer = this.serverRepository.findByFqdn(result.getServer().getFqdn());
        Server server;
        if (existingServer.isPresent())
        {
            server = existingServer.get();
        }
        else
        {
            server = new Server();
            server.setFqdn(result.getServer().getFqdn());
        }

        if (result.getOperatingSystem() != null)
        {
            Optional<OperatingSystem> existingOs = this.operatingSystemRepository.findByNameAndVersion(
                    result.getOperatingSystem().getName(),
                    result.getOperatingSystem().getVersion());

            if (existingOs.isPresent())
            {
                // Check to see if this OS is already represented in the database; if it is add a reference to the existing
                // one to the server
                server.setOperatingSystem(existingOs.get());
            }
            else
            {
                server.setOperatingSystem(result.getServer().getOperatingSystem());
            }
        }

        server.setArchitecture(result.getServer().getArchitecture());

        // TODO: TRACE lines, remove

        for (FileSystem fs : server.getFileSystems())
        {
            log.trace("Filesystem: [name: {}] [mounted on: {}] ", fs.getName(), fs.getMountedOn());
        }

        for (FileSystem fs : result.getFileSystems())
        {
            log.trace("Attempting to add filesystem [name: {}] [mounted on: {}] ", fs.getName(), fs.getMountedOn());
            boolean added = server.hasFileSystem(fs);
            log.trace("Added? {}", added);
        }

        log.info("Saving server from crawl update [fqdn: {}] [os: {} - {}] [filesystems: {}]",
                 server.getFqdn(),
                 server.getOperatingSystem().getName(),
                 server.getOperatingSystem().getVersion(),
                 server.getFileSystems().size());

        return this.serverRepository.save(server);
    }

}
