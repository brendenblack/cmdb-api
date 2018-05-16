package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.IRequest;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsBuildRepository;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.ComponentRepository;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.ProjectRepository;
import ca.bc.gov.nrs.infra.cmdb.domain.services.jenkins.DefaultJenkinsServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
//    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public static class Handler implements RequestHandler<Command, Model>
    {
        private final static Logger log = LoggerFactory.getLogger(Handler.class);

        private final ProjectRepository projectRepository;
        private final ComponentRepository componentRepository;
        private final DefaultJenkinsServiceImpl buildService;
        private final JenkinsClient jenkinsClient;
        private final JenkinsBuildRepository buildRepository;

        @Autowired
        public Handler(JenkinsClient jenkinsClient,
                       ProjectRepository projectRepository,
                       ComponentRepository componentRepository,
                       JenkinsBuildRepository buildRepository,
                       DefaultJenkinsServiceImpl buildService)
        {
            this.jenkinsClient = jenkinsClient;
            this.buildRepository = buildRepository;
            this.projectRepository = projectRepository;
            this.componentRepository = componentRepository;
            this.buildService = buildService;
        }


        @Override
        public Model handle(Command message)
        {
            BuildInfo buildInfo;
            try
            {
                buildInfo = this.jenkinsClient.fetchBuildInfo(
                        message.getStream(),
                        message.getProject(),
                        message.getComponent(),
                        message.getBuildNumber());
            }
            catch (IOException e)
            {
                log.error("An error occurred while fetching ");
                throw new HttpException(e);
            }

            Component component;
            Optional<Component> oComponent = this.componentRepository.findByName(message.getComponent());
            if (!oComponent.isPresent())
            {
                Project project;
                Optional<Project> oProject = this.projectRepository.findByAcronym(message.getProject());
                if (!oProject.isPresent())
                {
                    project = new Project();
                    project.setKey(message.getProject());
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

            JenkinsBuild builder = this.buildService.buildOf(component)
                    .builtOn(buildInfo.getBuiltOn())
                    .ofDuration(buildInfo.getDuration())
                    .ofJenkinsJobType(buildInfo.getJobClass())
                    .startedAt(buildInfo.getTimestamp())
                    .ofDuration(buildInfo.getDuration())
                    .withBuildUrl(buildInfo.getUrl())
                    .withDisplayName(buildInfo.getDisplayName())
                    .withQueueId(buildInfo.getQueueId())
                    .build();


            // console output?
            // https://apps.nrs.gov.bc.ca/int/jenkins/job/AQUA/job/aqua-as-cfg/4/logText/progressiveText?start=0
            // logText/progressiveText?start=0



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
