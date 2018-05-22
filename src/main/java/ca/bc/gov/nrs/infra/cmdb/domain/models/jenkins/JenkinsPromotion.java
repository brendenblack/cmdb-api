package ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Entity;
import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import lombok.*;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = false, of = { "build", "number" })
@Getter
public class JenkinsPromotion extends Entity
{
    public final static String RELATIONSHIP_PROMOTION_OF = "PROMOTION_OF";

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead
     */
    @Deprecated
    public JenkinsPromotion() {}

    JenkinsPromotion(JenkinsBuild build, int number)
    {
        this.build = build;
        this.number = number;
    }

    //region immutable fields
    @Relationship(type = RELATIONSHIP_PROMOTION_OF)
    @Required
    private JenkinsBuild build;

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
                .map(b -> b.getComponent())
                .orElse(null);
    }

    public static RequiresNumber of(JenkinsBuild build)
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
    public static class Builder implements RequiresTriggeredBy, RequiresResult, RequiresDuration, RequiresStartedTimestamp, RequiresBuildUrl, RequiresNumber, OptionalParameters
    {
        private final JenkinsBuild build;

        private int number;
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
            JenkinsPromotion promotion = new JenkinsPromotion(this.build, this.number);
            promotion.setDuration(this.duration);
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
    }
    //endregion
}
