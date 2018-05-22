package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsResult;
import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpsertBuilds_HandlerTests
{
    private Logger log = LoggerFactory.getLogger(UpsertBuilds_HandlerTests.class);

    @Autowired private UpsertBuilds.Handler sut;
    @Autowired private CmdbContext context;
    @Autowired private InfrastructureRegistrationService irs;


    @Test
    public void shouldReturnSuccessList_whenCommandValid()
    {
        AddBuildModel build = new AddBuildModel();
        build.setProjectKey("NPE");
        build.setComponentName("npe-test-api");
        build.setNumber(123);
        List<AddBuildModel> builds = Arrays.asList(build);
        UpsertBuilds.Command command = new UpsertBuilds.Command();
        command.setBuilds(builds);

        UpsertBuilds.Model result = this.sut.handle(command);

        assertThat(result.getAdded().size(), is(1));
    }

    @Test
    public void shouldAddNewBuilds_whenCommandValid()
    {
        String component = "npe-test-api";
        int number = 123;
        Optional<JenkinsBuild> nonExistentBuild = this.context.getJenkinsBuildRepository().findByComponentNameAndNumber(component, number);
        assertThat(nonExistentBuild.isPresent(), is(false));
        AddBuildModel build = new AddBuildModel();
        build.setProjectKey("NPE");
        build.setComponentName(component);
        build.setNumber(number);
        List<AddBuildModel> builds = Arrays.asList(build);
        UpsertBuilds.Command command = new UpsertBuilds.Command();
        command.setBuilds(builds);

        UpsertBuilds.Model result = this.sut.handle(command);
        Optional<JenkinsBuild> existentBuild = this.context.getJenkinsBuildRepository().findByComponentNameAndNumber(component, number);

        assertThat(existentBuild.isPresent(), is(true));
    }

    // TODO: this test uses the display name as a litmus test for updating, should be made more robust
    @Test
    public void shouldUpdateExistingBuilds_whenCommandValid()
    {
        Project project = Project.withKey("NPE").build();
        project = this.context.getProjectRepository().save(project);
        Component component = Component.ofName("npe-test-api").belongsTo(project).build();
        component = this.context.getComponentRepository().save(component);
        JenkinsBuild build = JenkinsBuild.of(component)
                .number(123)
                .url("http://example.org")
                .startedAt(1124985L)
                .took(1245L)
                .result(JenkinsResult.SUCCESS)
                .triggeredByUsername("user")
                .build();
        build = this.context.getJenkinsBuildRepository().save(build);
        Optional<JenkinsBuild> preExistingBuild = this.context.getJenkinsBuildRepository().findByComponentNameAndNumber(build.getComponent().getName(), build.getNumber());
        String preExistingDisplayName = preExistingBuild.get().getDisplayName();
        AddBuildModel model = new AddBuildModel();
        model.setProjectKey(component.getProject().getKey());
        model.setComponentName(component.getName());
        model.setNumber(build.getNumber());
        String expectedDisplayName = "New display name";
        model.setDisplayName(expectedDisplayName);
        List<AddBuildModel> builds = Arrays.asList(model);
        UpsertBuilds.Command command = new UpsertBuilds.Command();
        command.setBuilds(builds);

        UpsertBuilds.Model result = this.sut.handle(command);
        Optional<JenkinsBuild> updatedBuild = this.context.getJenkinsBuildRepository().findByComponentNameAndNumber(component.getName(), build.getNumber());

        assertThat(updatedBuild.isPresent(), is(true));
        assertThat(updatedBuild.get().getDisplayName(), not(preExistingDisplayName));
        assertThat(updatedBuild.get().getDisplayName(), is(expectedDisplayName));
    }
}
