package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.Secret;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SecretsRepository extends Neo4jRepository<Secret, Long>
{
}
