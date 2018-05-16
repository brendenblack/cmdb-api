package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.services.DefaultJenkinsServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class AddBuildInfo
{
    @Getter
    @Setter
    public static class Command
    {
        private String json;
        private String component;
        private String project;
    }

    @Setter
    @Getter
    public static class Model
    {
        private int buildNumber;
    }

    @Service
    public static class Handler implements RequestHandler<Command, Model>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final JenkinsClient jenkinsClient;
        private final CmdbContext context;
        private final DefaultJenkinsServiceImpl buildService;

        @Autowired
        public Handler(JenkinsClient jenkinsClient,
                       CmdbContext context,
                       DefaultJenkinsServiceImpl buildService)
        {
            this.jenkinsClient = jenkinsClient;
            this.context = context;
            this.buildService = buildService;
        }

        @Override
        public Model handle(Command message)
        {
            return null;
//            MinistryJenkinsClientImpl ministryJenkinsClient = (MinistryJenkinsClientImpl)this.jenkinsClient;
//            BuildInfo buildInfo = ministryJenkinsClient.readBuildInfoResponse(message.getJson());
//
//            Component component;
//            Optional<Component> oComponent = this.context.getComponentRepository()
//                    .findByName(message.getComponent());
//
//            if (!oComponent.isPresent())
//            {
//                Project project;
//                Optional<Project> oProject = this.context.getProjectRepository()
//                        .findByAcronym(message.getProject());
//
//                if (!oProject.isPresent())
//                {
//                    project = new Project();
//                    project.setKey(message.getProject());
//                    project = this.context.getProjectRepository()
//                            .save(project);
//                }
//                else
//                {
//                    project = oProject.get();
//                }
//
//                component = new Component();
//                component.setName(message.getComponent());
//                component.setProject(project);
//                component = this.context.getComponentRepository()
//                        .save(component);
//            }
//            else
//            {
//                component = oComponent.get();
//            }
//
//            Build build = this.buildService.buildOf(component)
//                    .withBuildNumber(buildInfo.getNumber())
//                    .builtOn(buildInfo.getBuiltOn())
//                    .ofDuration(buildInfo.getDuration())
//                    .ofJenkinsJobType(buildInfo.getJobClass())
//                    .startedAt(buildInfo.getTimestamp())
//                    .ofDuration(buildInfo.getDuration())
//                    .withBuildUrl(buildInfo.getUrl())
//                    .withDisplayName(buildInfo.getDisplayName())
//                    .withQueueId(buildInfo.getQueueId())
//                    .build();
//
//            build = this.context.getBuildRepository().save(build);
//
//            log.debug(build.toString());
//
//            Model model = new Model();
//            model.setBuildNumber(build.getNumber());
//            return model;
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
