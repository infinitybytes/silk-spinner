package ai.ibytes.ingester.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class Export {
    @Autowired
    private FileSystemStorageService storageService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/export-all.html")
    public void getSiteCsv(Principal user, @RequestParam("id") String id, HttpServletResponse response)   {
        log.info("Exporting to CSV SITE ID {}", id);
        
        List<FileUpload> fileUploads = new ArrayList<>();
        storageService.loadAll(id).forEach(file -> {
            try {
                FileUpload f = (FileUpload)objectMapper.readValue(file.toAbsolutePath().toFile(), FileUpload.class);
                fileUploads.add(f);
            } catch (IOException e) {
                log.error("Unable to read file tree.", e);
            }
        });

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename="+id+".csv");
        
        CsvMapper csvMapper = new CsvMapper();
        Builder csvSchemaBuilder = CsvSchema.builder();
        CsvSchema schema = csvSchemaBuilder
            .addColumn("originalPath")
            .addColumn("filename")
            .addColumn("status")
            .addColumn("waveform", ColumnType.BOOLEAN)
            .addColumn("voiceDetected", ColumnType.BOOLEAN)
            .addColumn("voiceDetectTimes").build().withHeader();
        
        try {
            csvMapper
                .writerFor(List.class)
                .with(schema)
                .writeValue(response.getWriter(), fileUploads);
        } catch (IOException e) {
           log.error("Error creating SITE CSV",e);
        }
    }
}
