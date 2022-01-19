package ai.ibytes.ingester.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import ai.ibytes.ingester.config.AuthConfig;
import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.DataFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

@Service
@Slf4j
public class FtpClient {
    @Autowired    
    private AuthConfig authConfig;

    @Autowired
    private StorageConfig storageConfig;

    private static FTPClient ftp = new FTPClient();

    /**
     * 
     * @throws SocketException
     * @throws IOException
     */
    public void connect() throws SocketException, IOException   {
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        
        ftp.connect(storageConfig.getFtpHost(), 21);
        if( !FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            log.error("Unable to connect to FTP: {}", ftp.getReplyString());
            ftp.disconnect();
            throw new ConnectException();
        }

        ftp.enterLocalPassiveMode();
        if(!ftp.login(authConfig.getUsername(), authConfig.getPassword()))  {
            log.error("Unable to login to FTP: {}", ftp.getReplyString());
            ftp.disconnect();
            throw new AuthenticationException();
        }
    }

    /**
     * 
     * @param dirName
     * @return
     * @throws IOException
     */
    public List<DataFile> ls(String dirName) throws IOException {
        List<FTPFile> files = Arrays.asList(ftp.listFiles(storageConfig.getDataFiles() + dirName));
        List<DataFile> dataFiles = new ArrayList<>();
        files.stream().forEach(file -> {
            if(!file.getName().equals(".")) {
                DataFile df = new DataFile();
                df.setRawFile(file);
                df.setSlug(dirName);
                dataFiles.add(df);
            }
        });
        return dataFiles;
    }

    public List<DataFile> ls() throws IOException   {
        return ls("");
    }

    public void getRemote(DataFile file, File tempLocation) throws IOException   {
        FileOutputStream local = new FileOutputStream(tempLocation);
        boolean downloaded = ftp.retrieveFile(storageConfig.getDataFiles() + file.getSlug() + '/' + file.getName(), local);
        local.flush();
        local.close();

        if(!downloaded) {
            log.error("Error downloading data file: {}", file);
            throw new IOException("Error downloading data file");
        }
    }

    /**
     * 
     * @throws IOException
     */
    public void disconnect() throws IOException    {
        ftp.logout();
        ftp.disconnect();
    }
}
