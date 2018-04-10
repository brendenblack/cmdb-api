package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.infrastructure.HttpException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.Build;
import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.repositories.ComponentRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.*;

public class GetBuildInfo
{
    @Getter
    @Setter
    public static class Command implements IRequest
    {
        private String stream;
        private String project;
        private String component;
        private int buildNumber;
    }

    public static class Model
    {

    }

    @Service
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public static class Handler implements IRequestHandler<Command, Model>
    {
        private final static Logger log = LoggerFactory.getLogger(Handler.class);

        private final String jenkinsUrl;
        private final String username;
        private final String password;
        private final ProjectRepository projectRepository;
        private final ComponentRepository componentRepository;
        private final BuildService buildService;

        @Autowired
        public Handler(@Value("${cmdb.jenkins.url}") String jenkinsUrl,
                       @Value("${cmdb.jenkins.username}") String username,
                       @Value("${cmdb.jenkins.password}") String password,
                       ProjectRepository projectRepository,
                       ComponentRepository componentRepository,
                       BuildService buildService)
        {

            this.jenkinsUrl = jenkinsUrl;
            this.username = username;
            this.password = password;
            this.projectRepository = projectRepository;
            this.componentRepository = componentRepository;
            this.buildService = buildService;
        }


        @Override
        public Model handle(Command message)
        {
            Component component;
            Optional<Component> oComponent = this.componentRepository.findByName(message.getComponent());
            if (!oComponent.isPresent())
            {
                Project project;
                Optional<Project> oProject = this.projectRepository.findByAcronym(message.getProject());
                if (!oProject.isPresent())
                {
                    project = new Project();
                    project.setAcronym(message.getProject());
                    project = this.projectRepository.save(project);
                }
                else
                {
                    project = oProject.get();
                }

                component = new Component();
                component.setName(message.getComponent());
                component.setProject(project);
                component = this.componentRepository.save(component);
            }
            else
            {
                component = oComponent.get();
            }

            BuildService.Builder builder = this.buildService.buildOf(component);


            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(this.jenkinsUrl);
            if (!StringUtils.isBlank(message.getStream()))
            {
              urlBuilder.append("/job/")
                      .append(message.getStream());
            }

            urlBuilder.append("/job/")
                    .append(component.getProject().getAcronym())
                    .append("/job/")
                    .append(component.getName())
                    .append("/")
                    .append(message.getBuildNumber())
                    .append("/api/json&depth=2");

            String url = urlBuilder.toString();
            String buildInfoResponse = getBuildInfo(url, this.username, this.password);
            BuildInfo buildInfo = readBuildInfoResponse(buildInfoResponse);

//            builder.ofJenkinsJobType(jobClass)
//                    .ofDuration(duration)
//                    .withDisplayName(displayName)
//                    .withBuildUrl(url)
//                    .startedAt(timestamp)
//                    .builtOn(builtOn)
            // https://apps.nrs.gov.bc.ca/int/jenkins/job/AQUA/job/aqua-as-cfg/4/logText/progressiveText?start=0
            // logText/progressiveText?start=0



            return null;
        }

        public String getBuildInfo(String url, String username, String password)
        {
            log.debug("Fetching build info from {} using username {}", url, username);

            return "";
        }

        public BuildInfo readBuildInfoResponse(String response)
        {
            BuildInfo build = new BuildInfo();
            ObjectMapper mapper = new ObjectMapper();
            try
            {

                JsonNode json = mapper.readTree(response);
                build.setJobClass(json.get("_class").asText());
                build.setDuration(json.get("duration").asInt());
                build.setDisplayName(json.get("displayName").asText());
                build.setUrl(json.get("url").asText());
                build.setQueueId(json.get("queueId").asInt());
                build.setTimestamp(json.get("timestamp").asInt());
                build.setBuiltOn(json.get("builtOn").asText());

                if (json.get("actions").isArray())
                {
                    for (final JsonNode node : json.get("actions"))
                    {
                        if (node.has("_class"))
                        {
                            String actionClass = node.get("_class").asText();
                            System.out.println("Action class: " + actionClass);

                            switch (actionClass)
                            {
                                case "hudson.model.CauseAction":
                                    build = readCauseAction(build, node);
                                    break;
                                case "hudson.model.ParametersAction":
                                    build = readParametersAction(build, node);
                                    break;
                                case "hudson.plugins.git.util.BuildData":
                                    build = readBuildData(build, node);
                                    break;
                                case "hudson.plugins.promoted_builds.PromotedBuildAction":
                                    build = readPromotedBuildAction(build, node);
                                    break;
                                default:
                                    Optional.of(node.get("_class").asText())
                                            .ifPresent(c -> log.warn("Unandled action type of {}", c));
                                    break;

                            }
                        }
                        // System.out.println(node);
//                        log.info("Action class: {}", node.get("_class").asText());
                    }
                }

                System.out.println(build.toString());
                return build;

            }
            catch (IOException e)
            {
                log.error("An error occurred while unmarshalling response string: {}", response, e);
                throw new HttpException(e);
            }
        }

        public BuildInfo readCauseAction(BuildInfo build, JsonNode node)
        {
            String expectedClass = "hudson.model.CauseAction";
            if (node.get("_class").asText().equals(expectedClass))
            {
                for (final JsonNode n : node.get("causes"))
                {
                    if (n.get("_class").asText().equals("hudson.model.Cause$UserIdCause"))
                    {
                        build.setTriggeredBy(n.get("userId").asText());
                    }
                }
            }
            else
            {
                log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
            }

            return build;
        }

        public BuildInfo readParametersAction(BuildInfo build, JsonNode node)
        {
            String expectedClass = "hudson.model.ParametersAction";
            if (node.get("_class").asText().equals(expectedClass))
            {
                for (final JsonNode n : node.get("parameters"))
                {
                    switch (n.get("_class").asText())
                    {
                        case "hudson.model.StringParameterValue":
                            switch (n.get("name").asText())
                            {
                                case "targetBranch":
                                    build.setTargetBranch(n.get("value").asText());
                                    break;
                                case "sourceBranch":
                                    build.setSourceBranch(n.get("value").asText());
                                    break;
                                default:
                                    log.warn("Unexpected String parameter of {} = {}",
                                             n.get("name").asText(),
                                             n.get("value").asText());
                                    break;
                            }
                            break;
                        default:
                            log.warn("Unhandled hudson.model.ParametersAction entry of type {}: {}", n.get("_class").asText(), n);
                            break;
                    }
                }
            }
            else
            {
                log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
            }

            return build;
        }

        public BuildInfo readBuildData(BuildInfo build, JsonNode node)
        {
            String expectedClass = "hudson.plugins.git.util.BuildData";

            if (node.get("_class").asText().equals(expectedClass))
            {
                Optional.of(node.get("buildsByBranchName"))
                        .map(n -> n.get("HEAD"))
                        .map(n -> n.get("marked"))
                        .ifPresent(n -> build.setSha1(n.get("SHA1").asText()));
            }
            else
            {
                log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
            }

            return build;

        }

        public BuildInfo readPromotedBuildAction(BuildInfo build, JsonNode node)
        {
            String expectedClass = "hudson.plugins.promoted_builds.PromotedBuildAction";

            if (node.get("_class").asText().equals(expectedClass))
            {
                for (final JsonNode promotion : node.get("promotions"))
                {
                    String name = promotion.get("name").asText();
                    for (final JsonNode promotionBuild : promotion.get("promotionBuilds"))
                    {
                        int number = promotionBuild.get("number").asInt();
                        log.debug("Promoted to {} - {}", name, number);
                    }
                }
            }
            else
            {
                log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
            }

            return build;
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

    @Getter
    @Setter
    @ToString
    public static class BuildInfo
    {
        private String jobClass;
        private int duration;
        private String displayName;
        private String url;
        private int queueId;
        private int timestamp;
        private String builtOn;
        private String triggeredBy;
        private String sourceBranch;
        private String targetBranch;
        private String sha1;
        private List<String> promotionUrls;
    }
}
