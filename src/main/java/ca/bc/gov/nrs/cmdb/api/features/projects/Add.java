package ca.bc.gov.nrs.cmdb.api.features.projects;

import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class Add
{
    public static class Command implements IRequest
    {

    }

    public static class Model
    {

    }

    @Service("addProjectHandler")
    public static class Handler implements IRequestHandler<Command, Model>
    {
        @Autowired
        public Handler()
        {

        }


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
