package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.repositories.ComponentRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class BuildService
{
    private final ComponentRepository componentRepository;
    private final ProjectRepository projectRepository;

    public BuildService(ComponentRepository componentRepository, ProjectRepository projectRepository)
    {

        this.componentRepository = componentRepository;
        this.projectRepository = projectRepository;
    }

    public Builder builder()
    {
        return new Builder(this.componentRepository, this.projectRepository);
    }

    public Builder buildOf(String componentName)
    {
        Builder builder = new Builder(this.componentRepository, this.projectRepository);
        builder.ofComponent(componentName);
        return builder;
    }

    public Builder buildOf(Component component)
    {
        Builder builder = new Builder(this.componentRepository, this.projectRepository);
        builder.ofComponent(component);
        return builder;
    }


    public static class Builder
    {
        private final ComponentRepository componentRepository;
        private final ProjectRepository repository;

        private Component component;
        private String componentName;
        private String projectKey;
        private String jobClass;
        private int duration;
        private String displayName;
        private int queueId;
        private int timestamp;
        private String builtOn;
        private String url;

        private Builder(ComponentRepository componentRepository, ProjectRepository repository)
        {
            this.componentRepository = componentRepository;
            this.repository = repository;
        }

        private Builder ofComponent(Component component)
        {
            this.component = component;
            return this;
        }

        private Builder ofComponent(String componentName)
        {
            this.componentName = componentName;
            return this;
        }

        public Builder inProject(String projectKey)
        {
            this.projectKey = projectKey;
            return this;
        }

        public Builder ofJenkinsJobType(String jobClass)
        {
            this.jobClass = jobClass;
            return this;
        }

        public Builder ofDuration(int duration)
        {
            this.duration = duration;
            return this;
        }

        /**
         * Sets the fully qualified domain name of the server this component was built on
         * @param fqdn
         * @return
         */
        public Builder builtOn(String fqdn)
        {
            this.builtOn = fqdn;
            return this;
        }

        public Builder withBuildUrl(String url)
        {
            this.url = url;
            return this;
        }

        public Builder withQueueId(int queueId)
        {
            this.queueId = queueId;
            return this;
        }

        public Builder startedAt(int timestamp)
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withDisplayName(String displayName)
        {
            this.displayName = displayName;
            return this;
        }
    }

}
