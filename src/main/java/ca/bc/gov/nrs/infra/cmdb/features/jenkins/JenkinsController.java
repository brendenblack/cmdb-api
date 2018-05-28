package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api(value = "/jenkins",
     tags = "Jenkins",
     description = "Endpoints for loading and retrieving data from Jenkins",
     consumes = "application/json",
     produces = "application/json")
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
    @PostMapping(JenkinsRoutes.POST_BUILD_ROUTE)
    public void addBuild(@PathVariable String projectKey,
                         @PathVariable String jobName,
                         @RequestBody AddBuild.Command message,
                         HttpServletResponse response)
    {
        message.setProjectKey(projectKey);
        message.setComponentName(jobName);

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
    public void addPromotion(@ApiParam(value = "The key of the target project") @PathVariable String project,
                             @ApiParam(value = "The component name") @PathVariable String component,
                             @ApiParam(value = "The Jenkins-assigned build number") @PathVariable int buildNumber,
                             @ApiParam(value = "Description of the Jenkins build event") @RequestBody AddPromotion.Command message,
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

    @ApiOperation(
            value = "Bulk import of data retrieved from Jenkins",
            response = Import.Model.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Import.Model.class)})
    @PostMapping("/import")
    public Import.Model importData(@ApiParam(required = true, name = "message", type = "object") @RequestBody Import.Command message)
    {
        return this.mediator.send(message, Import.Model.class);
    }
}
