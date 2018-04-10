package ut.ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.features.load.jenkins.BuildService;
import ca.bc.gov.nrs.cmdb.api.features.load.jenkins.GetBuildInfo;
import ca.bc.gov.nrs.cmdb.api.repositories.ComponentRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ProjectRepository;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.mock;

public class GetBuildInfoHandler_ReadBuildInfoResponse_Tests
{
    private static final Logger log = LoggerFactory.getLogger(GetBuildInfoHandler_ReadBuildInfoResponse_Tests.class);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldReturnBuildInfo() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/build.json")));
        GetBuildInfo.Handler sut = new GetBuildInfo.Handler("url",
                                 "username",
                                 "password",
                                 mock(ProjectRepository.class),
                                 mock(ComponentRepository.class),
                                 mock(BuildService.class));

        GetBuildInfo.BuildInfo result = sut.readBuildInfoResponse(content);

        collector.checkThat(result, is(notNullValue()));
        collector.checkThat(result, isA(GetBuildInfo.BuildInfo.class));
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
    }
}
