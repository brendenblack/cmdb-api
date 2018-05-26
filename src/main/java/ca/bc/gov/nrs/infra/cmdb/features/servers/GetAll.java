package ca.bc.gov.nrs.infra.cmdb.features.servers;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.ServerRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class GetAll
{
    public static class Query
    {
        public String fields;
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class ServersEnvelope
    {
        private List<ServerModel> servers = new ArrayList<>();
    }

    @Getter
    @Setter
    @JsonAutoDetect
    public static class ServerModel
    {
        private long id;
        private String fqdn;
    }

    @Component("serversGetAllHandler")
    public static class Handler implements RequestHandler<Query, ServersEnvelope>
    {
        private static Logger log = LoggerFactory.getLogger(Handler.class);
        private final ServerRepository serverRepository;

        @Autowired
        public Handler(ServerRepository serverRepository)
        {
            this.serverRepository = serverRepository;
        }

        @Override
        public ServersEnvelope handle(Query message)
        {
            List<ServerModel> models = new ArrayList<>();
            for (Server server : this.serverRepository.findAll())
            {
                ServerModel model = new ServerModel();
                model.setId(server.getId());
                model.setFqdn(server.getFqdn());
                models.add(model);
            }
            ServersEnvelope envelope = new ServersEnvelope();
            envelope.setServers(models);
            return envelope;
        }

        @Override
        public Class getRequestType()
        {
            return Query.class;
        }

        @Override
        public Class getReturnType()
        {
            return ServersEnvelope.class;
        }
    }
}
