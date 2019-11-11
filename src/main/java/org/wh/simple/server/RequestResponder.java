package org.wh.simple.server;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Date;
import java.util.StringTokenizer;

public class RequestResponder {
    private ServerHttp serverHttp;

    private BufferedOutputStream outRaw;
    private OutputStreamWriter outputStreamWriter;
    private InputStreamReader inputStreamReader;
    private Socket endpoint;

    public RequestResponder(ServerHttp serverHttp, Socket endpoint) throws IOException {
        this.serverHttp = serverHttp;

        outRaw = new BufferedOutputStream(endpoint.getOutputStream());
        outputStreamWriter = new OutputStreamWriter(outRaw, "UTF-8");
        inputStreamReader = new InputStreamReader(new BufferedInputStream(endpoint.getInputStream()), "UTF-8");
        this.endpoint = endpoint;
    }

    public void analyseRequest() throws Exception {
        String requestHeader = getRequestHeader(), requestHeaderCpy = new String(requestHeader);
        StringTokenizer st = new StringTokenizer(requestHeader);
        String method = st.nextToken();
        String fileName = st.nextToken();
        if (fileName.endsWith("/")) fileName += serverHttp.getIndexFileName();
        Request request=new Request(method,fileName);

        if (method.equals("GET")) {//Methode GET
            writeResponse(request,doGet(request));
        }

    }

    private String getRequestHeader() throws Exception {
        char[] b = new char[2048];
        int stream;
        try {
            stream = inputStreamReader.read(b);
        } catch (IOException e) {
            throw new Exception("Echec de lecture de la requête HTTP :" + e);
        }
        if (stream == -1) return "";
        String header = new String(b, 0, stream);
        stream = header.indexOf("\n");
        return stream == -1 ? header : header.substring(0, stream);
    }

    /**
     * Write the response from the server to the client.
     * @param request
     * @param response
     * @throws IOException
     */
    private void writeResponse(Request request,Response response) throws IOException {
        outputStreamWriter.write(response.getHeader());
        outputStreamWriter.flush();
        outRaw.write(response.getBody());
        outRaw.flush();
        Log.writeLog(request.getMethod() +" "+request.getURI()+" : "+response.getHeader());
    }



    // Processing methods

    public Response doGet(Request request) throws IOException {
        String rootPath = serverHttp.getDocumentRootDirectory().getPath();
        File theFile = new File(serverHttp.getDocumentRootDirectory(), request.getURI().substring(1));
        if (theFile.canRead() && theFile.getCanonicalPath().startsWith(rootPath)) {
            return new Response("200 OK",theFile);
        } else if (request.getURI()==null || request.getURI().equals("")){
            return new Response("400 Bad Request");
        } else{
            return new Response("404 Not Found");
        }
    }

}
