package coding.test.ops.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import coding.test.ops.constants.AppConstants;
import coding.test.ops.model.Event;
import coding.test.ops.model.IncomingJson;
import coding.test.ops.services.EventMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FileReaderUtil extends Thread {
    private static final Logger logger = LogManager.getLogger(FileReaderUtil.class);

    /* Reads from file provided and maps incoming json */
    public synchronized Map<String, Event> readFromFile(String filePath) throws IOException {

        FileInputStream inputStream = null;
        EventMapping eventMapping = new EventMapping();
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, AppConstants.CHARSETUTF);
            while (sc.hasNextLine()) {
                incomingJsonMappingToEvent(sc.nextLine(),eventMapping);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
            logger.info("File successfully read and mapped");
        } catch (IOException e) {
            logger.debug("{}",e.toString());

        } finally{
            if (inputStream != null) {
                logger.info("File closed");
                inputStream.close();
            }
            if (sc != null) {
                logger.info("Scanner stopped");
                sc.close();
            }
        }
        return eventMapping.getMap();
    }
    /* checks if field exists as its optional */
    private String jsonObjectFieldChecker(JSONObject obj,String field){
        if(obj.has(field)){
            return obj.getString(field);
        }
        else{
            return "";
        }
    }

    /* maps incoming json and adds to the map */
    private void incomingJsonMappingToEvent(String line,EventMapping eventMapping ){
        JSONObject json = new JSONObject(line);

        eventMapping.writeToMap(
                IncomingJson.builder()
                        .id(json.getString(AppConstants.ID))
                        .timeStamp(json.getInt(AppConstants.TIMESTAMP))
                        .state(json.getString(AppConstants.STATE))
                        .type(jsonObjectFieldChecker(json, AppConstants.TYPE))
                        .host(jsonObjectFieldChecker(json, AppConstants.HOST)).build());
    }
}
