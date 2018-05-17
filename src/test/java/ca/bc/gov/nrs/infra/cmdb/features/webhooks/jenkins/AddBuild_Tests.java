package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.JenkinsBuildRepository;
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
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddBuild_Tests
{
    private Logger log = LoggerFactory.getLogger(AddBuild_Tests.class);

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
    public void validCommandCreatesProject()
    {
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        AddBuild.Command message = new AddBuild.Command();
        message.setComponentName(component.getName());
        message.setProjectKey(component.getProject().getKey());
        message.setNumber(5);
        message.setDisplayName("AQUA/aqua-permit-api #5 1.0.0");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddBuild.Model response = this.sut.handle(message);
        Optional<JenkinsBuild> build = this.context.getBuildRepository().findById(response.getId());

        assertThat(build.isPresent(), is(true));
    }

}
