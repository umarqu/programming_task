package coding.test.ops;

import coding.test.ops.constants.AppConstants;
import coding.test.ops.model.Event;
import coding.test.ops.utils.FileReaderUtil;
import coding.test.ops.utils.HsqldbUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@SpringBootApplication
public class LoggingApplication {
	private static final Logger logger = LogManager.getLogger(LoggingApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(LoggingApplication.class, args);
		FileReaderUtil app = new FileReaderUtil();

		try {
			String fileName = "";
			if(args.length == 0 || args[0].isEmpty()){
				File file = ResourceUtils.getFile(AppConstants.PathToLogFile);
				fileName = file.getPath();
				logger.info("File Path taken from properties folder");
			}
			else{
				fileName = args[0];
				logger.info("FilePath taken from command line");
			}
			Map<String, Event> EventMap = app.readFromFile(fileName);
			HsqldbUtil createTableUtil = new HsqldbUtil();
			createTableUtil.createTable();
			for (String key : EventMap.keySet()) {
				createTableUtil.insertRow(EventMap.get(key));
			}

			// this should output all the table
			createTableUtil.selectAllStatement();
		}
		catch(IOException | SQLException e){
			logger.debug("{}",e.getMessage());
		}
	}

}
