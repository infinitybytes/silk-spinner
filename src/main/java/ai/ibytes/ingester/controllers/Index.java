package ai.ibytes.ingester.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.storage.FileSystemStorageService;

@Controller
public class Index {
    
    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private StorageConfig storageConfig;
    
    @GetMapping( path = {"/", "/index.html"})
    public ModelAndView getIndexPage(Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        String dirname = (id.isPresent()) ? id.get() + '/' : "/";
        model.put("dirname", dirname);

        return new ModelAndView("index", model);
    }

    @GetMapping( path = "/site.html")
    public ModelAndView getSitePage(Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        String dirname = (id.isPresent()) ? id.get() + '/' : "/";
        model.put("dirname", dirname);

        return new ModelAndView("site", model);
    }
}
