package ca.bc.gov.nrs.cmdb.api.features.secrets;

import ca.bc.gov.nrs.cmdb.api.infrastructure.RestException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.Secret;
import ca.bc.gov.nrs.cmdb.api.models.UsernamePasswordSecret;
import ca.bc.gov.nrs.cmdb.api.repositories.SecretsRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class Create
{
    @Getter
    @Setter
    public static class Command implements IRequest
    {
        private String type;

        private String username;
        private String password;

        private String description;
    }

    @Component("secretsCreateHandler")
    public static class Handler implements IRequestHandler<Command, Long>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final SecretsRepository secretsRepository;

        public Handler(SecretsRepository secretsRepository)
        {
            this.secretsRepository = secretsRepository;
        }


        @Override
        public Long handle(Command message)
        {
            log.info("Creating credential of type {}", message.getDescription());

            Secret secret;
            switch (message.getType())
            {
                case "USERNAMEPASSWORD":
                    secret = createUsernamePassword(message);
                    break;
                default:
                    log.warn("Unable to handle unknown secret type of '{}'", message.getType());
                    throw new RestException("Unknown secret type '" + message.getType() + "'");
            }

            secret.setDescription(message.getDescription());

            secret = this.secretsRepository.save(secret);
            return secret.getId();
        }

        public Secret createUsernamePassword(Command message)
        {
            UsernamePasswordSecret secret = new UsernamePasswordSecret();
            secret.setUsername(message.getUsername());
            secret.setPassword(message.getPassword());

            return secret;
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
