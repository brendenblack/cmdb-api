package ca.bc.gov.nrs.cmdb.api.features.secrets;

import ca.bc.gov.nrs.cmdb.api.mediator.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/secrets")
public class SecretsController
{
    public static final String PATH = "/secrets";
    private static final Logger log = LoggerFactory.getLogger(SecretsController.class);
    private final Mediator mediator;

    @Autowired
    public SecretsController(Mediator mediator)
    {
        this.mediator = mediator;
    }

    @PostMapping
    public void create(@RequestBody Create.Command message, HttpServletResponse response)
    {
        long id = this.mediator.send(message, Long.class);

        response.setStatus(HttpStatus.CREATED.value());
        response.addHeader("Location", PATH + "/" + id);
    }


}
