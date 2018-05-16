package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddBuild_Tests
{

    @Autowired
    private AddBuild.Handler sut;

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
}
