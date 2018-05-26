package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class BuildImportDeserializerTests
{
    private final Logger log = LoggerFactory.getLogger(BuildImportDeserializerTests.class);

    @Test
    public void shouldParseProjects() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/builds.json")));
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Import.Command.class, new BuildImportDeserializer());
        mapper.registerModule(module);

        Import.Command command = mapper.readValue(content, Import.Command.class);

        assertThat(command.getProjects().size(), greaterThan(0));
    }

    @Test
    public void shouldParseComponents() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/builds.json")));
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Import.Command.class, new BuildImportDeserializer());
        mapper.registerModule(module);

        Import.Command command = mapper.readValue(content, Import.Command.class);
        int componentCount = command.getProjects().stream()
                .map(Import.ProjectModel::getComponents)
                .mapToInt(List::size)
                .sum();

        assertThat(componentCount, greaterThan(0));
    }
}
