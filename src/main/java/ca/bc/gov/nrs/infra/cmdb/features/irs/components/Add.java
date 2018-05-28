package ca.bc.gov.nrs.infra.cmdb.features.irs.components;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.features.irs.IrsRoutes;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public class Add
{
    @Getter
    @Setter
    @ApiModel("irsComponentAddCommand")
    public static class Command
    {
        private String projectKey;
        private String name;
    }

    @Getter
    @Setter
    @ApiModel("irsComponentAddModel")
    public static class Model
    {
        private String link;
    }

    public static class Handler implements RequestHandler<Command,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);
        private final CmdbContext context;

        @Autowired
        public Handler(CmdbContext context)
        {
            this.context = context;
        }


        @Override
        public Model handle(Command message)
        {
            log.info("Received request to add component with name {}", message.getName());

            Optional<Project> project = this.context.getProjectRepository().findByKey(message.getProjectKey());
            if (!project.isPresent())
            {
                log.warn("No project found with key {}, unable to create component {}", message.getProjectKey(), message.getName());
                throw new HttpException(HttpStatus.BAD_REQUEST, "No project found with key " + message.getProjectKey() + ", unable to created component");
            }

            Component component = Component.ofName(message.getName())
                    .belongsTo(project.get())
                    .build();

            this.context.getComponentRepository().save(component);

            Model result = new Model();
            result.setLink(IrsRoutes.makeGetComponentLink(component));
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
