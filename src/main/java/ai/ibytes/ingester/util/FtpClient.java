package ai.ibytes.ingester.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.ibytes.ingester.config.AuthConfig;
import ai.ibytes.ingester.config.StorageConfig;
import ai.ibytes.ingester.model.DataFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamException;

@Service
@Slf4j
public class FtpClient {
    @Autowired    
    private AuthConfig authConfig;

    @Autowired
    private StorageConfig storageConfig;

    private FTPClient ftp = new FTPClient();

    /**
     * 
     * @throws SocketException
     * @throws IOException
     */
    public void connect()  {
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    
        try {
            ftp.connect(storageConfig.getFtpHost(), 21);
            if( !FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                log.error("Unable to connect to FTP: {}", ftp.getReplyString());
            }
            if(!ftp.login(authConfig.getUsername(), authConfig.getPassword()))  {
                log.error("Unable to login to FTP: {}", ftp.getReplyString());
            }
        } catch (IOException e) {
            log.error("Error trying to connect",e);
        }
    }

    /**
     * 
     * @param dirName
     * @return
     * @throws IOException
     */
    public List<FTPFile> ls(String dirName) {
        List<FTPFile> files = new ArrayList<>();
        try {
            files = Arrays.asList(ftp.listFiles(storageConfig.getDataFiles() + dirName));
        } catch (IOException e) {
           log.error("Error trying to list files @ {}",dirName, e);
        }

        return files;
    }

    public List<FTPFile> ls()   {
        return ls("");
    }

    public void getRemote(String file, File tempLocation)    {
        try (FileOutputStream local = new FileOutputStream(tempLocation)) {
            boolean downloaded = ftp.retrieveFile(storageConfig.getDataFiles() + file, local);

            if(!downloaded) {
                log.error("Error downloading data file: {}", file);
            }
        } catch(CopyStreamException e)  {
            log.error("CopyStream error downloading {}",file, e);
        } catch (FTPConnectionClosedException e) {
            log.error("FTPConnection closed unexpectedly",e);
        } catch (IOException e) {
            log.error("IOException",e);
        }
    }

    /**
     * 
     * @throws IOException
     */
    public void disconnect()    {
        try {
            ftp.disconnect();
        } catch (IOException e) {
            log.error("Error trying to disconnect",e);
        }
    }
}
