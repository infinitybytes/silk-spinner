package ai.ibytes.ingester.controllers;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.DataFile;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.tasks.AnalyzeAudio;
import ai.ibytes.ingester.util.FtpClient;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Analysis {
    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private FtpClient ftpClient;

    @Autowired
    private StorageConfig storageConfig;

    @Autowired
    private AnalyzeAudio analyzeAudio;
    
    private ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(12);

    @GetMapping( path = "/analysis.html")
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model, @RequestParam("id") Optional<String> id)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");

        try {
            ftpClient.connect();
            List<DataFile> datafiles = ftpClient.ls(id.get());
            
            // Submit the analyzer to a threadpool
            datafiles.stream().forEach(file -> {
                // skip goback
                if(!file.getName().endsWith("Go Back")) {
                    // download file to temp
                    try {
                        // create json data file locally
                        storageService.store(file.getName());

                        // create local temp file
                        File localFile = new File(storageConfig.getTempStore(), file.getName());
                        ftpClient.getRemote(file, localFile);

                        // submit to the analyzer
                        file.setLocalTempFile(localFile.toPath());
                        analyzeAudio.setDataFile(file);

                        // submit to threadpool
                        poolExecutor.submit(analyzeAudio);
                    } catch (Exception e) {
                        log.error("Unable to get remote file: {}",file.getRawFile(),e);
                    }
                }
            });

            ftpClient.disconnect();
        } catch (Exception e) {
            log.error("Error listing remote dir",e);
        }

        return new ModelAndView("index", model);
    }
}
