package ca.bc.gov.nrs.infra.cmdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("ca.bc.gov.nrs.cmdb.api.models")
public class CmdbApiApplication
{
    private static final Logger log = LoggerFactory.getLogger(CmdbApiApplication.class);

    public static void main(String[] args)
    {
        log.info("Starting application");
		SpringApplication.run(CmdbApiApplication.class, args);
	}
}
