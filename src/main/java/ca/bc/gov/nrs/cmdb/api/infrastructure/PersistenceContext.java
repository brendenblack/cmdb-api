package ca.bc.gov.nrs.cmdb.api.infrastructure;

import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class PersistenceContext
{
    private static final Logger log = LoggerFactory.getLogger(PersistenceContext.class);

    @Bean
    public SessionFactory getSessionFactory()
    {
        log.info("Creating session factory");
        return new SessionFactory(configuration(), "ca.bc.gov.nrs.cmdb.api.models");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() throws Exception
    {
        return new Neo4jTransactionManager(getSessionFactory());
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration()
    {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .uri("bolt://localhost")
                .credentials("neo4j", "password")
                .build();
    }
}
