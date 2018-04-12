package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.models.Build;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.repositories.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildService
{
    private final CmdbContext context;

    @Autowired
    public BuildService(CmdbContext context)
    {
        this.context = context;
    }

    public Builder builder()
    {
        return new Builder(this.context);
    }

    public Builder buildOf(String componentName)
    {
        Builder builder = new Builder(this.context);
        builder.ofComponent(componentName);
        return builder;
    }

    public Builder buildOf(Component component)
    {
        Builder builder = new Builder(this.context);
        builder.ofComponent(component);
        return builder;
    }


    public static class Builder
    {

        private final CmdbContext context;

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
        private int buildNumber;

        private Builder(CmdbContext context)
        {
            this.context = context;
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

        public Builder withBuildNumber(int buildNumber)
        {
            this.buildNumber = buildNumber;
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

        public Build build()
        {

            if (this.url == null)
            {
                throw new IllegalStateException("Cannot create a Build record without a valid URL");
            }

            Build build = this.context.getBuildRepository()
                    .findByUrl(this.url)
                    .orElse(new Build());

            build.setUrl(this.url);
            build.setDisplayName(this.displayName);
            build.setDuration(this.duration);
            build.setJobClass(this.jobClass);
            build.setNumber(this.buildNumber);
            build.setTimestamp(this.timestamp);
            build.setQueueId(this.queueId);

            Server server = this.context.getServerRepository()
                    .findByFqdn(this.builtOn)
                    .orElse(new Server());

            if (StringUtils.isBlank(server.getFqdn()))
            {
                server.setFqdn(this.builtOn);
            }

            build.setServer(server);

            return build;
        }
    }

}
