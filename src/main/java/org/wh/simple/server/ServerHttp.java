package org.wh.simple.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHttp extends Thread {

    private File documentRootDirectory;
    private String indexFileName;
    private ServerSocket serverSocket;
    private AtomicBoolean isRun;
    private int numThread;
    private ExecutorService processors;

    public ServerHttp(File documentRootDirectory,String indexFileName,int port,int numThread) {
        if (documentRootDirectory==null || !documentRootDirectory.isDirectory()){
            throw new IllegalArgumentException("L'argument documentRootDirectory est illegal "+documentRootDirectory);
        }
        this.documentRootDirectory=documentRootDirectory;
        this.indexFileName=indexFileName;
        try {
            this.serverSocket=new ServerSocket(port);
            Log.writeLog("Serveur démmaré, attente de requêtes sur le port "+port);
        } catch (IOException e) {
            throw new RuntimeException("Echec de connexion du serveur au port "+port);
        }
        isRun=new AtomicBoolean(true);
        this.numThread=numThread;
        processors= Executors.newFixedThreadPool(numThread);
    }

    public void cancel(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRun.set(false);
        Log.writeLog("Tentative d'arrêt du serveur");
        processors.shutdown();
    }

    public File getDocumentRootDirectory() {
        return documentRootDirectory;
    }

    public String getIndexFileName() {
        return indexFileName;
    }

    public boolean isRun() {
        return isRun.get();
    }

    @Override
    public void run() {
        Log.writeLog("Début d'acceptation des requêtes du serveur");
        for (int i=0;i<this.numThread;i++){
            RequestProcessor requestProcessor=new RequestProcessor(this);
            processors.submit(requestProcessor);
        }
        System.out.println("Debut d'ttente de connxeions au port "+serverSocket.getLocalPort());
        while (isRun.get()){
            try {
                Socket request=serverSocket.accept();
                RequestProcessor.processRequest(request);
            } catch(SocketException e){

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        Log.writeLog("Fin d'execution des requêtes");
    }
}
