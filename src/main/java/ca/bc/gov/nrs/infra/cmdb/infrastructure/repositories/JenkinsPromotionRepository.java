package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface JenkinsPromotionRepository extends Neo4jRepository<JenkinsPromotion, Long>
{
    Optional<JenkinsPromotion> findByComponentNameAndBuildNumberAndPromotionNumber(String componentName, int buildNumber, int promotionNumber);
}
