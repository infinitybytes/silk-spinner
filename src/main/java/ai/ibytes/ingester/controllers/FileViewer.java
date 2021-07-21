package ai.ibytes.ingester.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FileViewer {
    @Autowired
    private FileSystemStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/file.html")
    public ModelAndView getFileViewer(Principal user, @RequestParam("id") String id, Map<String, Object> model) {
        model.put("user",(user!=null) ? user.getName() : "ANON");

        log.info("Retrieving file ID {}", id);
        FileUpload fileJson = new FileUpload();

        try {
            fileJson = objectMapper.readValue(storageService.loadJson(id).toFile(), FileUpload.class);
        } catch (IOException e) {
            log.error("Unable to load the JSON for ID {}",id);
        }

        // Map
        model.put("json", fileJson);

        return new ModelAndView("fileViewer", model);
    }

    @GetMapping("/fileBytes.html")
    @ResponseBody FileSystemResource getFileResource(Principal user, @RequestParam("id") String id, HttpServletResponse response)   {
        log.info("Retrieving filebytes ID {}", id);
        FileUpload fileJson = new FileUpload();

        try {
            fileJson = objectMapper.readValue(storageService.loadJson(id).toFile(), FileUpload.class);
        } catch (IOException e) {
            log.error("Unable to load the JSON for ID {}",id);
        }

        // Load the actual file
        Resource videoFile = storageService.loadAsResource(fileJson.getFilename());

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename="+fileJson.getFilename().replace(" ", "_"));
        
        try {
            return new FileSystemResource(videoFile.getFile());
        } catch (IOException e) {
            log.error("Unable to get filebytes for ID {}", id);
            return null;
        }
    }
}