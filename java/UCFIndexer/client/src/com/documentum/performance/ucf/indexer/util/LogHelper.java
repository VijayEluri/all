package com.documentum.performance.ucf.indexer.util;

import java.util.HashMap;
import java.util.logging.Level;

public class LogHelper
{
    static HashMap<String, Level> levels;

    static {
        levels = new HashMap<String, Level>();
        levels.put("severe", Level.SEVERE);
        levels.put("warning", Level.WARNING);
        levels.put("info", Level.INFO);
        levels.put("config", Level.CONFIG);
        levels.put("fine", Level.FINE);
        levels.put("finer", Level.FINER);
        levels.put("finest", Level.FINEST);
        levels.put("off", Level.OFF);
    }

    /**
     * @param level A string specifying log level
     * @return Log level in java logging or null if the parameter is not valid
     */
    public static Level getLogLevel(String level)
    {
        Level logLevel = levels.get(level);
        return logLevel;
    }

}
