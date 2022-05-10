package ai.ibytes.ingester.storage;


import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.StorageConfig;

@Service
public class FileSystemStorageService {
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private final Path dataFiles;

	@Autowired
	public FileSystemStorageService(StorageConfig properties) {
		this.dataFiles = Paths.get(properties.getDataFiles());
	}

	public Path getDataFilesPath()	{
		return this.dataFiles;
	}

	public void store(String dirName, String fileName)	{
		
	}
}