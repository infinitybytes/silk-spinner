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
        ftp.setDataTimeout(30_000);
    
        try {
            ftp.connect(storageConfig.getFtpHost(), 21);
            if( !FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                log.error("Unable to connect to FTP: {}", ftp.getReplyString());
            }
            if(!ftp.login(authConfig.getUsername(), authConfig.getPassword()))  {
                log.error("Unable to login to FTP: {}", ftp.getReplyString());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param dirName
     * @return
     * @throws IOException
     */
    public List<DataFile> ls(String dirName) {
        List<FTPFile> files = new ArrayList<>();
        try {
            files = Arrays.asList(ftp.listFiles(storageConfig.getDataFiles() + dirName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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

    public List<DataFile> ls()   {
        return ls("");
    }

    public void getRemote(String file, File tempLocation)    {
        try (FileOutputStream local = new FileOutputStream(tempLocation)) {
            ftp.enterLocalPassiveMode();
            boolean downloaded = ftp.retrieveFile(storageConfig.getDataFiles() + file, local);
            ftp.enterLocalActiveMode();

            if(!downloaded) {
                log.error("Error downloading data file: {}", file);
            }
        } catch(CopyStreamException e)  {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPConnectionClosedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void getRemote(DataFile file, File tempLocation)    {
        try (FileOutputStream local = new FileOutputStream(tempLocation)) {
            ftp.enterLocalPassiveMode();
            boolean downloaded = ftp.retrieveFile(storageConfig.getDataFiles() + file.getSlug() + '/' + file.getName(), local);
            ftp.enterLocalActiveMode();

            if(!downloaded) {
                log.error("Error downloading data file: {}", file);
            }
        } catch(CopyStreamException e)  {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FTPConnectionClosedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @throws IOException
     */
    public void disconnect()    {
        try {
            ftp.logout();
            ftp.disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
