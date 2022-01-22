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
import org.springframework.util.StringUtils;
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
    public ModelAndView getIndexPage(Principal user, Map<String, Object> model, @RequestParam("id") Optional<String> id) throws IOException   {
        model.put("user",(user!=null) ? user.getName() : "ANON");

        if(id.get().endsWith(".WAV"))   {
            // single
            File localFile = new File(storageConfig.getTempStore(), id.get().substring(id.get().lastIndexOf("/")));
            if(!localFile.exists())  {
                ftpClient.connect();
                ftpClient.getRemote(id.get(), localFile);
                ftpClient.disconnect();

                // submit to the analyzer
                DataFile file = new DataFile();
                file.setName(localFile.getName());
                file.setLocalTempFile(localFile.toPath());
                analyzeAudio.setDataFile(file);

                // create json data file locally
                storageService.store(id.get(), file.getName());

                // send to analyzer
                analyzeAudio.run();
            }
        }
        else    {
            try {
                ftpClient.connect();
                List<FTPFile> datafiles = ftpClient.ls(id.get());
                ftpClient.disconnect();

                // Submit the analyzer to a threadpool
                datafiles.stream().forEach(file -> {
                    // skip goback
                    if(!file.getName().endsWith(".")) {
                        // recurse
                        try {
                            getIndexPage(user, model, Optional.of(id.get() + '/' + file.getName()));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                log.error("Error listing remote dir",e);
            } 
        }

        return new ModelAndView("index", model);
    }
}
