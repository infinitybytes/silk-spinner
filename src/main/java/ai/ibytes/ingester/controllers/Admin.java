package ai.ibytes.ingester.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.model.SystemUser;
import ai.ibytes.ingester.storage.UserStoreService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Admin {
    @Autowired
    public InMemoryUserDetailsManager users;

    @Autowired
    public UserStoreService userStoreService;

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

        try {
            List<SystemUser> sysUsers = userStoreService.loadUsers();
            model.put("users",sysUsers);
        } catch (IOException e) {
            log.error("Unable to load user list.",e);
        }
        
        return new ModelAndView("acp-users", model);
    }

    @GetMapping( path = "/acp-users-delete.html")
    public ModelAndView deleteUser(Principal user, Map<String, Object> model, @RequestParam("u") String username)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");
        
        List<String> errors = new ArrayList<>();
        List<String> msgs = new ArrayList<>();

        users.deleteUser(username);

        // reload disk
        List<SystemUser> sysUsers;
        try {
            sysUsers = userStoreService.loadUsers();
            sysUsers.remove(new SystemUser(username, "", "ADMIN"));
            userStoreService.saveUsers(sysUsers);
        } catch (IOException e) {
            log.error("Unable to delete user from disk.",e);
            errors.add("Unable to delete user from disk.");
            model.put("errors",errors);
        }

        if(errors.size()==0) {
            msgs.add("Deleted user.");
            model.put("msgs",msgs);
        }

        return getUserPage(user, model);
    }

    @PostMapping(path="/acp-users.html")
    public ModelAndView createNewUser(Principal user, Map<String, Object> model, @RequestParam("username") String username, @RequestParam("pass1") String password)    {
        List<String> errors = new ArrayList<>();
        List<String> msgs = new ArrayList<>();

        if(username.isEmpty() || password.isEmpty())    {
            errors.add("Username and password cannot be blank.");
        }

        if(password.length()<8) {
            errors.add("Password length has to be at least eight characters or numbers.");
        }

        if(errors.size()==0)    {
            // Create user
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            String pass = encoder.encode(password);

            SystemUser newuser = new SystemUser(username, pass, "ADMIN");
            users.createUser(User.builder()
                .username(username)
                .password(pass)
                .roles("ADMIN")
                .build());

            // Save to disk
            try {
                List<SystemUser> sysUsers = userStoreService.loadUsers();
                sysUsers.add(newuser);
                userStoreService.saveUsers(sysUsers);

                msgs.add("New user added.");
                model.put("msgs", msgs);
            } catch (IOException e) {
                log.error("Unable to save new user.",e);
                errors.add("Unable to save new user.");
            } catch (Exception e) {
                log.error("Unable to save new user.",e);
                errors.add("Unable to save new user.");
            }
        }

        if(errors.size()>0) {
            model.put("errors", errors);
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
