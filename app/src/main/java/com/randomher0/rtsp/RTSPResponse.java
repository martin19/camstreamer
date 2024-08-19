package com.randomher0.rtsp;

public class RTSPResponse {
    private RTSPStatusCode statusCode;
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
}
