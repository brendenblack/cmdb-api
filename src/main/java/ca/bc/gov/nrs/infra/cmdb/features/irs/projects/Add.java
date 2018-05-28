package ca.bc.gov.nrs.infra.cmdb.features.irs.projects;

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
import org.springframework.stereotype.Service;

import java.util.Optional;

public class Add
{

    @Getter
    @Setter
    @ApiModel("projectAddCommand")
    public static class Command
    {
        private String key;
    }

    @Getter
    @Setter
    @ApiModel("projectAddModel")
    public static class Model
    {
        private String link;
    }

    @Service("addProjectHandler")
    public static class Handler implements RequestHandler<Command, Model>
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
            log.info("Received request to create a project with key {}", message.getKey());
            Optional<Project> existingProject = this.context.getProjectRepository().findByKey(message.getKey());
            if (existingProject.isPresent())
            {
                log.warn("Unable to create a new project with key {} because it already exists with id {}",
                        existingProject.get().getKey(),
                        existingProject.get().getId());

                HttpException alreadyExistsException = new HttpException(HttpStatus.CONFLICT, "");
                alreadyExistsException.addHeader("Location", "");
                throw alreadyExistsException;
            }

            Project project = Project.withKey(message.getKey()).build();
            this.context.getProjectRepository().save(project);

            Model result = new Model();
            result.setLink(IrsRoutes.makeProjectLink(project));
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
