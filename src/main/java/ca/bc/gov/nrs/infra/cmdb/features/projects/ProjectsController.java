package ca.bc.gov.nrs.infra.cmdb.features.projects;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/projects")
public class ProjectsController
{
    private final Mediator mediator;

    @Autowired
    public ProjectsController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @PostMapping
    public void add(Add.Command message, HttpServletResponse response)
    {
        Model model = this.mediator.send(message, Model.class);

        response.setStatus(HttpStatus.CREATED.value());
    }
}
