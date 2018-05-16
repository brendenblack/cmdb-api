package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.io.Serializable;

@NodeEntity
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
