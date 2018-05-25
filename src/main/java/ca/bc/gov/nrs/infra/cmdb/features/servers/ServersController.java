package ca.bc.gov.nrs.infra.cmdb.features.servers;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.CmdbPermissions;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/servers")
@Api(value = "/servers", description = "Operations that manage compute node resources", tags = "Servers controller")
public class ServersController
{
    public static final String PATH = "/servers";

    private static final Logger log = LoggerFactory.getLogger(ServersController.class);

    private final Mediator mediator;

    @Autowired
    public ServersController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @ApiOperation(value = "Get a server by ID", notes = "Retrieve details about a server by its ID")
    @GetMapping("/{serverId}")
    public Get.ServerEnvelope getServer(@PathVariable("serverId") long serverId)
    {
        log.debug("Executing #getServer handler with id {}", serverId);
        Get.Query message = new Get.Query();
        message.setId(serverId);

        return this.mediator.send(message, Get.ServerEnvelope.class);
    }

//    @PostMapping("/crawl")
//    public void doCrawlServer(@RequestBody DoCrawl.Command message, HttpServletResponse response)
//    {
//        DoCrawl.Model result = this.mediator.send(message, DoCrawl.Model.class);
//
//        response.setStatus(HttpStatus.ACCEPTED.value());
//        for (String key : result.getHeaders().keySet())
//        {
//            response.addHeader(key, result.getHeaders().get(key));
//        };
//    }
//
//    @DeleteMapping("/crawl/{crawlId}")
//    public void cancelCrawl(@PathVariable String crawlId)
//    {
//        Cancel.Command message = new Cancel.Command();
//        message.setCrawlId(crawlId);
//
//        Cancel.Model result = this.mediator.send(message, Cancel.Model.class);
//    }

    @ApiOperation(value = "Get a server by ID", notes = "Retrieve details about a server by its ID")
    @GetMapping
    @Secured(CmdbPermissions.ROLE_READER)
    public GetAll.ServersEnvelope getAllServers()
    {
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
