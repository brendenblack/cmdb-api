package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsResult;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddBuild_HandlerTests
{
    private Logger log = LoggerFactory.getLogger(AddBuild_HandlerTests.class);

    @Autowired
    private AddBuild.Handler sut;

    @Autowired
    private CmdbContext context;

    @Autowired
    private InfrastructureRegistrationService irs;


    @Test
    public void validCommandReturnsId()
    {
        AddBuild.Command message = new AddBuild.Command();
        message.setComponentName("aqua-permit-api");
        message.setProjectKey("AQUA");
        message.setNumber(5);
        message.setDisplayName("AQUA/aqua-permit-api #5 1.0.0");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddBuild.Model response = this.sut.handle(message);

        assertThat("id", response.getId(), is(greaterThan(0L)));
    }


    @Test
    public void validCommandCreatesBuild()
    {
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        AddBuild.Command message = new AddBuild.Command();
        message.setComponentName("aqua-permit-api");
        message.setProjectKey("AQUA");
        message.setNumber(5);
        message.setDisplayName("AQUA/aqua-permit-api #5 1.0.0");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddBuild.Model response = this.sut.handle(message);
        Optional<JenkinsBuild> build = this.context.getJenkinsBuildRepository().findById(response.getId());

        assertThat(build.isPresent(), is(true));
    }

    @Test(expected = HttpException.class)
    public void existingBuildThrowsException()
    {
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild preExistingBuild = JenkinsBuild.of(component)
                .number(5)
                .url("http://example.org")
                .startedAt(100000L)
                .took(500L)
                .result(JenkinsResult.SUCCESS)
                .triggeredByUsername("user")
                .build();
        preExistingBuild = this.context.getJenkinsBuildRepository().save(preExistingBuild);
        log.debug("Saved a preexisting test build of {}/{} #{}",
                preExistingBuild.getComponent().getProject().getKey(),
                preExistingBuild.getComponent().getName(),
                preExistingBuild.getNumber());
        AddBuild.Command message = new AddBuild.Command();
        message.setComponentName(preExistingBuild.getComponent().getName());
        message.setProjectKey(preExistingBuild.getComponent().getProject().getKey());
        message.setNumber(preExistingBuild.getNumber());
        message.setDisplayName(preExistingBuild.getDisplayName());
        message.setDuration(preExistingBuild.getDuration());
        message.setStartedAt(100000L);
        message.setTriggeredBy(preExistingBuild.getTriggeredByName());

        this.sut.handle(message);
    }

}
