package ai.ibytes.ingester.controllers;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.net.ftp.FTPFile;
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

    @GetMapping( path = "/analysis.html")
    public ModelAndView getIndexPage(Map<String, Object> model, @RequestParam("id") Optional<String> id) throws IOException   {
        // Run analysis
        analyze(id.get());

        model.put("message", "Analysis complete.");
        return new ModelAndView("analysis-start", model);
    }

    private void analyze(String directory)   {
        ftpClient.connect();
        try {
            List<FTPFile> datafiles = ftpClient.ls(directory);

            // Submit the analyzer to a threadpool
            datafiles.stream().forEach(file -> {
                // skip goback
                if(!file.getName().endsWith(".")) {
                    // directory? recurse
                    if(file.isDirectory())  {
                        analyze(directory + '/' + file.getName());
                    }
                    else    {
                        File localFile = new File(storageConfig.getTempStore(), file.getName());
                        if(!localFile.exists())  {
                            ftpClient.getRemote(directory + '/' + file.getName(), localFile);

                            // submit to the analyzer
                            DataFile df = new DataFile();
                            df.setDirName(directory);
                            df.setName(localFile.getName());
                            df.setLocalTempFile(localFile.toPath());

                            analyzeAudio.setDataFile(df);

                            // create json data file locally
                            storageService.store(directory,file.getName());

                            // send to analyzer
                            analyzeAudio.run();
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error listing remote dir",e);
        } 
        ftpClient.disconnect();
    }
}
