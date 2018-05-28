package ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins;

import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import ca.bc.gov.nrs.infra.cmdb.domain.models.Server;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = false, of = { "component", "number" })
@Getter
public class JenkinsBuild
{
    public static final String RELATIONSHIP_BUILD_OF = "BUILD_OF";
    public static final String RELATIONSHIP_BUILT_ON = "BUILT_ON";

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead
     */
    @Deprecated
    public JenkinsBuild() {}

    JenkinsBuild(Component component, int number)
    {
        this.component = component;
        this.number = number;
    }

    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;

    //region immutable fields
    @Relationship(type = RELATIONSHIP_BUILD_OF, direction = Relationship.OUTGOING)
    @Required
    @JsonManagedReference
    private Component component;

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

    @Relationship(type = RELATIONSHIP_BUILT_ON)
    @Setter
    private Server server;

    @Setter
    private int queueId;
    //endregion


    public LocalDateTime getStartedAt()
    {
        Instant instant = Instant.ofEpochMilli(this.timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }



    //region builder
    public static RequiresBuildNumber of(Component component)
    {
        return new Builder(component);
    }


    public interface OptionalParameters
    {
        OptionalParameters ofJobType(String jobType);

        OptionalParameters withDisplayName(String displayName);

        OptionalParameters queueId(int queueId);

        OptionalParameters performedOn(Server server);

        OptionalParameters triggeredBy(IdirUser user);

        JenkinsBuild build();
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

    public interface RequiresBuildNumber
    {
        /**
         * When Jenkins performs a build, it assigns an incrementing number as an identifier for that build
         *
         * @param number The Jenkins-assigned build number
         * @return
         */
        RequiresBuildUrl number(int number);
    }



    public static class Builder implements RequiresTriggeredBy, RequiresResult, RequiresDuration, RequiresStartedTimestamp, RequiresBuildUrl, RequiresBuildNumber, OptionalParameters
    {
        private final Logger log = LoggerFactory.getLogger(Builder.class);

        private final Component component;

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

        Builder(Component component)
        {
            this.component = component;
        }


        @Override
        public RequiresBuildUrl number(int number)
        {
            this.number = number;
            return this;
        }

        @Override
        public OptionalParameters ofJobType(String jobType)
        {
            this.jobType = jobType;
            return this;
        }

        @Override
        public OptionalParameters withDisplayName(String displayName)
        {
            this.displayName = displayName;
            return this;
        }

        @Override
        public OptionalParameters queueId(int queueId)
        {
            this.queueId = queueId;
            return this;
        }

        @Override
        public OptionalParameters performedOn(Server server)
        {
            this.server = server;
            return this;
        }

        @Override
        public JenkinsBuild build()
        {
            JenkinsBuild build = new JenkinsBuild(this.component, this.number);
            build.setTimestamp(this.startedAt);
            String display = StringUtils.isEmpty(this.displayName) ?
                    this.component.getProject().getKey() + "/" + this.component.getName() + " #" + this.number :
                    this.displayName;
            build.setDisplayName(display);
            build.setDuration(this.duration);
            build.setJobClass(this.jobType);
            build.setResult(this.result);
            build.setTriggeredByName(triggeredByUsername);
            build.setUrl(this.url);

            log.debug(build.toString());

            return build;
        }

        @Override
        public RequiresStartedTimestamp url(String url)
        {
            this.url = url;
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
            this.startedAt = startedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return this;
        }

        /**
         * Attaches the {@link IdirUser} responsible for triggering this build. If the {@link #triggeredByUsername} does
         * not match the User's IDIR or a known alias, it will be added as an alias.
         *
         * @param user
         * @return
         */
        @Override
        public OptionalParameters triggeredBy(IdirUser user)
        {
            if (!user.getId().equals(this.triggeredByUsername) && !user.getKnownAliases().contains(this.triggeredByUsername))
            {
                user.addAlias(this.triggeredByUsername);
            }

            this.triggeredBy = user;
            return this;
        }

        @Override
        public RequiresResult took(long duration)
        {
            this.duration = duration;
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
        public OptionalParameters triggeredByUsername(String username)
        {
            this.triggeredByUsername = username;
            return this;
        }
    }
    //endregion

}
