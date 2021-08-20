package ai.ibytes.ingester.storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.model.SystemUser;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserStoreService {
    @Autowired
    private FileSystemStorageService storageService;

	private ObjectMapper objectMapper = new ObjectMapper();

    public List<SystemUser> loadUsers() throws JsonParseException, JsonMappingException, IOException   {
        log.info("Loading users from disk.");
		return Arrays.asList(objectMapper.readValue(storageService.load("users.json").toFile(), SystemUser[].class));
    }

    public void saveUsers(List<SystemUser> users) throws JsonGenerationException, JsonMappingException, IOException {
        log.info("Saving users to disk.");
        objectMapper.writeValue(
            new File(storageService.getRootLocation().toFile(), "users.json"),
			users.toArray()
        );
    }
}
