package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This helper class was built because I did not know how to deserialize the test JSON data in an easier way. This class
 * is testable, and can safely be used across other tests.
 */
public class BuildImportDeserializer extends StdDeserializer<Import.Command>
{
    private final Logger log = LoggerFactory.getLogger(BuildImportDeserializer.class);

    public BuildImportDeserializer()
    {
        super(Import.Command.class);
    }

    @Override
    public Import.Command deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        log.trace("Beginning deserialization");
        Import.Command command = new Import.Command();

        JsonNode root = jp.getCodec().readTree(jp);
        for (JsonNode projectNode : root.get("projects"))
        {
            log.debug(projectNode.get("name").asText());
            Import.ProjectModel projectModel = new Import.ProjectModel();
            projectModel.setName(projectNode.get("name").asText());

            for (JsonNode componentNode : projectNode.get("components"))
            {
                Import.ComponentModel componentModel = new Import.ComponentModel();
                log.debug(componentNode.get("name").asText());
                componentModel.setComponentName(componentNode.get("name").asText());

                for (JsonNode buildNode : componentNode.get("builds"))
                {
                    Import.BuildModel buildModel = new Import.BuildModel();
                    buildModel.setNumber(buildNode.get("number").asInt(0));
                    buildModel.setCommitId(buildNode.get("commitId").asText(""));
                    buildModel.setDuration(buildNode.get("duration").asLong(0L));
                    buildModel.setResult(buildNode.get("result").asText("UNKNOWN"));
                    buildModel.setTimestamp(buildNode.get("timestamp").asLong(0L));
                    buildModel.setTriggeredBy(buildNode.get("triggeredBy").asText());

                    for (JsonNode promotionNode : buildNode.get("promotions"))
                    {
                        Import.PromotionModel promotionModel = new Import.PromotionModel();
                        promotionModel.setNumber(promotionNode.get("number").asInt());
                        promotionModel.setDuration(promotionNode.get("duration").asLong());
                        promotionModel.setEnvironment(promotionNode.get("environment").asText("UNKNOWN"));
                        promotionModel.setResult(promotionNode.get("result").asText());
                        promotionModel.setTriggeredBy(promotionNode.get("triggeredBy").asText());
                        promotionModel.setTimestamp(promotionNode.get("timestamp").asLong(0L));

                        log.debug(buildModel.toString());
                        buildModel.getPromotions().add(promotionModel);
                    }

                    componentModel.getBuilds().add(buildModel);
                }

                projectModel.getComponents().add(componentModel);
            }

            command.getProjects().add(projectModel);
        }

        log.trace("Deserialization ends");

        return command;
    }
}
