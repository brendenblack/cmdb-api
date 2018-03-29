package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.cmdb.api.infrastructure.WebSocketConfiguration;
import ca.bc.gov.nrs.cmdb.api.repositories.OperatingSystemRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * A callback implementation that will pass crawl updates to a Websocket topic, and defer persistence concerns to
 * {@link PersistingCrawlCallback}
 */
@Component("crawlWebsocketCallback")
public class CrawlWebsocketCallback extends PersistingCrawlCallback implements CrawlCallback
{
    private static final Logger log = LoggerFactory.getLogger(CrawlWebsocketCallback.class);

    private final SimpMessagingTemplate template;

    @Autowired
    public CrawlWebsocketCallback(SimpMessagingTemplate template,
                                  ServerRepository serverRepository,
                                  OperatingSystemRepository operatingSystemRepository)
    {
        super(serverRepository, operatingSystemRepository);
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
