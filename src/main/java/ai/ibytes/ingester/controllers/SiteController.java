package ai.ibytes.ingester.controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.exceptions.StorageException;
import ai.ibytes.ingester.storage.model.Site;

@Controller
public class SiteController {
    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping( path = "/add-site.html")
    public ModelAndView getAddSitePage(Map<String, Object> model)   {
        model.put("site", Site.builder().name("").season("").dataLocation("").build());
        return new ModelAndView("manage-site", model);
    }

    @GetMapping( path = "/edit-site.html")
    public ModelAndView getEditSitePage(Map<String, Object> model, @RequestParam("id") String id)   {
        model.put("site", storageService.getSite(id));
        return new ModelAndView("manage-site", model);
    }

    @PostMapping( path = "/manage-site.html")
    public ModelAndView postSitePage(Map<String, Object> model, @ModelAttribute Site site)   {
        try {
            storageService.store(site);
        }
        catch(StorageException e)   {
            model.put("errors", Arrays.asList(new String[]{e.getMessage()}));
            model.put("site",site);
            return new ModelAndView("manage-site",model);
        }

        return new ModelAndView("redirect:/index.html", model);
    }

    @GetMapping( path = "/delete-site.html")
    public ModelAndView getAddSitePage(Map<String, Object> model, @RequestParam("id") String id)   {
        storageService.deleteSite(id);
        return new ModelAndView("redirect:/index.html", model);
    }

    @GetMapping( path = "/site.html")
    public ModelAndView getSitePage(Map<String, Object> model, @RequestParam("id") String id)   {
        model.put("site", storageService.getSite(id));
        return new ModelAndView("site", model);
    }
}
