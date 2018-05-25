package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.JenkinsTestHelper;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsResult;
import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Import_HandlerTests
{
    private Logger log = LoggerFactory.getLogger(Import_HandlerTests.class);

    @Autowired private CmdbContext context;
    @Autowired private Import.Handler sut;
    @Autowired private JenkinsTestHelper helper;

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldCreateExpectedComponents() throws IOException
    {
        Import.Command command = this.helper.readBuildsJson();

        Import.Model model = this.sut.handle(command);
        Optional<Component> component = this.context.getComponentRepository().findByName("arts-arts-api");

        assertThat(component.isPresent(), is(true));
    }

    @Test
    public void shouldCreateExpectedPromotions() throws IOException
    {
        Import.Command command = this.helper.readBuildsJson();

        Import.Model model = this.sut.handle(command);
        // arbitrarily select a representative promotion from the data set
        Optional<JenkinsPromotion> promotion = this.context.getJenkinsPromotionRepository().findPromotion("arts-arts-api", 5, "0_INTEGRATION", 14);

        assertThat(promotion.isPresent(), is(true));
    }

    @Test
    public void shouldCreateBuild_whenNotExists()
    {
        Import.BuildModel b = new Import.BuildModel();
        b.setTriggeredBy("user1");
        b.setResult("SUCCESS");
        b.setDuration(17265L);
        b.setCommitId("3715cce4d078f41d213c738009856af8b117920f");
        b.setNumber(25);
        b.setTimestamp(System.currentTimeMillis() - 10000);
        Import.ComponentModel c = new Import.ComponentModel();
        c.setComponentName("aqua-permit-api");
        c.setBuilds(Arrays.asList(b));
        Import.ProjectModel prj = new Import.ProjectModel();
        prj.setName("AQUA");
        prj.setComponents(Arrays.asList(c));
        Import.Command command = new Import.Command();
        command.setProjects(Arrays.asList(prj));

        Import.Model result = this.sut.handle(command);
        Optional<JenkinsBuild> createdBuild = this.context.getJenkinsBuildRepository().findByComponentNameAndNumber(c.getComponentName(), b.getNumber());
        JenkinsBuild build = createdBuild.get();

        collector.checkThat(build.getNumber(), is(b.getNumber()));
        collector.checkThat(build.getTriggeredByName(), is(b.getTriggeredBy()));
        collector.checkThat(build.getResult(), is(JenkinsResult.SUCCESS));
        collector.checkThat(build.getDuration(), is(b.getDuration()));
//        collector.checkThat(build.get);
    }


    @Test
    public void shouldCreatePromotion_whenNotExists()
    {
        Import.PromotionModel p = new Import.PromotionModel();
        p.setTriggeredBy("user2");
        p.setResult("SUCCESS");
        p.setEnvironment("2_TEST");
        p.setNumber(2);
        p.setDuration(12387L);
        p.setTimestamp(System.currentTimeMillis());
        Import.BuildModel b = new Import.BuildModel();
        b.setTriggeredBy("user1");
        b.setResult("SUCCESS");
        b.setDuration(17265L);
        b.setCommitId("3715cce4d078f41d213c738009856af8b117920f");
        b.setNumber(25);
        b.setTimestamp(System.currentTimeMillis() - 10000);
        b.setPromotions(Arrays.asList(p));
        Import.ComponentModel c = new Import.ComponentModel();
        c.setComponentName("aqua-permit-api");
        c.setBuilds(Arrays.asList(b));
        Import.ProjectModel prj = new Import.ProjectModel();
        prj.setName("AQUA");
        prj.setComponents(Arrays.asList(c));
        Import.Command command = new Import.Command();
        command.setProjects(Arrays.asList(prj));

        Import.Model result = this.sut.handle(command);
        Optional<JenkinsPromotion> existingPromotion = this.context.getJenkinsPromotionRepository().findPromotion(c.getComponentName(), b.getNumber(), p.getEnvironment(), p.getNumber());
        JenkinsPromotion promotion = existingPromotion.get();

        collector.checkThat(promotion.getNumber(), is(p.getNumber()));
        collector.checkThat(promotion.getEnvironment(), is(p.getEnvironment()));
        collector.checkThat(promotion.getTriggeredByName(), is(p.getTriggeredBy()));
    }
}
