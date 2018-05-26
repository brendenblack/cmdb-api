package ca.bc.gov.nrs.infra.cmdb.features.token;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController
{
    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    public TokenController()
    {

    }

    @GetMapping
    @PreAuthorize(Roles.HAS_ROLE_SERVICE)
    public String getServiceRole()
    {
        return "hello world";
    }

    @GetMapping("/user")
    @PreAuthorize(Roles.HAS_ROLE_USER)
    public String getUserRole()
    {
        return "hello, user";
    }
}
