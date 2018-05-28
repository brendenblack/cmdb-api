package ca.bc.gov.nrs.infra.cmdb.features.irs;

import ca.bc.gov.nrs.infra.cmdb.features.irs.projects.Add;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(IrsRoutes.BASE)
public class InfrastructureRegistrationController
{
    private final Mediator mediator;

    @Autowired
    public InfrastructureRegistrationController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @PostMapping(IrsRoutes.POST_PROJECT_ROUTE)
    public void addProject(Add.Command message, HttpServletResponse response)
    {
        Add.Model model = this.mediator.send(message, Add.Model.class);

        response.setHeader("Location", model.getLink());
        response.setStatus(HttpStatus.CREATED.value());
    }

    @PostMapping(IrsRoutes.POST_COMPONENT_ROUTE)
    public void addComponent(@PathVariable(IrsRoutes.PROJECT_KEY_PLACEHOLDER) String projectKey,
                             @RequestBody ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Command message,
                             HttpServletResponse response)
    {
        message.setProjectKey(projectKey);

        ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Model result = this.mediator.send(message, ca.bc.gov.nrs.infra.cmdb.features.irs.components.Add.Model.class);

        response.setStatus(HttpStatus.CREATED.value());
        response.setHeader("Location", result.getLink());
    }
}
