package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UploadFiles {

    @Autowired
    private FileSystemStorageService storageService;
    
    @GetMapping(path="/upload.html")
    public ModelAndView getUploadPage(Principal user, Map<String, Object> model)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");
        return new ModelAndView("uploadFiles", model);
    }

    @PostMapping(path="/upload.html")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files)    {
        Arrays.asList(files).parallelStream().forEach(file -> {
            if(file.getOriginalFilename().endsWith(".zip")) {
                log.info("Handling ZIP");
            }
            else {
                storageService.store(file);
            }
        });
        return "redirect:/index.html";
    }
}
