package ca.bc.gov.nrs.infra.cmdb.features.servers;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class Create
{
    @Getter
    @Setter
    public static class Command
    {
        private String fqdn;
    }

    @Component("serverCreateHandler")
    public static class Handler implements RequestHandler<Command, Long>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final ServerRepository serverRepository;

        public Handler(ServerRepository serverRepository)
        {
            this.serverRepository = serverRepository;
        }

        @Override
        public Long handle(Command message)
        {
            Optional<Server> result = this.serverRepository.findByFqdn(message.getFqdn());
            if (result.isPresent())
            {
                long id = result.get().getId();
                HttpException e = new HttpException(HttpStatus.CONFLICT, "A server already exists with the address " + message.getFqdn());
                e.addHeader("Location", ServersController.PATH + "/" + id);
                throw e;
            }

            Server server = new Server();
            server.setFqdn(message.getFqdn());

            server = this.serverRepository.save(server);

            return server.getId();
        }

        @Override
        public Class getRequestType()
        {
            return Command.class;
        }

        @Override
        public Class getReturnType()
        {
            return Long.class;
        }
    }
}
