package ca.bc.gov.nrs.cmdb.api.features.servers;

import ca.bc.gov.nrs.cmdb.api.features.servers.crawl.DoCrawl;
import ca.bc.gov.nrs.cmdb.api.mediator.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/servers")
public class ServersController
{
    public static final String PATH = "/servers";

    private static final Logger log = LoggerFactory.getLogger(ServersController.class);

    private final Mediator mediator;

    @Autowired
    public ServersController( Mediator mediator)
    {
        this.mediator = mediator;
    }

    @GetMapping("/{serverId}")
    public Get.ServerEnvelope getServer(@PathVariable("serverId") long serverId)
    {
        log.debug("Executing #getServer handler with id {}", serverId);
        Get.Query message = new Get.Query();
        message.setId(serverId);

        return this.mediator.send(message, Get.ServerEnvelope.class);
    }

    @PutMapping("/{serverId}/crawl")
    public void doCrawlServer(@PathVariable("serverId") long serverId, @RequestBody DoCrawl.Command message, HttpServletResponse response)
    {
        message.setServerId(serverId);
        DoCrawl.Model result = this.mediator.send(message, DoCrawl.Model.class);

        response.setStatus(HttpStatus.ACCEPTED.value());
        response.setHeader("Location", result.getCrawlId());
    }

    @GetMapping
    public GetAll.ServersEnvelope getAllServers()
    {
        log.warn("Executing #getAllServers");
        GetAll.Query message = new GetAll.Query();

        return this.mediator.send(message, GetAll.ServersEnvelope.class);
    }

    @PostMapping
    public void createServer(@RequestBody Create.Command message, HttpServletResponse response)
    {
        long id = this.mediator.send(message, Long.class);

        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", PATH + "/" + id);
    }
}
