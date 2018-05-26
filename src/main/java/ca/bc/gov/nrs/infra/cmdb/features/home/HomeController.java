package ca.bc.gov.nrs.infra.cmdb.features.home;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController
{
    @Value("${cmdb.version}")
    private String appVersion;

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("version", this.appVersion);
        return "home/index";
    }

}
