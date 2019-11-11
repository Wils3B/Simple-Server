package org.wh.simple.server;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestProcessor extends Thread {
    private ServerHttp serverHttp;
    private static LinkedList<Socket> pool;
    public static int counter=0;
    private AtomicBoolean isRun;

    public RequestProcessor(ServerHttp serverHttp) {
        this.serverHttp = serverHttp;
        this.pool = new LinkedList<>();
        isRun = new AtomicBoolean(true);
        counter++;
    }

    @Override
    public void run() {

        while (isRun.get()) {
            Socket connection;
            synchronized (pool) {
                while (pool.isEmpty()) {
                    try {
                        pool.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                connection = pool.removeFirst();
                if (connection==null) break;
            }
            try {
                new RequestResponder(serverHttp, connection).analyseRequest();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.writeLog("Request processor " + getName() + " arrête\n"+ pool);
        pool.push(null);
    }

    public static void processRequest(Socket request) {
        synchronized (pool) {
            pool.add(request);
            pool.notifyAll();
        }
    }

    public void cancel() {
        Log.writeLog("Arrêt du request processor " + this.getName());
        synchronized (pool){
            for (Socket socket:pool){
                try {
                    if (socket!=null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //pool.clear();
            pool.push(null);
            pool.push(null);
            pool.push(null);
            pool.push(null);
            pool.push(null);
            pool.notifyAll();
        }
        isRun.set(false);
    }

}
