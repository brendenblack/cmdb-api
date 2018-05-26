package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsResult;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JenkinsPromotionRepository_findPromotionTests
{
    private final Logger log = LoggerFactory.getLogger(JenkinsPromotionRepository_findPromotionTests.class);

    @Autowired private InfrastructureRegistrationService irs;
    @Autowired private JenkinsBuildRepository buildRepository;
    @Autowired private JenkinsPromotionRepository sut;

    @Test
    public void shouldReturnPromotion()
    {
        int number = 5;
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild preExistingBuild = JenkinsBuild.of(component)
                .number(1)
                .url("http://example.org")
                .startedAt(100000L)
                .took(500L)
                .result(JenkinsResult.SUCCESS)
                .triggeredByUsername("user")
                .build();
        this.buildRepository.save(preExistingBuild);
        log.debug(preExistingBuild.toString());
        JenkinsPromotion promotion = JenkinsPromotion.of(preExistingBuild)
                .environment("0_INTEGRATION")
                .number(number)
                .url("")
                .startedAt(12345L)
                .took(2455L)
                .result("SUCCESS")
                .triggeredByUsername("user")
                .build();
        this.sut.save(promotion);
        log.debug(promotion.toString());

        Optional<JenkinsPromotion> result = this.sut.findPromotion(
                component.getName(),
                preExistingBuild.getNumber(),
                promotion.getEnvironment(),
                promotion.getNumber());

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getId(), is(promotion.getId()));

    }

    @Test
    public void shouldHydrateBuild()
    {
        int number = 5;
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild preExistingBuild = JenkinsBuild.of(component)
                .number(1)
                .url("http://example.org")
                .startedAt(100000L)
                .took(500L)
                .result(JenkinsResult.SUCCESS)
                .triggeredByUsername("user")
                .build();
        this.buildRepository.save(preExistingBuild);
        log.debug(preExistingBuild.toString());
        JenkinsPromotion promotion = JenkinsPromotion.of(preExistingBuild)
                .environment("0_INTEGRATION")
                .number(number)
                .url("")
                .startedAt(12345L)
                .took(2455L)
                .result("SUCCESS")
                .triggeredByUsername("user")
                .build();
        this.sut.save(promotion);
        log.debug(promotion.toString());

        Optional<JenkinsPromotion> result = this.sut.findPromotion(
                component.getName(),
                preExistingBuild.getNumber(),
                promotion.getEnvironment(),
                promotion.getNumber());

        assertThat(result.get().getBuild(), not(nullValue()));
    }

    @Test
    public void shouldHydrateComponent()
    {
        int number = 5;
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild preExistingBuild = JenkinsBuild.of(component)
                .number(1)
                .url("http://example.org")
                .startedAt(100000L)
                .took(500L)
                .result(JenkinsResult.SUCCESS)
                .triggeredByUsername("user")
                .build();
        this.buildRepository.save(preExistingBuild);
        log.debug(preExistingBuild.toString());
        JenkinsPromotion promotion = JenkinsPromotion.of(preExistingBuild)
                .environment("0_INTEGRATION")
                .number(number)
                .url("")
                .startedAt(12345L)
                .took(2455L)
                .result("SUCCESS")
                .triggeredByUsername("user")
                .build();
        this.sut.save(promotion);
        log.debug(promotion.toString());

        Optional<JenkinsPromotion> result = this.sut.findPromotion(
                component.getName(),
                preExistingBuild.getNumber(),
                promotion.getEnvironment(),
                promotion.getNumber());

        assertThat(result.get().getBuild().getComponent(), not(nullValue()));
    }
}
