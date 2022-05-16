package ai.ibytes.ingester.storage;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.storage.exceptions.StorageException;
import ai.ibytes.ingester.storage.model.DataFile;
import ai.ibytes.ingester.storage.model.Site;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileSystemStorageService {
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private final Path dataFiles;

	@Autowired
	public FileSystemStorageService(StorageConfig properties) {
		this.dataFiles = Paths.get(properties.getDataFiles());

		if(!this.dataFiles.toFile().exists())	{
			try {
				Files.createDirectories(this.dataFiles);
			} catch (IOException e) {
				log.error("Unable to create the storage location",e);

				throw new StorageException("Unable to create storage location",e);
			}
		}
	}

	public Path getDataFilesPath()	{
		return this.dataFiles;
	}

	@Synchronized
	public void store(Site site)	{
		File siteLocation = new File(dataFiles.toFile(), "sites.json");
		List<Site> existingSites = new ArrayList<Site>();
		
		try {
			// Exists?
			if(siteLocation.exists())	{
				existingSites = Arrays.asList(
					objectMapper.readValue(siteLocation, Site[].class))
						.stream()
						.filter(s -> !s.getId().equals(site.getId()))
					.collect(Collectors.toList());
			}

			// Count raw source files
			if(!site.isRunningAnalysis())	{
				try (Stream<Path> files = Files.list(Paths.get(site.getDataLocation()))) {
					files.forEach(f -> {
						site.getDataFiles().add(
							DataFile.builder()
								.path(f.toFile().getPath())
								.name(f.toFile().getName())
							.build()
						);
					});

					site.setNumSourceFiles(site.getDataFiles().size());
				}
			}

			existingSites.add(site);
			objectMapper.writeValue(siteLocation, existingSites);
		} catch (IOException e) {
			log.error("{}: Error saving Site to disk",site.getId(), e);
			throw new StorageException("Error saving Site to disk",e);
		}
	}

	@Synchronized
	public void deleteSite(String id)	{
		File siteLocation = new File(dataFiles.toFile(), "sites.json");
		
		try {
			List<Site> existingSites = Arrays.asList(
				objectMapper.readValue(siteLocation, Site[].class))
					.stream()
					.filter(s -> !s.getId().equals(id))
				.collect(Collectors.toList());

			objectMapper.writeValue(siteLocation, existingSites);
		} catch (IOException e) {
			log.error("{}: Error deleting Site",id, e);
			throw new StorageException("Error deleting Site",e);
		}
	}

	public List<Site> getSites()	{
		File siteLocation = new File(dataFiles.toFile(), "sites.json");
		List<Site> sites = new ArrayList<Site>();
		
		try {
			sites = Arrays.asList(
				objectMapper.readValue(siteLocation, Site[].class))
					.stream()
				.collect(Collectors.toList());
		} catch (IOException e) {
			log.error("Error getting Sites from disk", e);
			throw new StorageException("Error getting Sites from disk",e);
		}

		return sites;
	}

	public Site getSite(String id)	{
		File siteLocation = new File(dataFiles.toFile(), "sites.json");
		Site site = new Site();
		try {
			site = (Site)Arrays.asList(
				objectMapper.readValue(siteLocation, Site[].class))
					.stream()
					.filter(s -> s.getId().equals(id))
					.findFirst()
				.get();
		} catch (IOException e) {
			log.error("{}: Error getting Site from disk",id, e);
			throw new StorageException("Error getting Site from disk",e);
		}

		return site;
	}

	public DataFile getDataFile(String siteId, String id)	{
		Site site = getSite(siteId);
		return site.getDataFiles()
				.stream()
				.filter(df -> df.getId().equals(id))
				.findFirst()
			.get();
	}

	@Synchronized
	public void storeDataFile(String siteId, DataFile dataFile)	{
		Site site = getSite(siteId);
		List<DataFile> dataFiles = site.getDataFiles()
			.stream()
			.filter(df -> !df.getId().equals(dataFile.getId()))
		.collect(Collectors.toList());

		dataFiles.add(dataFile);

		site.setDataFiles(dataFiles);

		store(site);
	}

	public Resource getDataFileBytes(String siteId, String id) {
		try {
			DataFile dataFile = getDataFile(siteId, id);

			Path file = Path.of(dataFile.getPath());
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageException(
						"Could not read file: " + dataFile.getName());

			}
		}
		catch (MalformedURLException e) {
			throw new StorageException("Could not read file", e);
		}
	}
}