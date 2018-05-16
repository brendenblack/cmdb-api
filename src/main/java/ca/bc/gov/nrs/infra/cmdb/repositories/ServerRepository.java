package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.Server;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface ServerRepository extends Neo4jRepository<Server, Long>
{
    Optional<Server> findByFqdn(String fqdn);
}
