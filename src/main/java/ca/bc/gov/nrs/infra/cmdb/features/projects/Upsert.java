package ca.bc.gov.nrs.infra.cmdb.features.projects;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

public class Upsert
{
    @Getter
    @Setter
    @ApiModel("projectUpsertCommand")
    public static class Command
    {

    }

    @Getter
    @Setter
    @ApiModel("projectUpsertModel")
    public static class Model
    {

    }

    @Service
    public static class Handler implements RequestHandler<Command,Model>
    {

        @Override
        public Model handle(Command message)
        {
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
