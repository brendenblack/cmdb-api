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

//        List<SuccessModel> added = new ArrayList<>();
//        List<SuccessModel> updated = new ArrayList<>();
//        List<ErrorModel> errors = new ArrayList<>();
//
//        public void addNewBuild(JenkinsBuild build)
//        {
//            SuccessModel model = new SuccessModel();
//
//            model.setProjectKey(Optional.of(build)
//                                        .map(b -> b.getComponent())
//                                        .map(c -> c.getProject()).map(p -> p.getKey())
//                                        .orElse("unknown"));
//
//            model.setComponentName(Optional.of(build)
//                                           .map(b -> b.getComponent())
//                                           .map(c -> c.getName())
//                                           .orElse("unknown"));
//
//            model.setNumber(Optional.of(build)
//                                    .map(b -> b.getNumber())
//                                    .orElse(-1));
//
//            this.added.add(model);
//        }
//
//        public void addUpdatedBuild(JenkinsBuild build)
//        {
//            SuccessModel model = new SuccessModel();
//
//            model.setProjectKey(Optional.of(build)
//                                        .map(b -> b.getComponent())
//                                        .map(c -> c.getProject())
//                                        .map(p -> p.getKey())
//                                        .orElse("unknown"));
//
//            model.setComponentName(Optional.of(build)
//                                           .map(b -> b.getComponent())
//                                           .map(c -> c.getName())
//                                           .orElse("unknown"));
//
//            model.setNumber(build.getNumber());
//
//            this.updated.add(model);
//        }
//
//        public void addError(String componentName, int number, String message)
//        {
//            ErrorModel model = new ErrorModel();
//            model.setComponentName(componentName);
//            model.setNumber(number);
//            model.setMessage(message);
//            errors.add(model);
//        }
    }

    @Getter
    @Setter
    public static class SuccessModel
    {
        private String projectKey;
        private String componentName;
        private int number;
        private String link;
    }

    @Getter
    @Setter
    public static class ErrorModel
    {
        private String componentName;
        private int number;
        private String message;
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
                    for (BuildModel b : c.getBuilds())
                    {
                        Optional<JenkinsBuild> existingBuild = this.context.getJenkinsBuildRepository()
                                .findByComponentNameAndNumber(component.getName(), b.getNumber());

                        if (existingBuild.isPresent())
                        {
                            log.warn("Build #{} of {} unexpectedly exists", existingBuild.get().getNumber(), existingBuild.get().getComponent().getName());
                            // TODO: update
                        }
                        else
                        {
                            String url = new StringBuilder()
                                    .append("job/")
                                    .append(component.getProject().getKey())
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
                                }
                            }
                        }

                    }
                }
            }



//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            // Get a listing of all components present in the command, which will limit the number of queries to the
//            // database. The map keys are project keys and the values are sets of component names belonging to that
//            // project
//            Map<String,Set<String>> requestedComponents = makeProjectMap(message.getBuilds());
//
//            Map<String,Component> componentMap = new HashMap<>(); // cache components that have already been pulled from the DB
//            for (Map.Entry<String,Set<String>> entry : requestedComponents.entrySet())
//            {
//                log.debug("Project {} has {} components represented", entry.getKey(), entry.getValue().size());
//                for (String componentName : entry.getValue())
//                {
//                    Component component = (componentMap.containsKey(componentName)) ?
//                            componentMap.get(componentName) : // first try to pull from the cache
//                            this.irs.getOrCreateComponent(entry.getKey(), componentName); // otherwise hit the DB
//
//                    log.debug("Resolved component name {} to component with id {}", componentName, component.getId());
//
//                    List<AddBuildModel> componentBuilds = message.getBuilds()
//                            .stream()
//                            .filter(b -> b.getComponentName().equalsIgnoreCase(componentName))
//                            .collect(Collectors.toList());
//
//                    log.debug("Component {} has {} associated builds to be added or updated",
//                              component.getName(),
//                              componentBuilds.size());
//
//                    for (AddBuildModel b : componentBuilds)
//                    {
//                        Server server = serverMap.get(""); // TODO
//                        IdirUser user = usersMap.get(b.getTriggeredBy()); // TODO
//
//
//                        Optional<JenkinsBuild> existingBuild = this.context
//                                .getJenkinsBuildRepository()
//                                .findByComponentNameAndNumber(component.getName(), b.getNumber());
//
//                        if (existingBuild.isPresent())
//                        {
//                            // TODO: update all mutable fields
//                            existingBuild.get().setDisplayName(b.getDisplayName());
//                            JenkinsBuild updatedBuild = this.context.getJenkinsBuildRepository().save(existingBuild.get());
//                            result.addUpdatedBuild(updatedBuild);
//                        }
//                        else
//                        {
//                            JenkinsBuild build = JenkinsBuild.of(component)
//                                    .number(b.getNumber())
//                                    .url(b.getUrl())
//                                    .startedAt(b.getStartedAt())
//                                    .took(b.getDuration())
//                                    .result(b.getResult())
//                                    .triggeredByUsername(b.getTriggeredBy())
//                                    .ofJobType(b.getJobType())
//                                    .withDisplayName(b.getDisplayName())
//                                    .performedOn(server)
//                                    .queueId(b.getQueueId())
//                                    .build();
//
//                            build = this.context.getJenkinsBuildRepository().save(build);
//                            log.debug("Saved new build with id {}", build.getId());
//                            result.addNewBuild(build);
//
//                        }
//                    }
//                }
//            }

            return result;
        }

        public IdirUser lookupUser(String username)
        {
            return null;
        }

        public Server lookupServer(String fqdn)
        {
            // TODO:
            // Hardcoding this value is a fragile approach, maybe a lookup of common names could be
            // implemented? For now, this covers the known uses cases
            if (!fqdn.endsWith(".bcgov"))
            {
                fqdn = fqdn + ".bcgov";
            }
            return null;
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
