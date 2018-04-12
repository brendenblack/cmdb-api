package it.ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.models.Server;
import ca.bc.gov.nrs.cmdb.api.repositories.CmdbContext;
import it.ca.bc.gov.nrs.cmdb.api.infrastructure.TestPersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = {TestPersistenceContext.class })
@RunWith(SpringRunner.class)
public class BuildService_Builder_Build_Tests
{
    @Autowired
    private CmdbContext context;

    @Test
    public void shouldBeNull()
    {
        Optional<Server> serverOptional = this.context.getServerRepository().findByFqdn("wii.bcgov");

        assertThat(serverOptional.isPresent(), is(false));
    }
}
