package ca.bc.gov.nrs.cmdb.api.services;

import ca.bc.gov.nrs.cmdb.api.models.Project;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;

import java.util.Optional;

/**
 * Used to manage the logical entities of Projects and Components,
 */
public interface ComponentService
{
    Project getOrCreateProject(String projectKey);

    Component getOrCreateComponent(String projectKey, String componentName);

}
