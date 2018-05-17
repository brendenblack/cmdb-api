package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.is;

public class JenkinsBuild_BuilderTests
{
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldSetExpectedValues_whenOnlyRequiredValuesProvided()
    {
        Project project = Project.withKey("NPE").build();
        Component component = Component.ofName("npe-test-api").belongsTo(project).build();
        IdirUser user = IdirUser.of("abcdef").build();
        String username = user.getIdir();
        int number = 12;
        String url = "http://google.com";
        long startedAt = 10056782510L;
        Instant instant = Instant.ofEpochMilli(startedAt);
        LocalDateTime startedAtDt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        long duration = 200L;
        JenkinsBuild.Result result = JenkinsBuild.Result.SUCCESS;

        JenkinsBuild build = JenkinsBuild.of(component)
            .number(number)
            .url(url)
            .startedAt(startedAt)
            .took(duration)
            .result(result)
            .triggeredByUsername(username)
            .build();

        collector.checkThat(build.getComponent(), is(component));
        collector.checkThat(build.getNumber(), is(number));
        collector.checkThat(build.getUrl(), is(url));
        collector.checkThat(build.getStartedAt(), is(startedAtDt));
        collector.checkThat(build.getDuration(), is(duration));
        collector.checkThat(build.getResult(), is(result));
        collector.checkThat(build.getTriggeredByName(), is(username));
    }
}
