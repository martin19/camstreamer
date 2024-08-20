package com.randomher0.rtsp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class RTSPPlayResponse {

    Map<String, String> headers;
    private String range;
    private String session;
    private String transport;

    public RTSPPlayResponse(RTSPResponse rtspResponse) {
        parse(rtspResponse.getHeaders());
    }

    public void parse(String response) {
       try {
            // Step 1: Parse Headers
            headers = parseHeaders(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> parseHeaders(String response) throws Exception {
        Map<String, String> headers = new HashMap<>();
        BufferedReader reader = new BufferedReader(new StringReader(response));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                break; // End of headers, empty line
            }
            // Split header line into key and value
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                String key = headerParts[0].trim();
                String value = headerParts[1].trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}
