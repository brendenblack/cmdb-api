package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;

public class JenkinsRoutes
{
    private static final Logger log = LoggerFactory.getLogger(JenkinsRoutes.class);

    public static final String PROJECT_KEY_PLACEHOLDER = "{projectKey}";
    public static final String JOB_NAME_PLACEHOLDER = "{jobName}";
    public static final String BUILD_NUMBER_PLACEHOLDER = "{buildNumber}";
    public static final String ENVIRONMENT_PLACEHOLDER = "{environmentName}";
    public static final String PROMOTION_NUMBER_PLACEHOLDER = "{promotionNumber}";

    public final static String BASE = "/jenkins";

    public static final String POST_BUILD_ROUTE = BASE + "/projects/" + PROJECT_KEY_PLACEHOLDER + "/jobs/" + JOB_NAME_PLACEHOLDER + "/builds";

    public static final String GET_BUILD_ROUTE = BASE + "/projects/" + PROJECT_KEY_PLACEHOLDER + "/jobs/" + JOB_NAME_PLACEHOLDER + "/builds/" + BUILD_NUMBER_PLACEHOLDER;

    public static final String GET_PROMOTION_ROUTE = BASE +
            "/projects" +
            PROJECT_KEY_PLACEHOLDER +
            "/jobs/" +
            JOB_NAME_PLACEHOLDER +
            "/builds/" +
            BUILD_NUMBER_PLACEHOLDER +
            "/promotions/" +
            ENVIRONMENT_PLACEHOLDER +
            "/" +
            PROMOTION_NUMBER_PLACEHOLDER;

    public static String makeLink(JenkinsBuild build)
    {
        try
        {
            return GET_BUILD_ROUTE
                    .replace(PROJECT_KEY_PLACEHOLDER, build.getComponent().getProject().getKey())
                    .replace(JOB_NAME_PLACEHOLDER, build.getComponent().getName())
                    .replace(BUILD_NUMBER_PLACEHOLDER, String.valueOf(build.getNumber()));
        }
        catch (NullPointerException e)
        {
            log.error("Unable to construct route for build", e);
            log.error(build.toString());
            return "";
        }
    }

    public static String makeLink(JenkinsPromotion promotion)
    {
        try
        {
            return GET_PROMOTION_ROUTE
                    .replace(PROJECT_KEY_PLACEHOLDER, promotion.getBuild().getComponent().getProject().getKey())
                    .replace(JOB_NAME_PLACEHOLDER, promotion.getBuild().getComponent().getName())
                    .replace(BUILD_NUMBER_PLACEHOLDER, String.valueOf(promotion.getBuild().getNumber()))
                    .replace(ENVIRONMENT_PLACEHOLDER, promotion.getEnvironment())
                    .replace(PROMOTION_NUMBER_PLACEHOLDER, String.valueOf(promotion.getNumber()));
        }
        catch (NullPointerException e)
        {
            log.error("Unable to construct route for promotion", e);
            log.error(promotion.toString());
            return "";
        }
    }

}
