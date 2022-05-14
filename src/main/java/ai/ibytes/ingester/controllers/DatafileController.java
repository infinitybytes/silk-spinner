package ai.ibytes.ingester.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DatafileController {
    
    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping("/datafile-bytes.html")
    @ResponseBody FileSystemResource getFileBytes(@RequestParam("siteId") String siteId, @RequestParam("id") String id, HttpServletResponse response)   {
        // Load the actual file
        Resource videoFile = storageService.getDataFileBytes(siteId, id);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename="+videoFile.getFilename().replace(" ", "_"));
        
        try {
            return new FileSystemResource(videoFile.getFile());
        } catch (IOException e) {
            log.error("Unable to get filebytes for ID {}", id);
            return null;
        }
    }
}
