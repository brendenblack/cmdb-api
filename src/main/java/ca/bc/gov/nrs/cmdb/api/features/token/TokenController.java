package ca.bc.gov.nrs.cmdb.api.features.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public String get()
    {
        return "hello world";
    }
}
