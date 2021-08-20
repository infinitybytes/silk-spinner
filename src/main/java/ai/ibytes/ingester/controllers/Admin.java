package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping(path="/acp-users.html")
    public ModelAndView createNewUser(Principal user, Map<String, Object> model, @RequestParam("username") String username, @RequestParam("pass1") String password)    {
        List<String> errors = new ArrayList<>();
        List<String> msgs = new ArrayList<>();

        if(username.isEmpty() || password.isEmpty())    {
            errors.add("Username and password cannot be blank.");
        }

        if(errors.size()>0) {
            model.put("errors", errors);
        }

        if(errors.size()==0)    {
            // Create user

            // Save to disk
        }

        return getUserPage(user, model);
    }

    @GetMapping( path = "/acp-xp.html")
    public ModelAndView getExProcPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        return new ModelAndView("acp-xp", model);
    }
}
