package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsBuildRepository;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

public class AddBuild
{
    @Getter
    @Setter
    @ToString
    @ApiModel("jenkinsAddBuildCommand")
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
        private String result = "";
        private String performedOn;
    }

    @Getter
    @Setter
    @ApiModel("jenkinsAddBuildModel")
    public static class Model
    {
        public long id;
        private String projectKey;
        private String componentName;
        private int buildNumber;
    }

    @Service
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
            final JenkinsBuildRepository repo = this.context.getJenkinsBuildRepository();

            log.debug("Looking up component {}/{}", message.getProjectKey(), message.getComponentName());
            Component component = this.irs.getOrCreateComponent(message.getProjectKey(), message.getComponentName());
            if (component == null)
            {
                throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create a component object with name " + message.getComponentName());
            }

            log.debug("Found component with id {}", component.getId());

            log.debug("Looking for existing build {} #{}", component.getName(), message.getNumber());
            Optional<JenkinsBuild> existingBuild = repo.findByComponentNameAndNumber(component.getName(), message.getNumber());

            if (existingBuild.isPresent())
            {
                log.warn("Unable to create a new build record for {}/{} #{} because one already exists with id {}",
                         message.getProjectKey(),
                         message.getComponentName(),
                         message.getNumber(),
                         existingBuild.get().getId());
                HttpException e = new HttpException(HttpStatus.CONFLICT, "A build record for " + message.getComponentName() + " #" + message.getNumber() + " already exists. Send a PATCH request to ___ to update it.");
                e.addHeader("Location", existingBuild.get().getId().toString());
                throw e;
            }

            log.debug("Found no existing build, will construct a new entry");
            // Safely convert the string value of the build result to an enum value
//            JenkinsBuild.Result buildResult = Optional
//                    .of(JenkinsBuild.Result.valueOf(message.getResult().trim().toUpperCase()))
//                    .orElse(JenkinsBuild.Result.UNKNOWN);
//
//            log.debug("Result is {}", buildResult);

            JenkinsBuild build = JenkinsBuild.of(component)
                    .number(message.getNumber())
                    .url(message.getUrl())
                    .startedAt(message.getStartedAt())
                    .took(message.getDuration())
                    .result(message.getResult())
                    .triggeredByUsername(null) // TODO
                    .ofJobType(message.getJobType())
                    .withDisplayName(message.getDisplayName())
                    .performedOn(null) // TODO
                    .queueId(message.getQueueId())
                    .build();

            log.debug("Constructed build: {}", build.toString());

            build = repo.save(build);

            log.debug("Persisted build with id {}", build.getId());

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
