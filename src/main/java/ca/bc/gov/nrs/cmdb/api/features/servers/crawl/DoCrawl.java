package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.infrastructure.RestException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.Secret;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.models.UsernamePasswordSecret;
import ca.bc.gov.nrs.cmdb.api.repositories.SecretsRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoCrawl
{
    @Getter
    @Setter
    public static class Command implements IRequest
    {
        private long serverId;
        private long secretId;
    }

    public static class Model
    {

    }

    @Component("doCrawlHandler")
    public static class Handler implements IRequestHandler<Command, Model>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final ServerRepository serverRepository;
        private final SecretsRepository secretsRepository;

        @Autowired
        public Handler(ServerRepository serverRepository, SecretsRepository secretsRepository)
        {
            this.serverRepository = serverRepository;
            this.secretsRepository = secretsRepository;
        }

        @Override
        public Model handle(Command message)
        {
            List<String> errors = new ArrayList<>();
            Optional<Server> server = this.serverRepository.findById(message.getServerId());
            if (!server.isPresent())
            {
               errors.add("Unable to find a server with id " + message.getServerId());
            }

            Optional<Secret> secret = this.secretsRepository.findById(message.getSecretId());
            if (!secret.isPresent())
            {
                errors.add("Unable to find a secret with id " + message.getSecretId());
            }

            if (errors.size() > 0)
            {
                throw new RestException(String.join(",", errors));
            }

            String fqdn = server.get().getFqdn();
            UsernamePasswordSecret cred = (UsernamePasswordSecret)secret.get();

            log.info("Will attempt to crawl {} with username {}", fqdn, cred.getUsername());


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
