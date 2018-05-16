package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.models.Build;
import ca.bc.gov.nrs.infra.cmdb.models.components.Component;
import ca.bc.gov.nrs.infra.cmdb.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.services.JenkinsService;
import ca.bc.gov.nrs.infra.cmdb.services.InfrastructureRegistrationService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

public class AddBuildRecord
{
    @Getter
    @Setter
    public static class Command
    {
        private List<AddBuildModel> builds = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class AddBuildModel
    {
        private String projectKey;
        private String componentName;
        private int number;

    }

    public static class Model
    {

    }

    @Service
    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);

        private final CmdbContext context;
        private final InfrastructureRegistrationService irs;
        private final JenkinsService jenkinsService;

        @Autowired
        public Handler(CmdbContext context,
                       InfrastructureRegistrationService irs,
                       JenkinsService jenkinsService)
        {
            this.context = context;
            this.irs = irs;
            this.jenkinsService = jenkinsService;
        }

        @Override
        public Model handle(Command message)
        {
            // Get a listing of all components present in the command, which will limit the number of queries to the
            // database
            Map<String,Set<String>> map = new HashMap<>();
            for (AddBuildModel b : message.getBuilds())
            {
                if (!map.containsKey(b.getProjectKey()))
                {
                    map.put(b.getProjectKey(), new HashSet<>());
                }

                map.get(b.getProjectKey()).add(b.getComponentName());
            }

            Map<String,Component> componentMap = new HashMap<>();
            for (Map.Entry<String,Set<String>> entry : map.entrySet())
            {
                for (String componentName : entry.getValue())
                {
                    Component component = this.irs.getOrCreateComponent(entry.getKey(), componentName);
                    log.debug("Resolved component name {} to component with id {}", componentName, component.getId());

                    List<AddBuildModel> componentBuilds = message.getBuilds()
                            .stream()
                            .filter(b -> b.getComponentName().equalsIgnoreCase(componentName))
                            .collect(Collectors.toList());

                    for (AddBuildModel b : componentBuilds)
                    {
                        Build build = this.jenkinsService.buildOf(component)
                            .withBuildNumber(b.getNumber())
                            .builtOn(b.getBuiltOn())
                            .ofDuration(buildInfo.getDuration())
                            .ofJenkinsJobType(buildInfo.getJobClass())
                            .startedAt(buildInfo.getTimestamp())
                            .ofDuration(buildInfo.getDuration())
                            .withBuildUrl(buildInfo.getUrl())
                            .withDisplayName(buildInfo.getDisplayName())
                            .withQueueId(buildInfo.getQueueId())
                            .build();
                    }

                }
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
