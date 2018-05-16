package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.domain.services.jenkins.JenkinsService;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.*;
import java.util.stream.Collectors;

public class UpsertBuildRecords
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

    public static class Model
    {

    }

    @Service
    @RequestScope
    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);

        private final CmdbContext context;
        private final InfrastructureRegistrationService irs;
        private final JenkinsService jenkinsService;

        // caching to help with larger requests
        private Map<String,Server> serverMap = new HashMap<>();
        private Map<String,IdirUser> usersMap = new HashMap<>();

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
            // database where the key is the project key and the value is a set of component names
            Map<String,Set<String>> requestedComponents = getProjectMap(message.getBuilds());

            Map<String,Component> componentMap = new HashMap<>();
            for (Map.Entry<String,Set<String>> entry : requestedComponents.entrySet())
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
                        Server server = serverMap.get(""); // TODO
                        IdirUser user = usersMap.get(b.getTriggeredBy());

                        JenkinsBuild.Result buildResult = Optional
                                .of(JenkinsBuild.Result.valueOf(b.getResult().trim().toUpperCase()))
                                .orElse(JenkinsBuild.Result.UNKNOWN);


                        Optional<JenkinsBuild> existingBuild = this.context.getBuildRepository().findByComponentAndNumber(component, b.getNumber());

                        if (existingBuild.isPresent())
                        {
                            // TODO: update
                        }
                        else
                        {
                            JenkinsBuild build = JenkinsBuild.of(component)
                                    .number(b.getNumber())
                                    .url(b.getUrl())
                                    .startedAt(b.getStartedAt())
                                    .took(b.getDuration())
                                    .result(buildResult)
                                    .triggeredBy(null)
                                    .ofJobType(b.getJobType())
                                    .withDisplayName(b.getDisplayName())
                                    .performedOn(server)
                                    .queueId(b.getQueueId())
                                    .build();

                            build = this.context.getBuildRepository().save(build);

                        }
                    }

                }
            }

            return null;
        }

        /**
         * Constructs a map of component names keyed by project key
         *
         * @param models
         * @return
         */
        public Map<String, Set<String>> getProjectMap(List<AddBuildModel> models)
        {
            Map<String,Set<String>> map = new HashMap<>();

            for (AddBuildModel b : models)
            {
                if (!map.containsKey(b.getProjectKey()))
                {
                    map.put(b.getProjectKey(), new HashSet<>());
                }

                map.get(b.getProjectKey()).add(b.getComponentName());
            }

            return map;
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
