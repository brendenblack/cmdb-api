package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import java.io.IOException;

public interface JenkinsClient
{
    /**
     * Constructs a URL to fetch build information.
     *
     * @param stream
     * @param projectKey
     * @param componentName
     * @param buildNumber
     * @return
     */
    String getBuildInfoUrl(String stream, String projectKey, String componentName, int buildNumber);

    /**
     *
     * @param stream
     * @param projectKey
     * @param componentName
     * @param buildNumber
     * @return
     * @throws IOException
     */
    BuildInfo fetchBuildInfo(String stream, String projectKey, String componentName, int buildNumber) throws IOException;

    /**
     * Retreives {@link BuildInfo} details from Jenkins using the provided build URL
     *
     * @param url
     * @return
     * @throws IOException
     */
    BuildInfo fetchBuildInfo(String url) throws IOException;
}
