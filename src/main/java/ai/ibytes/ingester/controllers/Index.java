package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;

@Controller
public class Index {
    
    @Autowired
    private FileSystemStorageService storageService;
    
    @GetMapping( path = {"/", "/index.html"})
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");
        List<FileUpload> uploads = new ArrayList<>();
        storageService.loadAll().forEach(file -> {
            uploads.add(new FileUpload(UUID.randomUUID().toString(), file.getFileName().toString(), "INGESTED"));
        });
        model.put("uploads", uploads);
        return new ModelAndView("index", model);
    }
}
