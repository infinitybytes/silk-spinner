package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UploadFiles {
    
    @GetMapping("/upload.html")
    public ModelAndView getUploadPage(Principal user, Map<String, Object> model)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");
        
        return new ModelAndView("uploadFiles", model);
    }
}
