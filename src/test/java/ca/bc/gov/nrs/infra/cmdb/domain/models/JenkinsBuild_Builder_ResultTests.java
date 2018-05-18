package ca.bc.gov.nrs.infra.cmdb.domain.models;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class JenkinsBuild_Builder_ResultTests
{
    private final Logger log = LoggerFactory.getLogger(JenkinsBuild_Builder_ResultTests.class);

    @Test
    @Parameters({ "SUCCESS", "success", "sucCeSS"})
    public void shouldSetSuccessResult(String result)
    {
        log.debug("Performing test with input {}", result);

        Project project = Project.withKey("NPE").build();
        Component component = Component.ofName("npe-test-api").belongsTo(project).build();
        String username = "idir";
        int number = 12;
        String url = "http://google.com";
        long startedAt = 10056782510L;
        Instant instant = Instant.ofEpochMilli(startedAt);
        LocalDateTime startedAtDt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        long duration = 200L;

        JenkinsBuild build = JenkinsBuild.of(component)
                .number(number)
                .url(url)
                .startedAt(startedAt)
                .took(duration)
                .result(result)
                .triggeredByUsername(username)
                .build();

        assertThat(build.getResult(), is(JenkinsBuild.Result.SUCCESS));
    }

    @Test
    @Parameters({ "FAILURE", "failure", "faIlUrE"})
    public void shouldSetFailureResult(String result)
    {
        log.debug("Performing test with input {}", result);

        Project project = Project.withKey("NPE").build();
        Component component = Component.ofName("npe-test-api").belongsTo(project).build();
        String username = "idir";
        int number = 12;
        String url = "http://google.com";
        long startedAt = 10056782510L;
        Instant instant = Instant.ofEpochMilli(startedAt);
        LocalDateTime startedAtDt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        long duration = 200L;

        JenkinsBuild build = JenkinsBuild.of(component)
                .number(number)
                .url(url)
                .startedAt(startedAt)
                .took(duration)
                .result(result)
                .triggeredByUsername(username)
                .build();

        assertThat(build.getResult(), is(JenkinsBuild.Result.FAILURE));
    }

    @Test
    @Parameters({ "junk", "not a thing", "", "       ", "SUCSES" })
    public void shouldSetUnknown(String result)
    {
        log.debug("Performing test with input {}", result);

        Project project = Project.withKey("NPE").build();
        Component component = Component.ofName("npe-test-api").belongsTo(project).build();
        String username = "idir";
        int number = 12;
        String url = "http://google.com";
        long startedAt = 10056782510L;
        Instant instant = Instant.ofEpochMilli(startedAt);
        LocalDateTime startedAtDt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        long duration = 200L;

        JenkinsBuild build = JenkinsBuild.of(component)
                .number(number)
                .url(url)
                .startedAt(startedAt)
                .took(duration)
                .result(result)
                .triggeredByUsername(username)
                .build();

        assertThat(build.getResult(), is(JenkinsBuild.Result.UNKNOWN));
    }


}
