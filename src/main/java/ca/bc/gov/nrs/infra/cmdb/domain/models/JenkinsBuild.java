package ca.bc.gov.nrs.infra.cmdb.domain.models;

import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.ComponentInstance;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@ToString
@EqualsAndHashCode(callSuper = false, of = { "component", "number" })
@Getter
public class JenkinsBuild extends Entity
{
    public static final String RELATIONSHIP_BUILD_OF = "BUILD_OF";
    public static final String RELATIONSHIP_BUILT_ON = "BUILT_ON";

    public static enum Result { SUCCESS, UNSTABLE, FAILURE, ABORTED, UNKNOWN };

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

    //region immutable fields
    @Relationship(type = RELATIONSHIP_BUILD_OF)
    @Required
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

        JenkinsBuild build();

        //.ofDuration(buildInfo.getDuration())
//                            .ofJenkinsJobType(buildInfo.getJobClass())
//                            .startedAt(buildInfo.getTimestamp())
//                            .ofDuration(buildInfo.getDuration())
//                            .withBuildUrl(buildInfo.getUrl())
//                            .withDisplayName(buildInfo.getDisplayName())
//                            .withQueueId(buildInfo.getQueueId())
//                            .build();
    }

    public interface RequiresTriggeredBy
    {
        OptionalParameters triggeredBy(IdirUser user);
    }

    public interface RequiresResult
    {
        RequiresTriggeredBy result(JenkinsBuild.Result result);
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
        private Result result;

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
            return null;
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
            build.setDisplayName(this.displayName);
            build.setDuration(this.duration);
            build.setJobClass(this.jobType);
            return null;
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

        @Override
        public OptionalParameters triggeredBy(IdirUser user)
        {
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
        public RequiresTriggeredBy result(JenkinsBuild.Result result)
        {
            this.result = result;
            return this;
        }
    }
    //endregion

}
