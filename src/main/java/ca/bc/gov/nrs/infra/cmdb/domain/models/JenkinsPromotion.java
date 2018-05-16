package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

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


    public static RequiresNumber of(JenkinsBuild build)
    {
        return new Builder(build);
    }

    public interface RequiresNumber
    {
        OptionalParameters number(int number);
    }

    public interface OptionalParameters
    {
        JenkinsPromotion build();
    }

    public static class Builder implements RequiresNumber, OptionalParameters
    {
        private final JenkinsBuild build;
        private int number;

        Builder(JenkinsBuild build)
        {
            this.build = build;
        }

        @Override
        public JenkinsPromotion build()
        {
            JenkinsPromotion promotion = new JenkinsPromotion(this.build, this.number);

            return promotion;
        }

        @Override
        public OptionalParameters number(int number)
        {
            this.number = number;
            return this;
        }
    }
}
