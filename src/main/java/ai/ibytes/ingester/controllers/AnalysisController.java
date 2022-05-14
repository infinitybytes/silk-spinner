package ai.ibytes.ingester.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.model.DataFile;
import ai.ibytes.ingester.storage.model.Site;
import ai.ibytes.ingester.tasks.GenerateWaveForm;

@Controller
public class AnalysisController {
    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private GenerateWaveForm waveFormTask;

    @GetMapping( path = "/analyze-file.html")
    public ModelAndView getAnalyzeFile(Map<String, Object> model, @RequestParam("siteId") String siteId, @RequestParam("id") String id)   {
        // Get the datafile
        Site site = storageService.getSite(siteId);
        model.put("site",site);

        DataFile dataFile = storageService.getDataFile(siteId, id);

        // Generate a waveform
        waveFormTask.generate(dataFile);

        model.put("dataFile",dataFile);

        return new ModelAndView("datafile", model);
    }
}
