package ca.bc.gov.nrs.infra.cmdb.domain.services;

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

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfrastructureRegistrationService_getOrCreateProjectTests
{
    private Logger log = LoggerFactory.getLogger(InfrastructureRegistrationService_getOrCreateProjectTests.class);

    @Autowired
    private InfrastructureRegistrationService sut;

    @Autowired
    private CmdbContext context;


    @Test
    public void shouldReturnExistingProject_whenProjectExists()
    {
        Project project = Project.withKey("ABC").build();
        log.debug("Project ({}): {}", project.getId(), project.toString());
        log.debug("Context: {}", this.context);
        log.debug("Repository: {}", this.context.getProjectRepository());
        this.context.getProjectRepository().save(project);
        log.debug("id: {} - {}", project.getId(), project.toString());

        Project result = this.sut.getOrCreateProject(project.getKey());

        assertThat(result.getId(), is(project.getId()));
    }

    @Test
    public void shouldCreateNewProject_whenProjectNotExists()
    {
        String key = "NEWPROJ";
        Optional<Project> nonExistentProject = this.context.getProjectRepository().findByKey(key);
        assertThat("Test project already exists in the database prior to the test", nonExistentProject.isPresent(), is(false));

        Project result = this.sut.getOrCreateProject(key);
        Optional<Project> existentProject = this.context.getProjectRepository().findByKey(key);

        assertThat(existentProject.isPresent(), is(true));
    }
}
