package ca.bc.gov.nrs.infra.cmdb.features.irs.projects;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.features.irs.IrsRoutes;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class GetAll
{
    public static class Query
    {}

    @Getter
    @Setter
    @ApiModel("irsProjectGetAllModel")
    public static class Model
    {
        private List<ProjectModel> projects = new ArrayList<>();
    }

    @Getter
    @Setter
    @ApiModel("irsProjectGetAllProjectModel")
    public static class ProjectModel
    {
        private String key;
        private String link;
    }

    @Service
    public static class Handler implements RequestHandler<Query,Model>
    {
        private final Logger log = LoggerFactory.getLogger(Handler.class);

        private final CmdbContext context;

        @Autowired
        Handler(CmdbContext context)
        {
            this.context = context;
        }

        @Override
        public Model handle(Query message)
        {
            Model result = new Model();

            for (Project project : this.context.getProjectRepository().findAll())
            {
                ProjectModel p = new ProjectModel();
                p.setKey(project.getKey());
                p.setLink(IrsRoutes.makeLink(project));
                result.getProjects().add(p);
            }

            return result;
        }

        @Override
        public Class getRequestType()
        {
            return Query.class;
        }

        @Override
        public Class getReturnType()
        {
            return Model.class;
        }
    }
}
