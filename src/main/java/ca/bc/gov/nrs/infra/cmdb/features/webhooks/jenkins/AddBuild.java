package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsBuildRepository;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

public class AddBuild
{
    @Getter
    @Setter
    public static class Command
    {
        private String projectKey;
        private String componentName;
        private int number;
        private String url;
        private long startedAt;
        private long duration;
        private String triggeredBy;
        private int queueId;
        private String jobType;
        private String displayName;
        private String result;
        private String performedOn;

    }

    @Getter
    @Setter
    public static class Model
    {
        public long id;
        private String projectKey;
        private String componentName;
        private int buildNumber;
    }

    @Service
    @RequestScope
    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);
        private final CmdbContext context;
        private final InfrastructureRegistrationService irs;

        @Autowired
        public Handler(CmdbContext context,
                       InfrastructureRegistrationService irs)
        {
            this.context = context;
            this.irs = irs;
        }

        @Override
        public Model handle(Command message)
        {
            final JenkinsBuildRepository repo = this.context.getBuildRepository();

            Component component = this.irs.getOrCreateComponent(message.getProjectKey(), message.getComponentName());


            Optional<JenkinsBuild> existingBuild = repo.findByComponentAndNumber(component, message.getNumber());
            if (existingBuild.isPresent())
            {
                HttpException e = new HttpException(HttpStatus.CONFLICT, "A build record for " + message.getComponentName() + " #" + message.getNumber() + " already exists. Send a PATCH request to ___ to update it.");
                e.addHeader("Location", existingBuild.get().getId().toString());
            }

            // Safely convert the string value of the build result to an enum value
            JenkinsBuild.Result buildResult = Optional
                    .of(JenkinsBuild.Result.valueOf(message.getResult().trim().toUpperCase()))
                    .orElse(JenkinsBuild.Result.UNKNOWN);

            JenkinsBuild build = JenkinsBuild.of(component)
                    .number(message.getNumber())
                    .url(message.getUrl())
                    .startedAt(message.getStartedAt())
                    .took(message.getDuration())
                    .result(buildResult)
                    .triggeredBy(null) // TODO
                    .ofJobType(message.getJobType())
                    .withDisplayName(message.getDisplayName())
                    .performedOn(null) // TODO
                    .queueId(message.getQueueId())
                    .build();

            build = repo.save(build);

            log.trace("Constructed build: {}", build.toString());

            try
            {
                Model model = new Model();
                model.setId(build.getId());
                model.setProjectKey(build.getComponent().getProject().getKey());
                model.setComponentName(build.getComponent().getName());
                model.setBuildNumber(build.getNumber());
                return model;
            }
            catch (NullPointerException e)
            {
                log.error("A null pointer exception was encountered while constructing result model for creating build {} #{}",
                          message.getComponentName(),
                          message.getNumber(),
                          e);

                throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        @Override
        public Class getRequestType()
        {
            return Command.class;
        }

        @Override
        public Class getReturnType()
        {
            return Model.class;
        }
    }

}
