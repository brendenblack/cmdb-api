package ca.bc.gov.nrs.infra.cmdb.domain.services;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;

/**
 * Used to centralize the creation and management of Projects and Components, mimicking the authority of the IRS
 * system in place at IMB.
 */
public interface InfrastructureRegistrationService
{
    Project getOrCreateProject(String projectKey);

    Component getOrCreateComponent(String projectKey, String componentName);

}
