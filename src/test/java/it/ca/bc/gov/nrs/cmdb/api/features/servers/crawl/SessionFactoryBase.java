package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import com.pastdev.jsch.DefaultSessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:application-test.properties")
public abstract class SessionFactoryBase
{
    private static final Logger log = LoggerFactory.getLogger(SessionFactoryBase.class);

    @Value("${ssh.linux.username}")
    protected String username;

    @Value("${ssh.linux.password}")
    protected String password;

    @Value("${ssh.linux.host}")
    protected String host;

    @Value("${ssh.linux.port}")
    protected int port;

    protected DefaultSessionFactory sessionFactory;

    @Before
    public void setup()
    {
        log.info("Creating session factory for {} with user {}", this.host, this.username);
        DefaultSessionFactory sessionFactory = new DefaultSessionFactory(this.username, this.host, this.port);
        sessionFactory.setPassword(this.password);
        sessionFactory.setConfig("StrictHostKeyChecking", "no"); // https://www.mail-archive.com/jsch-users@lists.sourceforge.net/msg00529.html
        this.sessionFactory = sessionFactory;
    }
}
