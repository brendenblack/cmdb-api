package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface JenkinsBuildRepository extends Neo4jRepository<JenkinsBuild, Long>
{
    Optional<JenkinsBuild> findByComponentNameAndNumber(String componentName, int number);

    Optional<JenkinsBuild> findByUrl(String url);

    boolean existsByComponentAndNumber(Component component, int number);
}
