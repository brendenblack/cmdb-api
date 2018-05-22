package ca.bc.gov.nrs.infra.cmdb.features.load.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

public class Import
{
    public static class Command
    {

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

        // caching to help with larger requests
        private Map<String,Server> serverMap = new HashMap<>();
        private Map<String,IdirUser> usersMap = new HashMap<>();

        @Autowired
        Handler(CmdbContext context, InfrastructureRegistrationService irs)
        {
            this.context = context;
            this.irs = irs;
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
