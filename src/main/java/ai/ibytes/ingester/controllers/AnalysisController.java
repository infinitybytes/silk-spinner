package ai.ibytes.ingester.controllers;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.storage.model.DataFile;
import ai.ibytes.ingester.storage.model.Site;
import ai.ibytes.ingester.tasks.ConvertAudio;
import ai.ibytes.ingester.tasks.DetectHumanVoice;
import ai.ibytes.ingester.tasks.GenerateAudioStats;
import ai.ibytes.ingester.util.Status;

@Controller
public class AnalysisController {
    private ExecutorService analysisExecutors = Executors.newFixedThreadPool(4);

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private ConvertAudio convertAudio;

    @Autowired
    private GenerateAudioStats generateAudioStats;

    @Autowired
    private DetectHumanVoice detectHumanVoice;

    @GetMapping( path = "/analyze-file.html")
    public ModelAndView getAnalyzeFile(Map<String, Object> model, @RequestParam("siteId") String siteId, @RequestParam("id") String id)   {
        // Get the datafile
        Site site = storageService.getSite(siteId);
        model.put("site",site);

        DataFile dataFile = storageService.getDataFile(siteId, id);

        // Convert
        convertAudio.convert(dataFile);

        // Generate audio stats
        generateAudioStats.generate(dataFile);

        // Detect voice
        detectHumanVoice.detectVoice(dataFile);

        // Change status
        dataFile.setStatus(Status.ANALYZED);

        // save back
        storageService.storeDataFile(siteId, dataFile);
        
        // Send to model
        model.put("dataFile",dataFile);

        return new ModelAndView("datafile", model);
    }

    @GetMapping( path="/analyze-site.html")
    public ModelAndView getAnalyzeSite(Map<String, Object> model, @RequestParam("siteId") String siteId) {
        Site site = storageService.getSite(siteId);

        // if(site.isRunningAnalysis())    {
        //     model.put("msgs", Arrays.asList(new String[]{"Analysis currently running, refresh the page for progress"}));

        //     // Still running?
        //     if(site.getNumAnalyzedFiles() == site.getNumSourceFiles())  {
        //         site.setRunningAnalysis(false);

        //         storageService.store(site);
        //     }
        // }
        // else    {
        //     // Mark as running
        //     site.setRunningAnalysis(true);  
        //     storageService.store(site);

            site.getDataFiles().stream().filter(d -> d.getStatus().equals(Status.NEW)).forEach(d -> {
               // analysisExecutors.submit(new Runnable() {
                //    @Override
                //    public void run() {
                        DataFile dataFile = storageService.getDataFile(siteId, d.getId());

                        // Convert audio and save in obj
                        convertAudio.convert(dataFile);
    
                        // Generate audio stats
                        generateAudioStats.generate(dataFile);
    
                        // Detect voice
                        detectHumanVoice.detectVoice(dataFile);

                        // Change status
                        dataFile.setStatus(Status.ANALYZED);

                        // save back
                        storageService.storeDataFile(siteId, dataFile);
              //      }
              //  });
            });
         
        //     model.put("msgs", Arrays.asList(new String[]{"Finished analysis."}));
        // }

        model.put("site",site);
        return new ModelAndView("site",model);
    }
}
