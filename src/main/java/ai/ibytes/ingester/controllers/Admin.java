package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Admin {
    @GetMapping( path = "/acp.html")
    public ModelAndView getAdminPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        return new ModelAndView("acp", model);
    }

    @GetMapping( path = "/acp-users.html")
    public ModelAndView getUserPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        return new ModelAndView("acp-users", model);
    }

    @GetMapping( path = "/acp-xp.html")
    public ModelAndView getExProcPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        return new ModelAndView("acp-xp", model);
    }
}
