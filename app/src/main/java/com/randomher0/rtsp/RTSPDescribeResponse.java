package com.randomher0.rtsp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTSPDescribeResponse {

    Map<String, String> headers;
    List<String> sdpFields;

    public RTSPDescribeResponse(RTSPResponse rtspResponse) {
        try {
            headers = parseHeaders(rtspResponse.getHeaders());
            sdpFields = parseSDP(rtspResponse.getBody());
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

    public List<String> parseSDP(String sdpData) throws Exception {
        List<String> sdpFields = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(sdpData));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            sdpFields.add(line.trim());
        }
        return sdpFields;
    }
}
