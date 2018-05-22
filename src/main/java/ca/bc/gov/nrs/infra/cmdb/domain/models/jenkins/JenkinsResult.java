package ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins;

/**
 * An enumeration of all known statuses that a build or promotion can have in Jenkins
 */
public enum JenkinsResult
{
    /**
     * Indicates that a build has completed successfully (green)
     */
    SUCCESS,
    /**
     * Indicates that a build has completed successfully but with warnings (yellow)
     */
    UNSTABLE,
    /**
     * Indicates that a build did not finish successfully (red)
     */
    FAILURE,
    /**
     * Indicates that a build was aborted by a user (grey)
     */
    ABORTED,
    /**
     * Indicates that this object was created with an unknown result value
     */
    UNKNOWN
}
