package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.Connection;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ConnectionRepository extends Neo4jRepository<Connection, Long>
{
}
