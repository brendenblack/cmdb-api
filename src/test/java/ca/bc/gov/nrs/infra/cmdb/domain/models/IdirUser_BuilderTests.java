package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import static org.hamcrest.Matchers.is;

public class IdirUser_BuilderTests
{
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldSetExpectedValues()
    {
        String idir = "abcdef";
        String displayName = "Abc Def";
        String email = "abc.def@email.com";

        IdirUser user = IdirUser.of(idir)
                .email(email)
                .displayName(displayName)
                .build();

        collector.checkThat(user.getIdir(), is(idir));
        collector.checkThat(user.getDisplayName(), is(displayName));
        collector.checkThat(user.getEmail(), is(email));
    }
}
