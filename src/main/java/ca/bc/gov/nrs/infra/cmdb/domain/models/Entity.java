package ca.bc.gov.nrs.infra.cmdb.domain.models;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import java.io.Serializable;

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
