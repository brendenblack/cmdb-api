package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.Secret;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SecretsRepository extends Neo4jRepository<Secret, Long>
{
}
