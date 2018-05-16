package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.WebSocketConfiguration;
import ca.bc.gov.nrs.cmdb.api.repositories.*;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * A callback implementation that will pass crawl updates to a Websocket topic, and defer persistence concerns to
 * {@link SilentPersistingCallback}
 */
@Component("websocketCrawlCallback")
public class WebsocketCallback extends SilentPersistingCallback implements CrawlCallback
{
    private static final Logger log = LoggerFactory.getLogger(WebsocketCallback.class);

    private final SimpMessagingTemplate template;

    @Autowired
    public WebsocketCallback(ServerRepository serverRepository,
                             OperatingSystemRepository operatingSystemRepository,
                             ProjectRepository projectRepository,
                             ComponentRepository componentRepository,
                             ComponentInstanceRepository componentInstanceRepository,
                             SimpMessagingTemplate template)
    {
        super(serverRepository, operatingSystemRepository, projectRepository, componentRepository, componentInstanceRepository);
        this.template = template;
    }

    @Override
    public void sendMessage(CrawlStatusMessage message)
    {
        ServerDto serverDto = new ServerDto();
        if (message.getServer() != null)
        {
            serverDto.setFqdn(message.getServer().getFqdn());
            serverDto.setId(message.getServer().getId());
        }

        CrawlUpdateDto dto = new CrawlUpdateDto();
        dto.setServer(serverDto);
        dto.setCrawlId(message.getCrawlId());
        dto.setStatus(message.getLevel());
        dto.setMessage(message.getMessage());

        String path = WebSocketConfiguration.BROKER_ROOT_PATH + "/crawl/" + message.getCrawlId();

        log.debug("Sending message [crawlId: {}] [server: {}] [status: {}] [message: {}] [path: {}]",
                  dto.getCrawlId(),
                  dto.getServer().getFqdn(),
                  dto.getStatus(),
                  dto.getMessage(),
                  path);

        template.convertAndSend(path, dto);
    }

    @Getter
    @Setter
    public class CrawlUpdateDto
    {
        private ServerDto server;
        private String crawlId;
        private String message;
        private String status;
    }

    @Getter
    @Setter
    public class ServerDto
    {
        private String fqdn;
        private long id;
    }
}
