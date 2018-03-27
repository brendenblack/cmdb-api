package ca.bc.gov.nrs.cmdb.api.features.servers;

import ca.bc.gov.nrs.cmdb.api.infrastructure.RestException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Get
{
    @Getter
    @Setter
    public static class Query implements IRequest
    {
        private long id;
    }

    public static class ServerEnvelope
    {
        private ServerModel server;

        public ServerModel getServer()
        {
            return server;
        }

        public void setServer(ServerModel server)
        {
            this.server = server;
        }
    }

    @Getter
    @Setter
    public static class ServerModel
    {
        private long id;
        private String fqdn;
        private List<FileSystemModel> filesystems = new ArrayList<>();
    }

    public static class FileSystemModel
    {
        private String mountedOn;

        public String getMountedOn()
        {
            return mountedOn;
        }

        public void setMountedOn(String mountedOn)
        {
            this.mountedOn = mountedOn;
        }
    }

    @Component
    public static class Handler implements IRequestHandler<Query,ServerEnvelope>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final ServerRepository serverRepository;

        public Handler(ServerRepository serverRepository)
        {
            this.serverRepository = serverRepository;
        }

        public ServerEnvelope handle(Query message)
        {
            Optional<Server> result = this.serverRepository.findById(message.getId());

            if (result.isPresent())
            {
                Server server = result.get();
                ServerModel model = new ServerModel();
                model.setFqdn(server.getFqdn());
                model.setId(server.getId());

                for (FileSystem fs : server.getFileSystems())
                {
                    FileSystemModel fsmodel = new FileSystemModel();
                    fsmodel.setMountedOn(fs.getMountedOn());
                    model.getFilesystems().add(fsmodel);
                }

                ServerEnvelope envelope = new ServerEnvelope();
                envelope.setServer(model);
                return envelope;
            }
            else
            {
                log.warn("No server was found with id {}", message.getId());
                throw new RestException(HttpStatus.NOT_FOUND, "No server was found with id " + message.getId());
            }
        }

        @Override
        public Class getRequestType()
        {
            return Query.class;
        }

        @Override
        public Class getReturnType()
        {
            return ServerEnvelope.class;
        }

    }
}
