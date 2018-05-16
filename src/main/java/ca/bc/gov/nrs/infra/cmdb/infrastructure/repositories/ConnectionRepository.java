package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Connection;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ConnectionRepository extends Neo4jRepository<Connection, Long>
{
}
