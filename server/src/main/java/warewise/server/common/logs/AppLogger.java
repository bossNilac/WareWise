package warewise.server.common.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import warewise.server.common.handler.LogHandler;
import warewise.server.common.model.Log;

import java.time.Clock;
import java.time.LocalDateTime;

public class AppLogger {
    private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);
    private static final LogHandler logHandler = LogHandler.getInstance();

    public static void log(String action,String description,String username){
        String now= LocalDateTime.now(Clock.systemDefaultZone())
                .toString();
        String message = username + ": " + action + "=>" + description + " at " + now;
        logger.info(message);

        logHandler.addLog(
                new Log(
                        username,
                        action,
                        description,
                        now
                )
        );
    }

}
