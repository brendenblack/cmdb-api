package ut.ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.features.load.jenkins.GetBuildInfo;
import ca.bc.gov.nrs.cmdb.api.repositories.ComponentRepository;
import ca.bc.gov.nrs.cmdb.api.repositories.ProjectRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GetBuildInfoHandler_ReadBuildInfoResponse_Tests
{
    private static final Logger log = LoggerFactory.getLogger(GetBuildInfoHandler_ReadBuildInfoResponse_Tests.class);

    @Test
    public void shouldDo() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/build.json")));
        GetBuildInfo.Handler sut = new GetBuildInfo.Handler("url",
                                                            "username",
                                                            "password",
                                                            mock(ProjectRepository.class),
                                                            mock(ComponentRepository.class));


        sut.readBuildInfoResponse(content);

        assertTrue(true);

    }
}
