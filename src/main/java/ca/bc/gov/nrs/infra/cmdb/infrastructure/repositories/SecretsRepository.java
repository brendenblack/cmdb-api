package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Secret;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SecretsRepository extends Neo4jRepository<Secret, Long>
{
}
