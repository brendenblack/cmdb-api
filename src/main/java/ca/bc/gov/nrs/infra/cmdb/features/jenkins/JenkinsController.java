package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api(value = "/jenkins", description = "Endpoints for loading and retrieving data from Jenkins", consumes = "application/json", produces = "application/json")
@RestController
@RequestMapping("/jenkins")
public class JenkinsController
{
    private static final Logger log = LoggerFactory.getLogger(JenkinsController.class);

    private final Mediator mediator;

    @Autowired
    JenkinsController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @ApiOperation(
            value = "Adds a build record for the specified component",
            response = AddBuild.Model.class
    )
    @PostMapping("/project/{project}/component/{component}")
    public void addBuild(@PathVariable String project,
                         @PathVariable String component,
                         @RequestBody AddBuild.Command message,
                         HttpServletResponse response)
    {
        message.setProjectKey(project);
        message.setComponentName(component);

        AddBuild.Model result = this.mediator.send(message, AddBuild.Model.class);

        String path = new StringBuilder()
                .append("/project/")
                .append(result.getProjectKey())
                .append("/component/")
                .append(result.getComponentName())
                .append("/build/")
                .append(result.getBuildNumber())
                .toString();

        response.setHeader("Location", path);
        response.setStatus(HttpStatus.CREATED.value());
    }

    @ApiOperation(
            value = "Adds a promotion record for the specified component build",
            response = AddPromotion.Model.class
    )
    @PostMapping("/project/{project}/component/{component}/build/{buildNumber}")
    public void addPromotion(@PathVariable String project,
                             @PathVariable String component,
                             @PathVariable int buildNumber,
                             @RequestBody AddPromotion.Command message,
                             HttpServletResponse response)
    {
        message.setProjectKey(project);
        message.setComponentName(component);
        message.setBuildNumber(buildNumber);

        AddPromotion.Model result = this.mediator.send(message, AddPromotion.Model.class);

        String path = new StringBuilder()
                .append("/project/")
                .append(result.getProjectKey())
                .append("/component/")
                .append(result.getComponentName())
                .append("/build/")
                .append(result.getBuildNumber())
                .append("/promotion/")
                .append(result.getEnvironment())
                .append("/")
                .append(result.getPromotionNumber())
                .toString();

        response.setHeader("Location", path);
        response.setStatus(HttpStatus.CREATED.value());

    }
}
