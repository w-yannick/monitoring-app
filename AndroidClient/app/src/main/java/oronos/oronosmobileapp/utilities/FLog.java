package oronos.oronosmobileapp.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FLog.java
 * Utilitaire pour ecrire des logs dans le fichier de log (inspiré de Android.Log)
 */
public class FLog {

    private static File DIR_PATH = new File("/sdcard/oronos_logs");
    private static String FILE_NAME = "client_logs_" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + ".txt";

    public static void d(Tag tag, String message) {
        writeLog(tag, message, "DEBUG");
    }

    public static void i(Tag tag, String message) {
        writeLog(tag, message, "INFO");
    }

    public static void w(Tag tag, String message) {
        writeLog(tag, message, "WARNING");
    }

    public static void e(Tag tag, String message) {
        writeLog(tag, message, "ERROR");
    }

    // Ajouter une ligne de texte dans le fichier
    private static void appendString(String text) {
        if (!DIR_PATH.exists())
            DIR_PATH.mkdirs();

        final File logFile = new File(DIR_PATH, FILE_NAME);

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajouter une ligne de log correctement formatée dans le fichier de log
    private static void writeLog(Tag tag, String message, String level) {
        appendString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + level + " - " + tag.toString() + ": " + message);
    }

}
