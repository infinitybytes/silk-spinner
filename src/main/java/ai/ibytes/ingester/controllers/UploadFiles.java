package ai.ibytes.ingester.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UploadFiles {

    @Autowired
    private StorageConfig storageConfig;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;
    
    @GetMapping(path="/upload.html")
    public ModelAndView getUploadPage(Principal user, Map<String, Object> model)   {
        model.put("user",(user!=null) ? user.getName() : "ANON");
        return new ModelAndView("uploadFiles", model);
    }

    @PostMapping(path="/upload.html")
    public ModelAndView handleFileUpload(Principal user, Map<String, Object> model, HttpServletRequest request)    {
        List<String> msgs = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterStream;
        try {
            iterStream = upload.getItemIterator(request);

            while(iterStream.hasNext()) {
                FileItemStream item = iterStream.next();
                
                InputStream stream = item.openStream();
                if(!item.isFormField()) {
                    log.info("Handling large ZIP upload: {}",item.getName());
                    OutputStream os = new FileOutputStream(new File(storageConfig.getDiskLocation(), item.getName()));
                    IOUtils.copy(stream, os);
                    log.info("Finished streaming ZIP to disk: {}/{}",storageConfig.getDiskLocation(), item.getName());
                    os.close();

                    msgs.add("Uploaded "+item.getName());

                    // store as a ref pointer
                    fileSystemStorageService.store(item.getName());
                }
    
                stream.close();
            }
        } catch (FileUploadException | IOException e) {
            log.error("Error uploading file",e);
            errors.add("Error uploading zip file: "+e.getMessage());
        }

        if(msgs.size()>0)   {
            model.put("msgs",msgs);
        }

        if(errors.size()>0) {
            model.put("errors",errors);
        }
        return getUploadPage(user, model);
    }
}
