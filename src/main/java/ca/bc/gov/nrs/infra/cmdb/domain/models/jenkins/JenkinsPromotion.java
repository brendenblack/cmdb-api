package ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = false, of = { "build", "number" })
@Getter
public class JenkinsPromotion
{
    public final static String RELATIONSHIP_PROMOTION_OF = "PROMOTION_OF";

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead
     */
    @Deprecated
    public JenkinsPromotion() {}

    JenkinsPromotion(JenkinsBuild build, String environment, int number)
    {
        this.build = build;
        this.environment = environment;
        this.number = number;
    }

    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;

    //region immutable fields
    @Relationship(type = RELATIONSHIP_PROMOTION_OF)
    @Required
    private JenkinsBuild build;

    @Required
    private String environment;

    @Required
    private int number;
    //endregion

    //region required fields
    @Setter
    @Required
    private long duration;

    @Index(unique = true)
    @Required
    @Setter
    private String url;

    @Setter
    @Required
    @Getter(AccessLevel.NONE)
    private long timestamp;

    @Setter
    @Required
    private JenkinsResult result;

    /**
     * The username as represented in Jenkins.This value may or may not map to an {@link IdirUser} object, and in the
     * case where no such object can be found it is imperative to have this reference.
     */
    @Setter
    @Required
    private String triggeredByName;
    //endregion

    //region optional fields
    @Relationship(type = ComponentInstance.RELATIONSHIP_MANIFESTS_BUILD)
    private Set<ComponentInstance> instances;

    @Setter
    private String jobClass;

    @Setter
    private String displayName;

//    @Relationship(type = RELATIONSHIP_BUILT_ON)
//    @Setter
//    private Server server;

    @Setter
    private int queueId;
    //endregion

    /**
     * Convenience method to access the {@link Component} that was built by the {@link JenkinsBuild} being deployed
     *
     * @return
     */
    public Component getComponent()
    {
        return Optional.ofNullable(this.build)
                .map(JenkinsBuild::getComponent)
                .orElse(null);
    }

    /**
     * Convenience method to safely access the name of the {@link Component} that was built and promoted
     *
     * @return The component name or "unknown" if something unexpected went wrong
     */
    public String getComponentName()
    {
        return Optional.ofNullable(this.build)
                .map(JenkinsBuild::getComponent)
                .map(Component::getName)
                .orElse("unknown");
    }

    /**
     * Convenience method to safely access the {@link Project} that the component being built and deployed belongs to
     *
     * @return The project, or null if something unexpected went wrong
     */
    public Project getProject()
    {
        return Optional.ofNullable(this.build)
                .map(JenkinsBuild::getComponent)
                .map(Component::getProject)
                .orElse(null);
    }

    /**
     * Convenience method to safely access the {@link Project#getKey() project key} that the component being built and
     * deployed belongs to
     *
     * @return The project key or "UNKNOWN" if something unexpected went wrong
     */
    public String getProjectKey()
    {
        return Optional.ofNullable(this.build)
                .map(JenkinsBuild::getComponent)
                .map(Component::getProject)
                .map(Project::getKey)
                .orElse("UNKNOWN");
    }

    /**
     * Convenience method to safely access the {@link JenkinsBuild#getNumber() build number} that is being promoted.
     *
     * @return The build number, or 0 if something unexpected went wrong
     */
    public int getBuildNumber()
    {
        return Optional.ofNullable(this.build)
                .map(JenkinsBuild::getNumber)
                .orElse(0);
    }


    public static RequiresEnvironment of(JenkinsBuild build)
    {
        return new Builder(build);
    }

    //region builder

    public interface OptionalParameters
    {
        OptionalParameters ofJobType(String jobType);

        OptionalParameters withDisplayName(String displayName);

        OptionalParameters queueId(int queueId);

        OptionalParameters performedOn(Server server);

        OptionalParameters triggeredBy(IdirUser user);

        JenkinsPromotion build();
    }

    public interface RequiresTriggeredBy
    {
        OptionalParameters triggeredByUsername(String username);
    }

