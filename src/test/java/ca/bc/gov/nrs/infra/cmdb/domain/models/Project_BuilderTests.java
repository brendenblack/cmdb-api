package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Project_BuilderTests
{
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldSetExpectedAndDefaultValues_whenRequiredValuesAreValid()
    {
        String key = "NPE";

        Project project = Project.withKey(key)
                .build();

        collector.checkThat(project.getKey(), is(key));
        collector.checkThat(project.getName(), is(key));
        collector.checkThat(project.getDescription(), is(key + " project"));
    }

    @Test
    public void shouldSetValues_whenOptionalValuesAreValid()
    {
        String key = "NPE";
        String name = "NRS Phonebook Example";
        String description = "An example for NRS using a phonebook";

        Project project = Project.withKey(key)
                .description(description)
                .name(name)
                .build();

        collector.checkThat(project.getKey(), is(key));
        collector.checkThat(project.getName(), is(name));
        collector.checkThat(project.getDescription(), is(description));
    }

    @Test
    public void shouldSetKeyUpperCase()
    {
        String key = "lowerc";

        Project project = Project.withKey(key).build();

        assertThat(project.getKey(), is(key.toUpperCase()));
    }

}
