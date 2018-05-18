package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JenkinsBuildRepository_findByComponentNameAndNumberTests
{
    @Autowired
    private InfrastructureRegistrationService irs;

    @Autowired
    private JenkinsBuildRepository sut;

    @Test
    public void shouldDo()
    {
        int number = 5;
        Component component = this.irs.getOrCreateComponent("AQUA", "aqua-permit-api");
        JenkinsBuild preExistingBuild = JenkinsBuild.of(component)
                .number(number)
                .url("http://example.org")
                .startedAt(100000L)
                .took(500L)
                .result(JenkinsBuild.Result.SUCCESS)
                .triggeredByUsername("user")
                .build();
        preExistingBuild = this.sut.save(preExistingBuild);

        Optional<JenkinsBuild> result = this.sut.findByComponentNameAndNumber(component.getName(), number);

        assertThat(result.isPresent(), is(true));

    }
}
