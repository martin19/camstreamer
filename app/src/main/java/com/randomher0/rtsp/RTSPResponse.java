package com.randomher0.rtsp;

public class RTSPResponse {
    private RTSPStatusCode statusCode;
    private String headers;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public RTSPStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(RTSPStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
