package ca.bc.gov.nrs.cmdb.api.features.servers;

import ca.bc.gov.nrs.cmdb.api.infrastructure.HttpException;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequest;
import ca.bc.gov.nrs.cmdb.api.mediator.IRequestHandler;
import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
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

    @Getter
    @Setter
    public static class FileSystemModel
    {
        private String mountedOn;
        private String freeSpace;
        private String totalSpace;
        private String usedSpace;
        private String type;
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
                    fsmodel.setUsedSpace(FileUtils.byteCountToDisplaySize(fs.getUsed()));
                    fsmodel.setFreeSpace(FileUtils.byteCountToDisplaySize(fs.getAvailable()));
                    fsmodel.setTotalSpace(FileUtils.byteCountToDisplaySize(fs.getSize()));
                    fsmodel.setType(fs.getType());
                }

                ServerEnvelope envelope = new ServerEnvelope();
                envelope.setServer(model);
                return envelope;
            }
            else
            {
                log.warn("No server was found with id {}", message.getId());
                throw new HttpException(HttpStatus.NOT_FOUND, "No server was found with id " + message.getId());
            }
        }

        public static String humanReadableByteCount(long bytes, boolean si)
        {
            // https://stackoverflow.com/a/3758880
            int unit = si ? 1000 : 1024;
            if (bytes < unit)
            {
                return bytes + " B";
            }

            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
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
