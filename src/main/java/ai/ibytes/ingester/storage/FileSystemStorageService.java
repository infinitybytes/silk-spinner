package ai.ibytes.ingester.storage;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileSystemStorageService {
	private final Path dataFiles;
	private final Path analysisStore;
	private final Path tempStore;

	private final ObjectMapper objectMapper;

	@Autowired
	public FileSystemStorageService(StorageConfig properties) {
		this.dataFiles = Paths.get(properties.getDataFiles());
		this.analysisStore = Paths.get(properties.getAnalysisStore());
		this.tempStore = Paths.get(properties.getTempStore());

		objectMapper = new ObjectMapper();
	}

	public Path getDataFilesPath()	{
		return this.dataFiles;
	}

	public Path getAnalysisStorePath()	{
		return this.analysisStore;
	}

	public Path getTempStorePath()	{
		return this.tempStore;
	}

	public void store(String dirName, String fileName)	{
		// File is stored, create JSON record
		try {
			FileUpload fileUpload = new FileUpload();
			fileUpload.setOriginalPath(dirName);
			fileUpload.setFilename(fileName);
			fileUpload.setStatus(FileUpload.STATUS.UPLOADED);

			File sitePath = new File(this.analysisStore.toFile(), dirName);
			if(!sitePath.exists())	{
				FileUtils.forceMkdir(sitePath);
			}

			objectMapper.writeValue(
				new File(sitePath, fileName + ".json"),
				fileUpload		
			);
		} catch (IOException e) {
			throw new StorageException("Failed to write meta file after file upload.",e);
		}
	}

	public void save(FileUpload file)	{
		try {
			objectMapper.writeValue(loadJson(file.getOriginalPath(), file.getFilename()).toFile(), file);
		} catch (IOException e) {
			throw new StorageException("Failed to save meta file",e);
		}
	}

	public Stream<Path> loadAll(String dirName) {
		Path walkPath = Paths.get(this.analysisStore.toString(), dirName);

		try {
			return Files.walk(walkPath, 1)
				.filter(path -> !path.equals(this.analysisStore))
				.filter(path -> !path.getFileName().toString().endsWith("users.json"))
				.filter(path -> path.getFileName().toString().endsWith(".json"))
				.map(walkPath::resolve);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	public Path load(String filename) {
		return dataFiles.resolve(filename);
	}

	public Path loadJson(String dirName, String fileName)	{
		Path sitePath = Paths.get(this.analysisStore.toString(), dirName, fileName + ".json");
		return analysisStore.resolve(sitePath);
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = tempStore.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageException("Could not read file: " + filename, e);
		}
	}
}