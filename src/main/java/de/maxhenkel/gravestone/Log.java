package de.maxhenkel.gravestone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    private static Logger log = LogManager.getLogger(Main.MODID);

    public static void i(String msg) {
        log.log(Level.INFO, msg);
    }

    public static void w(String msg) {
        log.log(Level.WARN, msg);
    }

    public static void e(String msg) {
        log.log(Level.ERROR, msg);
    }

    public static void f(String msg) {
        log.log(Level.FATAL, msg);
    }

    public static void setLogger(Logger logger) {
        log = logger;
    }
}
