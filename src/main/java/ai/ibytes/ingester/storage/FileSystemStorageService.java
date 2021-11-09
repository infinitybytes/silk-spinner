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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.FileUpload;
import ai.ibytes.ingester.storage.exceptions.StorageException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileSystemStorageService {

	private final Path rootLocation;
	private final Path zipLocation;
	private final Path tempLocation;

	private final ObjectMapper objectMapper;

	@Autowired
	public FileSystemStorageService(StorageConfig properties) {
		this.rootLocation = Paths.get(properties.getDiskLocation());
		this.zipLocation = Paths.get(properties.getZipLocation());
		this.tempLocation = Paths.get(properties.getTempLocation());

		objectMapper = new ObjectMapper();
	}

	public Path getRootLocation()	{
		return this.rootLocation;
	}

	public Path getTempLocation()	{
		return this.tempLocation;
	}

	public Path getZipLocation()	{
		return this.zipLocation;
	}

	public void store(String fileName)	{
		// File is stored, create JSON record
		String id = UUID.randomUUID().toString();
		try {
			FileUpload fileUpload = new FileUpload();
			fileUpload.setId(id);
			fileUpload.setFilename(fileName);
			fileUpload.setStatus(FileUpload.STATUS.UPLOADED);
			objectMapper.writeValue(
				new File(this.rootLocation.toFile(), id + ".json"),
				fileUpload		
			);
		} catch (IOException e) {
			throw new StorageException("Failed to write meta file after file upload.",e);
		}
	}

	public void store(File file) {		
		if (!file.exists()) {
			throw new StorageException("Failed to store non-existant file.");
		}

		// Only allow WAV from temp
		if(file.getName().toUpperCase().endsWith(".WAV")) {
			Path destinationFile = this.rootLocation.resolve(
				Paths.get(file.getName()))
				.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			
			// Move on disk
			file.renameTo(destinationFile.toFile());

			// File is stored, create JSON record
			String id = UUID.randomUUID().toString();
			try {
				FileUpload fileUpload = new FileUpload();
				fileUpload.setId(id);
				fileUpload.setFilename(file.getName());
				fileUpload.setStatus(FileUpload.STATUS.UPLOADED);
				objectMapper.writeValue(
					new File(this.rootLocation.toFile(), id + ".json"),
					fileUpload		
				);
			} catch (IOException e) {
				throw new StorageException("Failed to write meta file after file upload.",e);
			}
		}
		else {
			throw new StorageException("Unsupported file type.");
		}
	}

	@Synchronized
	public void save(FileUpload fileUpload)	{
		log.info("Saving {}.json", fileUpload.getId());
		log.debug("File: {}", fileUpload.toString());

		try {
			objectMapper.writeValue(
					new File(this.rootLocation.toFile(), fileUpload.getId() + ".json"),
					fileUpload
			);
		} catch (IOException e) {
			log.error("Unable to save file metadata: {}",e);
			throw new StorageException("Failed to save file.",e);
		}
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.filter(path -> !path.getFileName().toString().endsWith("users.json"))
				.filter(path -> path.getFileName().toString().endsWith(".json"))
				.map(this.rootLocation::resolve);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	public Stream<Path> loadZipFromTemp() {
		try {
			return Files.walk(this.tempLocation, 1)
				.filter(path -> !path.equals(this.tempLocation))
				.filter(path -> path.getFileName().toString().toUpperCase().endsWith(".ZIP"))
				.map(this.tempLocation::resolve);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored temp files", e);
		}
	}

	public Stream<Path> loadWavFromTemp() {
		try {
			return Files.walk(this.tempLocation, Integer.MAX_VALUE)
				.filter(path -> !path.equals(this.tempLocation))
				.filter(path -> !path.getFileName().toString().toUpperCase().endsWith(".ZIP"))
				.map(this.tempLocation::resolve);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored temp files", e);
		}
	}

	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	public Path loadJson(String id)	{
		return load( id + ".json" );
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
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

	public void deleteFile(FileUpload json)	{
		try {
			Files.deleteIfExists(this.rootLocation.resolve(json.getFilename()));
		} catch (IOException e) {
			log.error("Unable to delete file for ID {}",json.getId());
			throw new StorageException("Unable to delete file",e);
		}

		try {
			Files.deleteIfExists(this.rootLocation.resolve( json.getId() + ".json" ));
		} catch (IOException e) {
			log.error("Unable to delete JSON for ID {}",json.getId());
			throw new StorageException("Unable to delete JSON",e);
		}
	}
}