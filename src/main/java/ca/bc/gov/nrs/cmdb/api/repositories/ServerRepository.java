package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.Server;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface ServerRepository extends Neo4jRepository<Server, Long>
{
    Optional<Server> findByFqdn(String fqdn);
}
