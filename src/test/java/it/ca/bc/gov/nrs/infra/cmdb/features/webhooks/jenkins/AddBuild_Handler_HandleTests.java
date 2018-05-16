package it.ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import org.junit.Before;
import org.junit.Rule;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.ogm.session.Session;

public class AddBuild_Handler_HandleTests
{
    @Rule
    public Neo4jRule neoServer = new Neo4jRule();
    private Session session;

    @Before
    public void setup() throws Exception
    {

    }
}
