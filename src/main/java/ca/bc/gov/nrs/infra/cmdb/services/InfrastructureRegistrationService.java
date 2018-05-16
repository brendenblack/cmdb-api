package ca.bc.gov.nrs.infra.cmdb.services;

import ca.bc.gov.nrs.infra.cmdb.models.Project;
import ca.bc.gov.nrs.infra.cmdb.models.components.Component;

/**
 * Used to centralize the creation and management of Projects and Components, mimicking the authority of the IRS
 * system in place at IMB.
 */
public interface InfrastructureRegistrationService
{
    Project getOrCreateProject(String projectKey);

    Component getOrCreateComponent(String projectKey, String componentName);

}
