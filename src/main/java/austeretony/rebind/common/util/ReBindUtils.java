package austeretony.rebind.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ReBindUtils {

    public static JsonElement getInternalJsonData(String path) throws IOException {     
        JsonElement rawData = null;             
        try (InputStream inputStream = ReBindUtils.class.getClassLoader().getResourceAsStream(path)) {                                      
            rawData = new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
        }
        return rawData;
    }

    public static JsonElement getExternalJsonData(String path) throws IOException {             
        JsonElement rawData = null;             
        try (InputStream inputStream = new FileInputStream(new File(path))) {                                   
            rawData = new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  
        }               
        return rawData;
    }

    public static void createExternalJsonFile(String path, JsonElement data) throws IOException {               
        try (Writer writer = new FileWriter(path)) {                                            
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        }
    }

    public static void createAbsoluteJsonCopy(String path, InputStream source) throws IOException {                 
        List<String> fileData;       
        try (InputStream inputStream = source) {                                                               
            fileData = IOUtils.readLines(new InputStreamReader(inputStream, "UTF-8"));
        } 
        if (fileData != null) {              
            try (PrintStream printStream = new PrintStream(new File(path))) {                      
                for (String line : fileData)                
                    printStream.println(line);
            }
        }
    }

    public static boolean isOutdated(String currentVersion, String availableVersion) {                                                               
        String[] 
                cVer = currentVersion.split("[.]"),
                aVer = availableVersion.split("[.]");                           
        int diff;               
        for (int i = 0; i < cVer.length; i++) {                                 
            try {                               
                diff = Integer.parseInt(aVer[i]) - Integer.parseInt(cVer[i]);                                                                                           
                if (diff > 0)
                    return true;                                
                if (diff < 0)
                    return false;
            } catch (NumberFormatException exception) {                         
                exception.printStackTrace();
            }
        }               
        return false;
    }
}
