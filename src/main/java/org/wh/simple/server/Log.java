package org.wh.simple.server;

import java.io.PrintStream;

public class Log {
    private static PrintStream logStream=System.out;
    private static PrintStream errorStream=System.err;


    public static void writeLog(String log){
        logStream.println(log);
    }

    public static void writeError(String error){
        errorStream.println(error);
    }

    public static void setLogStream(PrintStream logStream) {
        Log.logStream = logStream;
    }

    public static void setErrorStream(PrintStream errorStream) {
        Log.errorStream = errorStream;
    }
}
