package ai.ibytes.ingester.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.FileUpload;
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

        String dirname = (id.isPresent()) ? id.get() + '/' : "/";
        model.put("dirname", dirname);

        ftpClient.connect();
        model.put("datafiles", ftpClient.ls(dirname));
        ftpClient.disconnect();

        return new ModelAndView("index", model);
    }

    @GetMapping( path = "/site.html")
    public ModelAndView getSitePage(Principal user, Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");

        String dirname = (id.isPresent()) ? id.get() + '/' : "/";
        model.put("dirname", dirname);

        ftpClient.connect();
        model.put("datafiles", ftpClient.ls(dirname));
        ftpClient.disconnect();

        return new ModelAndView("site", model);
    }

    @GetMapping( path = "/datafiles.html")
    public ModelAndView getDataFilesPage(Principal user, Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        // @todo centralize
        model.put("user",(user!=null) ? user.getName() : "ANON");

        List<FileUpload> uploads = new ArrayList<>();
        storageService.loadAll(id.get()).forEach(file -> {
            try {
                FileUpload f = (FileUpload)objectMapper.readValue(file.toAbsolutePath().toFile(), FileUpload.class);
                uploads.add(f);
            } catch (IOException e) {
                log.error("Unable to read file tree.", e);
            }
        });
        model.put("uploads", uploads);

        return new ModelAndView("datafiles", model);
    }
}
