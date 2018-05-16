package ca.bc.gov.nrs.infra.cmdb.features.secrets;

import ca.bc.gov.nrs.infra.cmdb.mediator.IRequest;
import ca.bc.gov.nrs.infra.cmdb.mediator.RequestHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class GetAll
{
    public static class Query implements IRequest
    {}

    public static class SecretsEnvelope
    {
        List<SecretModel> secrets = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class SecretModel
    {
        private long id;
        private String username;
        private String description;
    }

    @Component("secrestsGetAllHandler")
    public static class Handler implements RequestHandler<Query, SecretsEnvelope>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);



        @Override
        public SecretsEnvelope handle(Query message)
        {
            return null;
        }

        @Override
        public Class getRequestType()
        {
            return Query.class;
        }

        @Override
        public Class getReturnType()
        {
            return SecretsEnvelope.class;
        }
    }
}
