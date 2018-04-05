package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.infrastructure.HttpException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.Secret;
import ca.bc.gov.nrs.cmdb.api.repositories.SecretsRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

public class PasswordFetch
{
    @Getter
    @Setter
    public static class Command implements IRequest
    {
        private long secretId;
    }

    public static class Model
    {

    }

    @Service("passwordFetchHandler")
    public static class Handler implements IRequestHandler<Command, Model>
    {
        private final SecretsRepository secretsRepository;

        @Autowired
        public Handler(SecretsRepository secretsRepository)
        {

            this.secretsRepository = secretsRepository;
        }

        @Override
        public Model handle(Command message)
        {
            Optional<Secret> secret = this.secretsRepository.findById(message.getSecretId());
            if (!secret.isPresent())
            {
                throw new HttpException("Unable to find a secret with id " + message.getSecretId());
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
