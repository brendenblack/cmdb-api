package ut.ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins.BuildInfo;
import ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins.MinistryJenkinsClientImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JenkinsClient_ReadBuildInfoResponse_Tests
{
    private static final Logger log = LoggerFactory.getLogger(JenkinsClient_ReadBuildInfoResponse_Tests.class);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldReturnBuildInfo() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/build.json")));
        MinistryJenkinsClientImpl sut = new MinistryJenkinsClientImpl();

        BuildInfo result = sut.readBuildInfoResponse(content);

        assertThat(result, is(notNullValue()));
        assertThat(result, isA(BuildInfo.class));

        collector.checkThat(result.getJobClass(), is("hudson.maven.MavenModuleSetBuild"));
        collector.checkThat(result.getDuration(), is(23484));
        collector.checkThat(result.getQueueId(), is(2871));
        collector.checkThat(result.getDisplayName(), is("#4 (1.0.0)"));
        collector.checkThat(result.getUrl(), is("https://apps.nrs.gov.bc.ca/int/jenkins/job/AQUA/job/aqua-as-cfg/4/"));
        collector.checkThat(result.getTriggeredBy(), is("PSHOWELL"));
        collector.checkThat(result.getTimestamp(), is(175225819));
        collector.checkThat(result.getBuiltOn(), is("wii2"));
        collector.checkThat(result.getSourceBranch(), is("release/1.0.0"));
        collector.checkThat(result.getTargetBranch(), is("master"));
        collector.checkThat(result.getPromotions().size(), is(3));
    }
}
