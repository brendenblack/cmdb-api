package ca.bc.gov.nrs.infra.cmdb.domain.services;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultInfrastructureRegistrationServiceImpl implements InfrastructureRegistrationService
{
    private final Logger log = LoggerFactory.getLogger(DefaultInfrastructureRegistrationServiceImpl.class);
    private final CmdbContext context;

    @Autowired
    DefaultInfrastructureRegistrationServiceImpl(CmdbContext context)
    {
        this.context = context;
    }

    @Override
    public Project getOrCreateProject(String projectKey)
    {
        Optional<Project> existingProject = this.context.getProjectRepository().findByKey(projectKey);
        if (existingProject.isPresent())
        {
            return existingProject.get();
        }
        else
        {
            Project project = Project.createProject(projectKey).build();
            return this.context.getProjectRepository().save(project);
        }
    }

    @Override
    public Component getOrCreateComponent(String projectKey, String componentName)
    {
        log.trace("Getting or creating component {}/{}", projectKey, componentName);
        Optional<Component> existingComponent = this.context.getComponentRepository().findByName(componentName);
        if (existingComponent.isPresent())
        {
            log.debug("Found existing component with id {}", existingComponent.get().getId());
            return existingComponent.get();
        }
        else
        {
            log.debug("No component found, creating it");
            Project project = getOrCreateProject(projectKey);
            Component component = Component.ofName(componentName)
                    .belongsTo(project)
                    .build();

            return this.context.getComponentRepository().save(component);
        }
    }
}
