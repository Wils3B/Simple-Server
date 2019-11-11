package org.wh.simple.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

public class Response {
    private String header;
    private byte[] body;

    public Response(String status){
        this.body = status.getBytes();
        this.header = headerResponse(status,"text/plain",this.body.length);
    }

    public Response(String status, File body) throws IOException {
        this.body = Files.readAllBytes(body.toPath());
        this.header = headerResponse(status,getContentTypeFromName(body.getName()),this.body.length);
    }

    public Response(String header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public static String headerResponse(String responseStatus, String contentType) {
        return "HTTP/1.1 " + responseStatus + "\r\n"
                + "Server: WH HTTP Server 1.0\r\n"
                + "Date: " + new Date() + "\n"
                + "Content-type: " + contentType + "\r\n\r\n";
    }

    public static String headerResponse(String responseStatus, String contentType, int contentLength) {
        return "HTTP/1.1 " + responseStatus + "\r\n"
                + "Server: WH HTTP Server 1.0\r\n"
                + "Date: " + new Date()
                + "Content-length: " + contentLength + "\r\n"
                + "Content-type: " + contentType + "\r\n\r\n";
    }

    private static String getContentTypeFromName(String filename) {
        if (filename.endsWith(".html") || filename.endsWith(".htm"))
            return "text/html";
        else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
            return "image/jpeg";
        else if (filename.endsWith(".txt") || filename.endsWith(".java")) {
            return "text/plain";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (filename.endsWith(".class")) {
            return "application/octet-stream";
        } else if (filename.endsWith(".css")) {
            return "text/css";
        } else if (filename.endsWith(".scss")) {
            return "text/scss";
        } else if (filename.endsWith(".js")) {
            return "text/javascript";
        } else return "text/plain";
    }

}
