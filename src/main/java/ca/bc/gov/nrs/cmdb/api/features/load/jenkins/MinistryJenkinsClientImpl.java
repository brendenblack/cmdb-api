package ca.bc.gov.nrs.cmdb.api.features.load.jenkins;

import ca.bc.gov.nrs.cmdb.api.infrastructure.HttpException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * Opinionated Jenkins client that relies on the Ministry's implementation conventions.
 */
@Service
public class MinistryJenkinsClientImpl implements JenkinsClient
{
    private static final Logger log = LoggerFactory.getLogger(MinistryJenkinsClientImpl.class);

    private String baseUrl;
    private String username;
    private String password;

//    @Value("${cmdb.jenkins.url}") String jenkinsUrl,
//    @Value("${cmdb.jenkins.username}") String username,
//    @Value("${cmdb.jenkins.password}") String password,
    public CloseableHttpClient getHttpClient()
    {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("user", "passwd"));

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        return httpclient;
    }

    //region Build info
    @Override
    public String getBuildInfoUrl(String stream, String projectKey, String componentName, int buildNumber)
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(this.baseUrl);
        if (!StringUtils.isBlank(stream))
        {
            urlBuilder.append("/job/")
                    .append(stream);
        }

        urlBuilder.append("/job/")
                .append(projectKey)
                .append("/job/")
                .append(componentName)
                .append("/")
                .append(buildNumber)
                .append("/api/json&depth=2");

        return urlBuilder.toString();
    }

    @Override
    public BuildInfo fetchBuildInfo(String stream, String projectKey, String componentName, int buildNumber) throws IOException
    {
        String url = getBuildInfoUrl(stream, projectKey, componentName, buildNumber);

        return fetchBuildInfo(url);
    }

    @Override
    public BuildInfo fetchBuildInfo(String url) throws IOException
    {
        log.debug("Fetching build information from {}", url);
        try (CloseableHttpClient client = getHttpClient())
        {
            HttpGet httpget = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(httpget))
            {
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200)
                {
                    return readBuildInfoResponse(EntityUtils.toString(response.getEntity()));
                }
                else
                {
                    log.error("While making a GET call to {}, server returned an unexpected status code of {}: {}",
                              url,
                              statusCode,
                              response.getStatusLine().getReasonPhrase());

                    if (response.getEntity() != null)
                    {
                        log.error("Response included message body: {}", EntityUtils.toString(response.getEntity()));
                    }
                    else
                    {
                        log.error("Response included no message body");
                    }

                    throw new HttpException(HttpStatus.resolve(response.getStatusLine().getStatusCode()),
                                            response.getStatusLine().getReasonPhrase());
                }
            }
        }
    }

    public BuildInfo readBuildInfoResponse(String response)
    {
        BuildInfo build = new BuildInfo();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            JsonNode json = mapper.readTree(response);
            build.setJobClass(json.get("_class").asText());
            build.setDuration(json.get("duration").asInt());
            build.setDisplayName(json.get("displayName").asText());
            build.setUrl(json.get("url").asText());
            build.setQueueId(json.get("queueId").asInt());
            build.setTimestamp(json.get("timestamp").asInt());
            build.setBuiltOn(json.get("builtOn").asText());

            if (json.get("actions").isArray())
            {
                for (final JsonNode node : json.get("actions"))
                {
                    if (node.has("_class"))
                    {
                        String actionClass = node.get("_class").asText();

                        switch (actionClass)
                        {
                            case "hudson.model.CauseAction":
                                build = readCauseAction(build, node);
                                break;
                            case "hudson.model.ParametersAction":
                                build = readParametersAction(build, node);
                                break;
                            case "hudson.plugins.git.util.BuildData":
                                build = readBuildData(build, node);
                                break;
                            case "hudson.plugins.promoted_builds.PromotedBuildAction":
                                build = readPromotedBuildAction(build, node);
                                break;
                            case "readMavenAggregatedArtifactRecord":
                                build = readMavenAggregatedArtifactRecord(build, node);
                                break;
                            default:
                                // Many actions are returned as an empty map, i.e. {}
                                Optional.of(node.get("_class").asText())
                                        .ifPresent(c -> log.warn("Unandled action type of {}", c));
                                break;

                        }
                    }
                }
            }

            return build;

        }
        catch (IOException e)
        {
            log.error("An error occurred while unmarshalling response string: {}", response, e);
            throw new HttpException(e);
        }
    }

    public BuildInfo readCauseAction(BuildInfo build, JsonNode node)
    {
        String expectedClass = "hudson.model.CauseAction";
        if (node.get("_class").asText().equals(expectedClass))
        {
            for (final JsonNode n : node.get("causes"))
            {
                if (n.get("_class").asText().equals("hudson.model.Cause$UserIdCause"))
                {
                    build.setTriggeredBy(n.get("userId").asText());
                }
            }
        }
        else
        {
            log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
        }

        return build;
    }

    public BuildInfo readParametersAction(BuildInfo build, JsonNode node)
    {
        String expectedClass = "hudson.model.ParametersAction";
        if (node.get("_class").asText().equals(expectedClass))
        {
            for (final JsonNode n : node.get("parameters"))
            {
                switch (n.get("_class").asText())
                {
                    case "hudson.model.StringParameterValue":
                        switch (n.get("name").asText())
                        {
                            case "targetBranch":
                                build.setTargetBranch(n.get("value").asText());
                                break;
                            case "sourceBranch":
                                build.setSourceBranch(n.get("value").asText());
                                break;
                            default:
                                log.warn("Unexpected String parameter of {} = {}",
                                         n.get("name").asText(),
                                         n.get("value").asText());
                                break;
                        }
                        break;
                    default:
                        log.warn("Unhandled hudson.model.ParametersAction entry of type {}: {}", n.get("_class").asText(), n);
                        break;
                }
            }
        }
        else
        {
            log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
        }

        return build;
    }

    public BuildInfo readBuildData(BuildInfo build, JsonNode node)
    {
        String expectedClass = "hudson.plugins.git.util.BuildData";

        if (node.get("_class").asText().equals(expectedClass))
        {
            Optional.of(node.get("buildsByBranchName"))
                    .map(n -> n.get("HEAD"))
                    .map(n -> n.get("marked"))
                    .ifPresent(n -> build.setSha1(n.get("SHA1").asText()));
        }
        else
        {
            log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
        }

        return build;

    }

    public BuildInfo readPromotedBuildAction(BuildInfo build, JsonNode node)
    {
        String expectedClass = "hudson.plugins.promoted_builds.PromotedBuildAction";

        if (node.get("_class").asText().equals(expectedClass))
        {
            for (final JsonNode promotion : node.get("promotions"))
            {
                String environmentName = promotion.get("name").asText();
                for (final JsonNode promotionBuild : promotion.get("promotionBuilds"))
                {
                    int number = promotionBuild.get("number").asInt();
                    String url = promotionBuild.get("url").asText();
                    build.addPromotion(environmentName, number, url);
                    log.debug("Promoted to {} - {}", environmentName, number);
                }
            }
        }
        else
        {
            log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
        }

        return build;
    }

    public BuildInfo readMavenAggregatedArtifactRecord(BuildInfo build, JsonNode node)
    {
        String expectedClass = "hudson.maven.reporters.MavenAggregatedArtifactRecord";

        if (node.get("_class").asText().equals(expectedClass))
        {
            // TODO
//                for (final JsonNode moduleRecord : node.get("moduleRecords"))
//                {
//
//                }
            log.warn("Handling of {} not yet implemented", expectedClass);
        }
        else
        {
            log.warn("Provided node is of type {}, expected {}", node.get("_class"), expectedClass);
        }

        return build;
    }
    //endregion
}
