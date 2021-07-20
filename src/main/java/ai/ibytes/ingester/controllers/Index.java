package ai.ibytes.ingester.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Index {
    
    @Autowired
    private FileSystemStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping( path = {"/", "/index.html"})
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        List<FileUpload> uploads = new ArrayList<>();
        storageService.loadAll().forEach(file -> {
            try {
                FileUpload f = (FileUpload)objectMapper.readValue(file.toAbsolutePath().toFile(), FileUpload.class);
                uploads.add(f);
            } catch (IOException e) {
                log.error("Unable to read file tree.", e);
            }
        });
        model.put("uploads", uploads);
        return new ModelAndView("index", model);
    }
}
