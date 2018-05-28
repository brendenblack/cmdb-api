package ca.bc.gov.nrs.infra.cmdb.features.irs;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;

public class IrsRoutes
{

    public static final String PROJECT_KEY_PLACEHOLDER = "{projectKey}";
    public static final String COMPONENT_NAME_PLACEHOLDER = "{componentName}";

    public final static String BASE = "/irs";

    public static final String POST_PROJECT_ROUTE = BASE + "/projects";

    public static final String GET_PROJECT_ROUTE = BASE + "/projects/" + PROJECT_KEY_PLACEHOLDER;

    public static final String POST_COMPONENT_ROUTE = GET_PROJECT_ROUTE + "/components";

    public static final String GET_COMPONENT_ROUTE = BASE + GET_PROJECT_ROUTE + "/components/" + COMPONENT_NAME_PLACEHOLDER;

    public static String makeLink(Project project)
    {
        return makeLink(project.getKey());
    }

    public static String makeLink(Component component)
    {
       return makeLink(component.getProject().getKey(), component.getName());
    }

    public static String makeLink(String projectKey, String componentName)
    {
        return GET_COMPONENT_ROUTE
                .replace(PROJECT_KEY_PLACEHOLDER, projectKey)
                .replace(COMPONENT_NAME_PLACEHOLDER, componentName);
    }

    public static String makeLink(String projectKey)
    {
        return GET_PROJECT_ROUTE.replace(PROJECT_KEY_PLACEHOLDER, projectKey);
    }
}
