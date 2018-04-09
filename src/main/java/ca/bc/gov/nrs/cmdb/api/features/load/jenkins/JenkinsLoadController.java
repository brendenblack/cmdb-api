package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.mediator.Mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/load/jenkins")
public class JenkinsLoadController
{
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


}