    public interface RequiresResult
    {
        RequiresTriggeredBy result(JenkinsResult result);

        /**
         * Attempts to convert the string value of the build result to {@link JenkinsResult} value. If this parsing is
         * unsuccessful, a value of UNKNOWN will be assigned
         * @param result
         * @return
         */
        RequiresTriggeredBy result(String result);
    }

    public interface RequiresDuration
    {
        RequiresResult took(long duration);
    }

    public interface RequiresStartedTimestamp
    {
        RequiresDuration startedAt(long startedAt);
        RequiresDuration startedAt(LocalDateTime startedAt);
    }

    public interface RequiresBuildUrl
    {
        RequiresStartedTimestamp url(String url);
    }

    public interface RequiresNumber
    {
        /**
         * When Jenkins performs a promotion, it assigns an incrementing number as an identifier for that promotion
         *
         * @param number The Jenkins-assigned promotion number
         * @return
         */
        RequiresBuildUrl number(int number);
    }

    public interface RequiresEnvironment
    {
        /**
         * The name of the promotion process that was triggered
         *
         * @param environmentName
         * @return
         */
        RequiresNumber environment(String environmentName);
    }
    public static class Builder implements RequiresEnvironment, RequiresTriggeredBy, RequiresResult, RequiresDuration, RequiresStartedTimestamp, RequiresBuildUrl, RequiresNumber, OptionalParameters
    {
        private final JenkinsBuild build;

        private int number;
        private String environment;
        private String url;
        private long startedAt;
        private long duration;
        private String jobType;
        private String displayName;
        private int queueId;
        private Server server;
        private IdirUser triggeredBy;
        private JenkinsResult result;
        private String triggeredByUsername;

        Builder(JenkinsBuild build)
        {
            this.build = build;
        }

        @Override
        public OptionalParameters ofJobType(String jobType)
        {
            return null;
        }

        @Override
        public OptionalParameters withDisplayName(String displayName)
        {
            return null;
        }

        @Override
        public OptionalParameters queueId(int queueId)
        {
            return null;
        }

        @Override
        public OptionalParameters performedOn(Server server)
        {
            return null;
        }

        @Override
        public OptionalParameters triggeredBy(IdirUser user)
        {
            return null;
        }

        @Override
        public JenkinsPromotion build()
        {
            JenkinsPromotion promotion = new JenkinsPromotion(this.build, this.environment, this.number);
            promotion.setDuration(this.duration);
            promotion.setTriggeredByName(this.triggeredByUsername);
            promotion.setUrl(this.url);
            promotion.setTimestamp(this.startedAt);
            promotion.setResult(this.result);
            // TODO

            return promotion;
        }

        @Override
        public OptionalParameters triggeredByUsername(String username)
        {
           this.triggeredByUsername = username;
           return this;
        }

        @Override
        public RequiresTriggeredBy result(JenkinsResult result)
        {
            this.result = result;
            return this;
        }

        @Override
        public RequiresTriggeredBy result(String result)
        {
            try
            {
                this.result = JenkinsResult.valueOf(result.trim().toUpperCase());
            }
            catch (IllegalArgumentException | NullPointerException e)
            {
                this.result = JenkinsResult.UNKNOWN;
            }

            return this;
        }

        @Override
        public RequiresResult took(long duration)
        {
            this.duration = duration;
            return this;
        }

        @Override
        public RequiresDuration startedAt(long startedAt)
        {
            this.startedAt = startedAt;
            return this;
        }

        @Override
        public RequiresDuration startedAt(LocalDateTime startedAt)
        {
            this.startedAt = startedAt.atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            return this;
        }

        @Override
        public RequiresStartedTimestamp url(String url)
        {
            this.url = url;
            return this;
        }

        @Override
        public RequiresBuildUrl number(int number)
        {
            this.number = number;
            return this;
        }

        @Override
        public RequiresNumber environment(String environmentName)
        {
            this.environment = environmentName;
            return this;
        }
    }
    //endregion
}
