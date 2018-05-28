package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
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
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to bulk import build and promotion data obtained by running a gathering script on the target Jenkins system
 */
public class Import
{
    @Getter
    @Setter
    @ToString
    @ApiModel("jenkinsImportCommand")
    public static class Command
    {
        private List<ProjectModel> projects = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    public static class ProjectModel
    {
        private String name;
        private List<ComponentModel> components = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    public static class ComponentModel
    {
        private String componentName;
        private List<BuildModel> builds = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    public static class BuildModel
    {
        private long duration;
        private int number;
        private String result;
        private String triggeredBy;
        private String commitId;
        private long timestamp;
        private List<PromotionModel> promotions = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PromotionModel
    {
        private String environment;
        private int number;
        private long timestamp;
        private String result;
        private long duration;
        private String triggeredBy;
    }

    @Getter
    @Setter
    @ApiModel("jenkinsImportModel")
    public static class Model
    {
        private List<ResultModel> results = new ArrayList<>();

        public void updated(JenkinsBuild build)
        {
            ResultModel r = new ResultModel();
            r.setType(build.getClass().getName());
            r.setStatus("updated");
            r.setLink(JenkinsRoutes.makeLink(build));
            results.add(r);
        }

        public void created(JenkinsBuild build)
        {
            ResultModel r = new ResultModel();
            r.setType(build.getClass().getName());
            r.setStatus("created");
            r.setLink(JenkinsRoutes.makeLink(build));
            results.add(r);
        }

        public void updated(JenkinsPromotion promotion)
        {
            ResultModel r = new ResultModel();
            r.setType(promotion.getClass().getName());
            r.setStatus("updated");
            r.setLink(JenkinsRoutes.makeLink(promotion));
            results.add(r);
        }

        public void created(JenkinsPromotion promotion)
        {
            ResultModel r = new ResultModel();
            r.setType(promotion.getClass().getName());
            r.setStatus("created");
            r.setLink(JenkinsRoutes.makeLink(promotion));
            results.add(r);
        }

    }

    @Getter
    @Setter
    @ToString
    public static class ResultModel
    {
        private String status;
        private String type;
        private String link;
    }

    @Service
    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);

        private final CmdbContext context;
        private final InfrastructureRegistrationService irs;

        // caching to help with larger requests
        private Map<String,Server> serverMap = new HashMap<>();
        private Map<String,IdirUser> usersMap = new HashMap<>();

        @Autowired
        Handler(CmdbContext context, InfrastructureRegistrationService irs)
        {
            this.context = context;
            this.irs = irs;
        }

        @Override
        public Model handle(Command message)
        {
            log.info("Received command to add or update {} projects", message.getProjects().size());

            Model result = new Model();


            for (ProjectModel prj : message.getProjects())
            {
                Project project = this.irs.getOrCreateProject(prj.getName());
                for (ComponentModel c : prj.getComponents())
                {
                    Component component = this.irs.getOrCreateComponent(project, c.getComponentName());
                    log.debug(component.toString());
                    for (BuildModel b : c.getBuilds())
                    {
                        Optional<JenkinsBuild> existingBuild = this.context.getJenkinsBuildRepository()
                                .findByComponentNameAndNumber(component.getName(), b.getNumber());

                        if (existingBuild.isPresent())
                        {
                            log.warn("Build #{} of {} unexpectedly exists", existingBuild.get().getNumber(), existingBuild.get().getComponent().getName());
                            // TODO: update
                            result.updated(existingBuild.get());
                        }
                        else
                        {
                            String url = new StringBuilder()
                                    .append("job/")
                                    .append(project.getKey())
                                    .append("/job/")
                                    .append(component.getName())
                                    .append("/")
                                    .append(b.getNumber())
                                    .toString();

                            log.debug(url);

                            // TODO: convert username to User

                            JenkinsBuild build = JenkinsBuild.of(component)
                                    .number(b.getNumber())
                                    .url(url)
                                    .startedAt(b.getTimestamp())
                                    .took(b.getDuration())
                                    .result(b.getResult())
                                    .triggeredByUsername(b.getTriggeredBy())
                                    .build();

                            log.debug(build.toString());

                            this.context.getJenkinsBuildRepository().save(build);
                            result.created(build);

                            for (PromotionModel p : b.getPromotions())
                            {
                                log.trace(p.toString());
                                Optional<JenkinsPromotion> existingPromotion = this.context.getJenkinsPromotionRepository().findPromotion(
                                        component.getName(),
                                        build.getNumber(),
                                        p.getEnvironment(),
                                        p.getNumber());


                                if (existingPromotion.isPresent())
                                {
                                    log.debug("Promotion {}#{} already exists with id {}", p.getEnvironment(), p.getNumber(), existingPromotion.get().getId());
                                    // TODO: update
                                    result.updated(existingBuild.get());
                                }
                                else
                                {
                                    log.debug("Promotion {}#{} does not yet exist", p.getEnvironment(), p.getNumber());

                                    String promotionUrl = "";
                                    JenkinsPromotion promotion = JenkinsPromotion.of(build)
                                        .environment(p.getEnvironment())
                                        .number(p.getNumber())
                                        .url(promotionUrl)
                                        .startedAt(p.getTimestamp())
                                        .took(p.getDuration())
                                        .result(p.getResult())
                                        .triggeredByUsername(p.getTriggeredBy())
                                        .build();

                                    this.context.getJenkinsPromotionRepository().save(promotion);

                                    result.created(promotion);
                                }
                            }
                        }
                    }
                }
            }

            return result;
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
