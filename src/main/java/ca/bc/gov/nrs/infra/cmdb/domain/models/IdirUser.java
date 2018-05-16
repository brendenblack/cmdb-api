package ca.bc.gov.nrs.infra.cmdb.domain.models;


import org.neo4j.ogm.annotation.Index;

import java.util.HashSet;
import java.util.Set;

public class IdirUser extends Entity
{
    @Index(unique = true)
    private String idir;

    private String email;

    private Set<String> knownAliases = new HashSet<>();
}
