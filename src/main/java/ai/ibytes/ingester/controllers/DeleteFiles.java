package ai.ibytes.ingester.controllers;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DeleteFiles {
    
    @Autowired
    private FileSystemStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/delete.html")
    public String deleteFile(@RequestParam("id") String id)  {
        log.warn("Deleting {}", id);

        // Get back metadata + filename
        try {
            FileUpload json = objectMapper.readValue(storageService.loadJson(id).toFile(), FileUpload.class);

            // Delete JSON and file
            storageService.deleteFile(json);
        } catch (IOException e) {
            log.error("Unable to load the JSON for ID {}",id);
            throw new StorageException("Unable to load the JSON for ID.", e);
        }

        return "redirect:/";
    }
}
