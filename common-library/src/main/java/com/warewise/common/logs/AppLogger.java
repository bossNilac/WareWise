package com.warewise.common.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class AppLogger {
    private static final Logger logger = LoggerFactory.getLogger(AppLogger.class);
    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");



    public static Logger getLogger() {
        return logger;
    }

    public void log(LogLevel level,String message){
        switch (level){
            case INFO  -> logger.info(message);
            case TRACE -> logger.trace(message);
            case WARN  -> logger.warn(message);
            case ERROR -> logger.error(message);
            case FATAL -> logger.error(FATAL, "FATAL: "+message);
        }
    }

}
