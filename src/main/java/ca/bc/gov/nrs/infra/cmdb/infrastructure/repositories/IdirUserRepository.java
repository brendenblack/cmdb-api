package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.IdirUser;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface IdirUserRepository extends Neo4jRepository<IdirUser, Long>
{
    Optional<IdirUser> findByIdir(String idir);
}
