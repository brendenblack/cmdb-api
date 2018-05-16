package ca.bc.gov.nrs.infra.cmdb.models;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.UUID;

@NodeEntity
public abstract class Entity
{
    @Id
    @GeneratedValue
    private Long id;

    public Long getId()
    {
        return id;
    }

}
