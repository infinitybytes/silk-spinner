package ai.ibytes.ingester.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DeleteFiles {
    
    @Autowired
    private FileSystemStorageService storageService;

    @GetMapping("/delete.html")
    public String deleteFile(@RequestParam("id") String id)  {
        log.warn("Deleting {}", id);

        return "redirect:/";
    }
}
