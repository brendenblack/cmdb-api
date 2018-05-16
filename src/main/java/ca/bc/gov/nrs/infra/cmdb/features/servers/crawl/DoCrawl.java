package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.mediator.IRequest;
import ca.bc.gov.nrs.infra.cmdb.mediator.RequestHandler;
import ca.bc.gov.nrs.infra.cmdb.models.Secret;
import ca.bc.gov.nrs.infra.cmdb.models.Server;
import ca.bc.gov.nrs.infra.cmdb.models.UsernamePasswordSecret;
import ca.bc.gov.nrs.infra.cmdb.repositories.SecretsRepository;
import ca.bc.gov.nrs.infra.cmdb.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Getter
    @Setter
    public static class Model
    {
        private Map<String,String> headers;
    }

    @Component("doCrawlHandler")
    public static class Handler implements RequestHandler<Command, Model>
    {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final ServerRepository serverRepository;
        private final SecretsRepository secretsRepository;
        private final CrawlManager crawlManager;

        @Autowired
        public Handler(ServerRepository serverRepository, SecretsRepository secretsRepository, CrawlManager crawlManager)
        {
            this.serverRepository = serverRepository;
            this.secretsRepository = secretsRepository;
            this.crawlManager = crawlManager;
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
                throw new HttpException(String.join(",", errors));
            }

            UsernamePasswordSecret cred = (UsernamePasswordSecret)secret.get();
            log.info("Will attempt to crawl {} with username {}", server.get().getFqdn(), cred.getUsername());

            Map<String,String> crawlHeaders = this.crawlManager.doCrawl(server.get(), cred);

            Model model = new Model();
            model.setHeaders(crawlHeaders);

            return model;
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