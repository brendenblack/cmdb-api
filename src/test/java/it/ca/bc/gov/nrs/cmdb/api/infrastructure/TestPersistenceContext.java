//package it.ca.bc.gov.nrs.cmdb.api.infrastructure;
//
//import org.junit.Rule;
//import org.neo4j.harness.junit.Neo4jRule;
//import org.neo4j.ogm.session.Session;
//import org.neo4j.ogm.session.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
//import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//@TestConfiguration
//@EnableNeo4jRepositories("ca.bc.gov.nrs.cmdb.api.repositories")
//@EnableTransactionManagement
//@ComponentScan("ca.bc.gov.nrs.infra.cmdb.domain.models")
//public class TestPersistenceContext
//{
//    private static final Logger log = LoggerFactory.getLogger(TestPersistenceContext.class);
//
//    @Bean
//    public SessionFactory getSessionFactory()
//    {
//        log.info("Creating TEST session factory");
//        return new SessionFactory(configuration(), "ca.bc.gov.nrs.infra.cmdb.domain.models");
//    }
//
//    @Bean
//    public Neo4jTransactionManager transactionManager() throws Exception
//    {
//        return new Neo4jTransactionManager(getSessionFactory());
//    }
//
//    @Bean
//    public org.neo4j.ogm.config.Configuration configuration()
//    {
//        log.info("Creating TEST configuration object");
//        org.neo4j.ogm.config.Configuration.Builder builder = new org.neo4j.ogm.config.Configuration.Builder();
//        return builder.build();
////        builder.uri(rule.boltURI())
////        .build();
//    }
//}
