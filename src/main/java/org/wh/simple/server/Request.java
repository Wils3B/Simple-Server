package org.wh.simple.server;

public class Request {
    private String method;
    private String URI;
    private String version;
    private String header;

    public Request() {
        this.method="GET";
        this.URI="";
    }

    public Request(String method, String URI) {
        this.method = method;
        this.URI = URI;
    }

    public Request(String method, String URI, String version, String header) {
        this.method = method;
        this.URI = URI;
        this.version = version;
        this.header = header;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
}
