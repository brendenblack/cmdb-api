package ca.bc.gov.nrs.infra.cmdb.features.irs;

import ca.bc.gov.nrs.infra.cmdb.features.irs.projects.Add;
import ca.bc.gov.nrs.infra.cmdb.features.irs.projects.GetAll;
import ca.bc.gov.nrs.infra.cmdb.features.jenkins.AddBuild;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpExceptionModel;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Api(value = "/irs",
     tags = "Infrastructure registration",
     description = "Manage the logical components that are hosted in IRS",
     consumes = "application/json",
     produces = "application/json")
@RestController
public class InfrastructureRegistrationController
{
    private final Mediator mediator;

    @Autowired
    public InfrastructureRegistrationController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @ApiOperation(tags = "Add project",
            value = "Creates a record of a project",
            response = AddBuild.Model.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", responseHeaders = @ResponseHeader(name = "Location", description = "Where to find details about the created project", response = String.class)),
            @ApiResponse(code = 400, message = "Bad request", response = HttpExceptionModel.class),
            @ApiResponse(code = 409, message ="Already exists", response = HttpExceptionModel.class) })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(IrsRoutes.POST_PROJECT_ROUTE)
    public void addProject(@ApiParam(name = "Parameters", value = "The unique key assigned to the project", type = "body", required = true) @RequestBody Add.Command message, HttpServletResponse response)
    {
        Add.Model model = this.mediator.send(message, Add.Model.class);

        response.setHeader("Location", model.getLink());
    }


    @ApiOperation(tags = "Add component", value = "Creates a component record")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", responseHeaders = @ResponseHeader(name = "Location", description = "Where to find details about the created component", response = String.class)),
            @ApiResponse(code = 400, message = "Bad request", response = HttpExceptionModel.class),
            @ApiResponse(code = 409, message ="Already exists", response = HttpExceptionModel.class) })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(IrsRoutes.POST_COMPONENT_ROUTE)
    public void addComponent(@ApiParam(value = "The unique key assigned to the project that this component belongs to", type = "param", required = true) @PathVariable(IrsRoutes.PROJECT_KEY_PLACEHOLDER) String projectKey,
                             @RequestBody ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Command message,
                             HttpServletResponse response)
    {
        message.setProjectKey(projectKey);

        ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Model result = this.mediator.send(message, ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Model.class);

        response.setHeader("Location", result.getLink());
    }

    @ApiOperation(value = "List all projects")
    @ApiResponse(code = 200, message = "Success", response = GetAll.Model.class)
    @GetMapping("/irs/projects")
    public GetAll.Model listProjects()
    {
        GetAll.Model result = this.mediator.send(new GetAll.Query(), GetAll.Model.class);

        return result;
    }



}
