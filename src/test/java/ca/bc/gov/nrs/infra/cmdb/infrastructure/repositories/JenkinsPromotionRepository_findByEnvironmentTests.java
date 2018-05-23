package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.JenkinsTestHelper;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JenkinsPromotionRepository_findByEnvironmentTests
{
    private final Logger log = LoggerFactory.getLogger(JenkinsPromotionRepository_findByEnvironmentTests.class);

    @Autowired private JenkinsTestHelper testHelper;
    @Autowired private InfrastructureRegistrationService irs;
    @Autowired private JenkinsBuildRepository buildRepository;
    @Autowired private JenkinsPromotionRepository sut;


    // TODO: this test is fragile and relies on the implementation of JenkinsTestHelper
    @Test
    public void shouldReturnAllPromotionsOfEnvironment()
    {
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild build = this.testHelper.makeRandomBuildOfComponent(component);
        this.buildRepository.save(build);
        JenkinsPromotion p1 = this.testHelper.makeRandomPromotionOfBuild(build);
        JenkinsPromotion p2 = this.testHelper.makeRandomPromotionOfBuild(build);
        JenkinsPromotion p3 = this.testHelper.makeRandomPromotionOfBuild(build);
        this.testHelper.setEnvironment("2_TEST");
        JenkinsPromotion p4 = this.testHelper.makeRandomPromotionOfBuild(build);
        this.sut.saveAll(Arrays.asList(p1, p2, p3, p4));

        Iterable<JenkinsPromotion> promotions = this.sut.findByEnvironment(component.getName(), p1.getEnvironment());
        List<JenkinsPromotion> results = new ArrayList<>();
        promotions.iterator().forEachRemaining(results::add);

        assertThat(results.size(), is(3));
    }



}
