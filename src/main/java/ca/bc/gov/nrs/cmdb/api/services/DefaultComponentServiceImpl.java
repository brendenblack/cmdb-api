package ca.bc.gov.nrs.cmdb.api.services;

import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.repositories.CmdbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class DefaultComponentServiceImpl implements ComponentService
{
    private final Logger log = LoggerFactory.getLogger(DefaultComponentServiceImpl.class);
    private final CmdbContext context;

    @Autowired
    DefaultComponentServiceImpl(CmdbContext context)
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
        Optional<Component> existingComponent = this.context.getComponentRepository().findByName(componentName);
        if (existingComponent.isPresent())
        {
            return existingComponent.get();
        }
        else
        {
            Project project = getOrCreateProject(projectKey);
            Component component = Component.ofName(componentName)
                    .belongsTo(project)
                    .build();

            return this.context.getComponentRepository().save(component);
        }
    }
}
