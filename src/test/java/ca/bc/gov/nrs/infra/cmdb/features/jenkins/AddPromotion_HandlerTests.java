package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
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
public class AddPromotion_HandlerTests
{
    private Logger log = LoggerFactory.getLogger(AddPromotion_HandlerTests.class);

    @Autowired
    private AddPromotion.Handler sut;

    @Autowired
    private CmdbContext context;

    @Autowired
    private InfrastructureRegistrationService irs;

    @Test
    public void validCommandReturnsId()
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
        this.context.getJenkinsBuildRepository().save(preExistingBuild);
        AddPromotion.Command message = new AddPromotion.Command();
        message.setComponentName("aqua-permit-api");
        message.setProjectKey("AQUA");
        message.setBuildNumber(5);
        message.setPromotionNumber(5);
        message.setEnvironmentName("0_INTEGRATION");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddPromotion.Model response = this.sut.handle(message);

        assertThat("id", response.getId(), is(greaterThan(0L)));
    }

    @Test
    public void validCommandCreatesPromotion()
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
        this.context.getJenkinsBuildRepository().save(preExistingBuild);
        AddPromotion.Command message = new AddPromotion.Command();
        message.setComponentName("aqua-permit-api");
        message.setProjectKey("AQUA");
        message.setBuildNumber(5);
        message.setPromotionNumber(5);
        message.setEnvironmentName("0_INTEGRATION");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddPromotion.Model response = this.sut.handle(message);
        Optional<JenkinsPromotion> promotion = this.context.getJenkinsPromotionRepository().findPromotion(component.getName(), preExistingBuild.getNumber(), "0_INTEGRATION", 5);

        assertThat(promotion.isPresent(), is(true));
    }

    @Test(expected = HttpException.class)
    public void shouldThrowWhenBuildNotExist()
    {
        AddPromotion.Command message = new AddPromotion.Command();
        message.setComponentName("aqua-permit-api");
        message.setProjectKey("AQUA");
        message.setBuildNumber(25);
        message.setPromotionNumber(5);
        message.setEnvironmentName("0_INTEGRATION");
        message.setDuration(1000L);
        message.setStartedAt(new Date().getTime());
        message.setTriggeredBy("brblack");

        AddPromotion.Model response = this.sut.handle(message);
    }

}
