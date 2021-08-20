package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Profile {

    @Autowired
    public InMemoryUserDetailsManager users;

    @GetMapping( path = "/profile.html")
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        return new ModelAndView("profile", model);
    }

    @PostMapping(path="/profile.html")
    public ModelAndView handlePasswordChange(Principal user, Map<String, Object> model, @RequestParam("pass1") String pass1, @RequestParam("pass2") String pass2)    {
        List<String> errors = new ArrayList<>();
        List<String> msgs = new ArrayList<>();

        if(pass1.isEmpty() || pass2.isEmpty()) {
            errors.add("Password cannot be blank.");
        } 
        
        if(pass1.length()<8)  {
            errors.add("Password length has to be at least eight characters or numbers.");
        }

        // Compare passwords
        if(!pass1.equals(pass2))    {
            // register error
            errors.add("Passwords much match.");
        }

        if(errors.size()>0) {
            model.put("errors",errors);
        }

        // save to user DB
        if(errors.size()==0)  {
            msgs.add("Password successfully changed.");

            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            
            UserDetails sysUser = users.loadUserByUsername(user.getName());
            users.changePassword(sysUser.getPassword(), encoder.encode(pass1) );

            model.put("msgs", msgs);
        }

        return getIndexPage(user, model);
    }
}
