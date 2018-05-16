package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/load/jenkins")
public class JenkinsLoadController
{
    private static final Logger log = LoggerFactory.getLogger(JenkinsLoadController.class);

    private final Mediator mediator;

    @Autowired
    public JenkinsLoadController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @PutMapping("/password")
    public void triggerPasswordFetch(HttpServletResponse response)
    {
        this.mediator.send(new PasswordFetch.Command(), PasswordFetch.Model.class);
        response.setStatus(HttpStatus.ACCEPTED.value());
    }

    @PutMapping("/build/{stream}/{project}/{component}/{buildNumber}")
    public void getBuildInfo(@PathVariable String stream, @PathVariable String project, @PathVariable String component, @PathVariable int buildNumber)
    {
        GetBuildInfo.Command message = new GetBuildInfo.Command();
        message.setStream(stream);
        message.setProject(project);
        message.setComponent(component);
        message.setBuildNumber(buildNumber);

        GetBuildInfo.Model result = this.mediator.send(message, GetBuildInfo.Model.class);

    }

    @PutMapping("/build/{project}/{component}/{buildNumber}")
    public void getBuildInfo(@PathVariable String project, @PathVariable String component, @PathVariable int buildNumber)
    {
        GetBuildInfo.Command message = new GetBuildInfo.Command();
        message.setProject(project);
        message.setComponent(component);
        message.setBuildNumber(buildNumber);

        GetBuildInfo.Model result = this.mediator.send(message, GetBuildInfo.Model.class);
    }

    @PostMapping("/build/{project}/{component}")
    public void addBuildInfo(@PathVariable String project,
                             @PathVariable String component,
                             @RequestBody AddBuild.Command message,
                             HttpServletResponse response)
    {
        message.setProjectKey(project);
        message.setComponentName(component);

        AddBuild.Model result = this.mediator.send(message, AddBuild.Model.class);

        String path = "/build/" +
                project +
                "/" +
                component +
                "/" +
                result.getId();

        response.setHeader("Location", path);
        response.setStatus(HttpStatus.CREATED.value());
    }


}
