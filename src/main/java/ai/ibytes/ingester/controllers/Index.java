package ai.ibytes.ingester.controllers;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.util.FtpClient;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Index {
    
    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private FtpClient ftpClient;

    @Autowired
    private StorageConfig storageConfig;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping( path = {"/", "/index.html"})
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");

        try {
            ftpClient.connect();
            model.put("ftpfiles", (id.isPresent()) ? ftpClient.ls(id.get()) : ftpClient.ls());
            ftpClient.disconnect();
        } catch (Exception e) {
            log.error("Error listing remote dir",e);
        }

        return new ModelAndView("index", model);
    }
}
