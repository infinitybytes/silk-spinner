package ai.ibytes.ingester.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.model.Site;

@Controller
public class SiteController {
    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping( path = "/add-site.html")
    public ModelAndView getAddSitePage(Map<String, Object> model)   {
        return new ModelAndView("add-site", model);
    }

    @PostMapping( path = "/add-site.html")
    public ModelAndView postAddSitePage(Map<String, Object> model, @ModelAttribute Site site)   {
        storageService.store(site);
        return new ModelAndView("redirect:/index.html", model);
    }

    @GetMapping( path = "/delete-site.html")
    public ModelAndView getAddSitePage(Map<String, Object> model, @RequestParam("id") String id)   {
        storageService.deleteSite(id);
        return new ModelAndView("redirect:/index.html", model);
    }
}
