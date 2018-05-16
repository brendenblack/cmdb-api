package ca.bc.gov.nrs.infra.cmdb.domain.services.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.domain.services.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultJenkinsServiceImpl implements JenkinsService
{
    private final Logger log = LoggerFactory.getLogger(DefaultJenkinsServiceImpl.class);

    private final CmdbContext context;
    private final InfrastructureRegistrationService irs;

    @Autowired
    DefaultJenkinsServiceImpl(CmdbContext context, InfrastructureRegistrationService irs)
    {
        this.context = context;
        this.irs = irs;
    }

    public Builder buildOf(String projectKey, String componentName)
    {
        Component component = this.irs.getOrCreateComponent(projectKey, componentName);
        return new Builder(component);
    }

    public Builder buildOf(Component component)
    {
        return new Builder(component);
    }



    public interface IBuildNumber
    {
        IBuild number(int number);
    }

    public interface IBuildOf
    {
        IBuildNumber buildOf(int number);
    }



    public interface IBuild
    {
        JenkinsBuild build();
    }

    public static class Builder implements IBuild
    {

        private Component component;
        private String jobClass;
        private long duration;
        private String displayName;
        private int queueId;
        private long timestamp;
        private String builtOn;
        private String url;
        private int buildNumber;
        private Server server;

        Builder(Component component)
        {
            this.component = component;
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
         * Sets the server that this build was performed on
         *
         * @param server
         * @return
         */
        public Builder builtOn(Server server)
        {
            this.server = server;
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

        public Builder startedAt(long timestamp)
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder withDisplayName(String displayName)
        {
            this.displayName = displayName;
            return this;
        }

        /**
         *
         * @throws IllegalStateException when required fields are missing (component, build number, url)
         * @return
         */
        @Override
        public JenkinsBuild build()
        {
            Map<String,String> validationErrors = new HashMap<>();

            if (this.url == null)
            {
                validationErrors.put("url", "Cannot create a Build record without a valid URL");
            }

            if (this.buildNumber <= 0)
            {
                validationErrors.put("buildNumber", "Build number must be a positive integer");
            }

            if (this.component == null)
            {
                validationErrors.put("component", "Unable to create a build record without a component being set");
            }

            if (validationErrors.size() > 0)
            {
                throw new ValidationException(JenkinsBuild.class, validationErrors);
            }

            JenkinsBuild build = new JenkinsBuild();

            return build;
        }
    }

}
