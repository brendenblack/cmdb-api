package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.infrastructure.HttpException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.repositories.ComponentRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        @Autowired
        public Handler(@Value("${cmdb.jenkins.url}") String jenkinsUrl,
                       @Value("${cmdb.jenkins.username}") String username,
                       @Value("${cmdb.jenkins.password}") String password,
                       ProjectRepository projectRepository,
                       ComponentRepository componentRepository)
        {

            this.jenkinsUrl = jenkinsUrl;
            this.username = username;
            this.password = password;
            this.projectRepository = projectRepository;
            this.componentRepository = componentRepository;
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
                    .append("/api/json&depth=1");

            String url = urlBuilder.toString();
            String buildInfoResponse = getBuildInfo(url, this.username, this.password);

            // https://apps.nrs.gov.bc.ca/int/jenkins/job/AQUA/job/aqua-as-cfg/4/logText/progressiveText?start=0
            // logText/progressiveText?start=0



            return null;
        }

        public String getBuildInfo(String url, String username, String password)
        {
            log.debug("Fetching build info from {} using username {}", url, username);

            return "";
        }

        public void readBuildInfoResponse(String response)
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                JsonNode json = mapper.readTree(response);
                //System.out.println(json);

                String jobClass = json.get("_class").asText();
                System.out.println("Class: " + jobClass);

                int duration = json.get("duration").asInt();
                System.out.println("Duration: " + duration);

                String displayName = json.get("displayName").asText();
                System.out.println("Display name: " + displayName);

                String url = json.get("url").asText();
                System.out.println("URL: " + url);

                int queueid = json.get("queueId").asInt();
                System.out.println("Queue id: " + queueid);

                int timestamp = json.get("timestamp").asInt();
                System.out.println("Timestamp: " + timestamp);

                String builtOn = json.get("builtOn").asText();
                System.out.println("Built on: " + builtOn);

                log.debug("Build [class: {}] [duration: {}] [display: {}] [url: {}] [queue id: {}] [timestamp: {}] [built on: {}]",
                          jobClass,
                          duration,
                          displayName,
                          url,
                          queueid,
                          timestamp,
                          builtOn);

                if (json.get("actions").isArray())
                {
                    System.out.println("Actions is an array");
                    for (final JsonNode node : json.get("actions"))
                    {
                        if (node.has("_class"))
                        {
                            String actionClass = node.get("_class").asText();
                            System.out.println("Action class: " + actionClass);

                            switch (actionClass)
                            {
                                case "hudson.model.CauseAction":
                                    for (final JsonNode n : node.get("causes"))
                                    {
                                        if (n.get("_class").asText().equals("hudson.model.Cause$UserIdCause"))
                                        {
                                            String userId = n.get("userId").asText();
                                            System.out.println("User id: " + userId);
                                        }
                                    }
                                    break;
                                case "hudson.model.ParametersAction":
                                    Map<String,String> branches = new HashMap<>();
                                    for (final JsonNode n : node.get("parameters"))
                                    {
                                        switch (n.get("_class").asText())
                                        {
                                            case "hudson.model.StringParameterValue":
                                                switch (n.get("name").asText())
                                                {
                                                    case "targetBranch":
                                                        branches.put("targetBranch", n.get("value").asText());
                                                        break;
                                                    case "sourceBranch":
                                                        branches.put("sourceBranch", n.get("value").asText());
                                                        break;
                                                    default:
                                                        log.warn("Unexpected String parameter of {}", n.get("name").asText());
                                                        break;
                                                }
                                                String name = n.get("name").asText();
                                                String value = n.get("value").asText();
                                                System.out.println("Parameter [name: " + name + "] [value: " + value + "]");
                                                break;
                                        }
                                    }
                                    System.out.println(branches);
                                    break;
                                case "hudson.plugins.git.util.BuildData":
                                    break;
                                default:
                                    break;

                            }
                        }
                        // System.out.println(node);
//                        log.info("Action class: {}", node.get("_class").asText());
                    }
                }


            }
            catch (IOException e)
            {
                log.error("An error occurred while unmarshalling response string: {}", response, e);
                throw new HttpException(e);
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
