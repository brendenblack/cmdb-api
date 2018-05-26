package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import java.io.Serializable;

/**
 * @deprecated implement an id field and annotate entities with @NodeEntity rather than deriving from this base class.
 * At some point, it just started causing NullPointerExceptions despite being fine for weeks.
 */
@Deprecated
public abstract class Entity implements Serializable
{
    @Id
    @GeneratedValue
    private Long id;

    public Long getId()
    {
        return id;
    }

}
