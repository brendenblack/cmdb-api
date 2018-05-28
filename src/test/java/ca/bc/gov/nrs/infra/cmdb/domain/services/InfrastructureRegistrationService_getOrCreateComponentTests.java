package ca.bc.gov.nrs.infra.cmdb.domain.services;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfrastructureRegistrationService_getOrCreateComponentTests
{
    private Logger log = LoggerFactory.getLogger(InfrastructureRegistrationService_getOrCreateComponentTests.class);

    @Autowired
    private InfrastructureRegistrationService sut;

    @Autowired
    private CmdbContext context;

    @Test
    public void shouldReturnExistingComponent_whenComponentExists()
    {
        Project project = Project.withKey("ABC").build();
        this.context.getProjectRepository().save(project);
        Component component = Component.ofName("abc-component-api").belongsTo(project).build();
        this.context.getComponentRepository().save(component);

        Component result = this.sut.getOrCreateComponent("ABC", "abc-component-api");

        assertThat(result.getId(), is(component.getId()));
    }

    @Test
    public void shouldReturnComponentWithHydratedProject()
    {
        Project project = Project.withKey("ABC").build();
        this.context.getProjectRepository().save(project);
        Component component = Component.ofName("abc-component-api").belongsTo(project).build();
        this.context.getComponentRepository().save(component);

        Component result = this.sut.getOrCreateComponent("ABC", "abc-component-api");

        assertThat(result.getProject(), not(nullValue()));
        assertThat(result.getProject().getId(), is(project.getId()));
    }
}
