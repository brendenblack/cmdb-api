package ca.bc.gov.nrs.infra.cmdb.domain.models.irs;

import ca.bc.gov.nrs.infra.cmdb.domain.models.*;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import net.sourceforge.plantuml.StringUtils;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

/**
 * A component is a logical entity that belongs to a project and generally maps to a repository. They can be built,
 * deployed and manifested as a service.
 *
 * @see Project
 * @see ComponentInstance
 * @see JenkinsBuild
 * @see JenkinsPromotion
 */
@Getter
@NodeEntity
@ToString(of = "name")
@EqualsAndHashCode(of = "name", callSuper = false)
public class Component
{
    public final static String RELATIONSHIP_IS_OF_TYPE = "IS_OF_TYPE";

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead
     */
    @Deprecated
    public Component() {}

    Component(Project project, String name)
    {
        this.project = project;
        this.name = name;
    }

    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;

    /**
     * The project that this component belongs to
     */
    @Relationship(type = Project.RELATIONSHIP_HAS_COMPONENTS, direction = Relationship.INCOMING)
    @Required
    @JsonBackReference
    private Project project;

    /**
     * Builds created of this component
     */
//    @JsonBackReference
//    @Relationship(type = JenkinsBuild.RELATIONSHIP_BUILD_OF, direction = Relationship.INCOMING)
//    private Set<JenkinsBuild> builds;

    /**
     * The unique name of this component
     */
    @Index(unique = true)
    private String name;




    public static Builder ofName(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("");
        }

        return new Builder(name);
    }

    //region builder
    public static class Builder
    {
        private String name;
        private Project project;

        Builder(String name)
        {
            this.name = name.toLowerCase();
        }

        public Builder belongsTo(Project project)
        {
            this.project = project;
            return this;
        }

        public Component build()
        {
            return new Component(this.project, this.name);
        }


    }
    //endregion

}
