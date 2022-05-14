package ai.ibytes.ingester.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.model.Site;

@Controller
public class IndexController {
    
    @Autowired
    private FileSystemStorageService storageService;
    
    @GetMapping( path = {"/", "/index.html"})
    public ModelAndView getIndexPage(Map<String, Object> model)   {
        List<Site> sites = storageService.getSites();
        model.put("sites",sites);

        return new ModelAndView("index", model);
    }
}
