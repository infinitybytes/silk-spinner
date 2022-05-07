package ai.ibytes.ingester.storage;


import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.StorageConfig;

@Service
public class FileSystemStorageService {
	private final Path dataFiles;
	private final Path audioFiles;

	@Autowired
	public FileSystemStorageService(StorageConfig properties) {
		this.dataFiles = Paths.get(properties.getDataFiles());
		this.audioFiles = Paths.get(properties.getAudioFiles());
	}

	public Path getDataFilesPath()	{
		return this.dataFiles;
	}

	public Path getAudioFilesPath()	{
		return this.audioFiles;
	}

	public void store(String dirName, String fileName)	{
		
	}
}