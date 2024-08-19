package com.randomher0.rtsp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTSPDescribeResponse {

    Map<String, String> headers;
    Map<String, List<String>> sdpFields;

    public RTSPDescribeResponse(RTSPResponse rtspResponse) {
        parse(rtspResponse.getBody());
    }

    public void parse(String response) {
       try {
            // Step 1: Parse Headers
            headers = parseHeaders(response);

            // Step 2: Parse SDP (if any)
            String sdpData = response.substring(response.indexOf("\r\n\r\n") + 4);
            sdpFields = parseSDP(sdpData);

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

    public Map<String, List<String>> parseSDP(String sdpData) throws Exception {
        Map<String, List<String>> sdpFields = new HashMap<>();
        BufferedReader reader = new BufferedReader(new StringReader(sdpData));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            // Split SDP line into key and value
            String[] sdpParts = line.split("=", 2);
            if (sdpParts.length == 2) {
                String key = sdpParts[0].trim();
                String value = sdpParts[1].trim();

                sdpFields.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }
        return sdpFields;
    }
}
