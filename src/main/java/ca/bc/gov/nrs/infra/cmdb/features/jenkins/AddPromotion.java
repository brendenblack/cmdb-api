package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsBuildRepository;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsPromotionRepository;
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

public class AddPromotion
{
    @Getter
    @Setter
    @ToString
    @ApiModel("jenkinsAddPromotionCommand")
    public static class Command
    {
        private String projectKey;
        private String componentName;

        private int buildNumber;
        private int promotionNumber;

        private String environmentName;
        private String url;
        private long startedAt;
        private long duration;
        private String triggeredBy;

        private String result = "";
    }

    @Getter
    @Setter
    @ApiModel("jenkinsAddPromotionModel")
    public static class Model
    {
        public long id;
        private String projectKey;
        private String componentName;
        private int buildNumber;
        private String environment;
        private int promotionNumber;
    }

    @Service
    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);
        private final CmdbContext context;
        private final InfrastructureRegistrationService irs;

        @Autowired
        Handler(CmdbContext context, InfrastructureRegistrationService irs)
        {
            this.context = context;
            this.irs = irs;
        }

        @Override
        public Model handle(Command message)
        {
            log.debug("Request to add promotion record: {}", message.toString());
            final JenkinsBuildRepository buildRepository = this.context.getJenkinsBuildRepository();
            Optional<JenkinsBuild> existingBuild = buildRepository.findByComponentNameAndNumber(message.getComponentName(), message.getBuildNumber());
            if (!existingBuild.isPresent())
            {
                log.warn("Unable to locate build {}/{} #{} which is required in order to create a record of promotion #{}",
                        message.getProjectKey(),
                        message.getComponentName(),
                        message.getBuildNumber(),
                        message.getPromotionNumber());
                throw new HttpException(HttpStatus.BAD_REQUEST, "Unable to find a record of the build that this record promotes, create it first and then try again");
            }

            JenkinsBuild build = existingBuild.get();

            final JenkinsPromotionRepository promotionRepository = this.context.getJenkinsPromotionRepository();
            Optional<JenkinsPromotion> existingPromotion = promotionRepository.findByComponentNameAndBuildNumberAndNumber(
                    message.getComponentName(),
                    message.getBuildNumber(),
                    message.getPromotionNumber());

            if (existingPromotion.isPresent())
            {
                String projectKey = Optional.of(existingPromotion.get())
                        .map(p -> p.getComponent())
                        .map(c -> c.getProject())
                        .map(p -> p.getKey())
                        .orElse("UNKNOWN");

                String componentName = Optional.of(existingPromotion.get())
                        .map(p -> p.getComponent())
                        .map(c -> c.getName())
                        .orElse("unknown");

                int buildNumber = Optional.of(existingPromotion.get())
                        .map(p -> p.getBuild())
                        .map(b -> b.getNumber())
                        .orElse(0);

                log.warn("Unable to create promotion #{} of build {}/{} #{} because it already exists with id {}",
                        existingPromotion.get().getNumber(),
                        projectKey,
                        componentName,
                        existingPromotion.get().getId());

                HttpException e = new HttpException(HttpStatus.CONFLICT, "Promotion " + message.getPromotionNumber() + " already exists with id " + existingPromotion.get().getId());
                e.addHeader("Location", "/promotions/" + projectKey + "/" + componentName + "/" + buildNumber + "/" + existingPromotion.get().getNumber());
            }


            JenkinsPromotion promotion = JenkinsPromotion.of(build)
                    .environment("0_INTEGRATION")
                    .number(message.getPromotionNumber())
                    .url(message.getUrl())
                    .startedAt(message.getStartedAt())
                    .took(message.getDuration())
                    .result(message.getResult())
                    .triggeredByUsername(message.getTriggeredBy())
                    .build();

            log.debug("Promotion id before save: {}", promotion.getId());

            promotionRepository.save(promotion);

            log.debug("Promotion id after save: {}", promotion.getId());

            Model model = new Model();
            model.setProjectKey(promotion.getProjectKey());
            model.setComponentName(promotion.getComponentName());
            model.setBuildNumber(promotion.getBuildNumber());
            model.setId(promotion.getId());
            return model;
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
