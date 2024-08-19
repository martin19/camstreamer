package com.randomher0.rtsp;

import android.net.rtp.RtpStream;

import androidx.media3.exoplayer.rtsp.RtspMediaSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RTSPClient {

    /*
    DESCRIBE rtsp://localhost:8554/live.stream RTSP/1.0
    CSeq: 1
    User-Agent: Lavf58.76.100

    v=0
    o=- 0 0 IN IP4 127.0.0.1
    s=No Name
    c=IN IP4 0.0.0.0
    t=0 0
    m=video 0 RTP/AVP 96
    a=control:rtsp://localhost:8554/live.stream/trackID=0
    a=rtpmap:96 MP4V-ES/90000
    a=fmtp:96 config=000001B001000001B58913000001000000012000C48D8800F528045A1443000001B24C61766335382E3133342E313030; profile-level-id=1
     */

    String sdp;

    public RTSPClient() {
    }

    public RTSPResponse readResponse(BufferedReader reader) throws Exception {
        RTSPStatusCode rtspStatusCode = null;
        String responseLine;
        StringBuilder response = new StringBuilder();

        while ((responseLine = reader.readLine()) != null) {
            if(responseLine.contains("RTSP/1.0 200 OK")) {
                rtspStatusCode = RTSPStatusCode.OK;
            }

            response.append(responseLine).append("\n");
            // RTSP responses typically end with a blank line, break the loop when it's reached
            if (responseLine.trim().isEmpty()) {
                break;
            }
        }

        RTSPResponse rtspResponse = new RTSPResponse();
        rtspResponse.setStatusCode(rtspStatusCode);
        rtspResponse.setBody(response.toString());

        return rtspResponse;
    }

    private void sendRTSPCommand(BufferedWriter writer, String command) {
        try {
            writer.write(command + "\r\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printRTSPResponse(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.isEmpty()) {
                break;  // End of RTSP headers
            }
        }
    }

    public void start() throws IOException {
        String sourceRTSPUrl = "rtsp://localhost:8554/live.stream";
        String destRTSPUrl = "rtsp://localhost:8555";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //rtsp://127.0.0.1:8554/action-one-1
                    // Step 1: Connect to the source RTSP server
                    Socket sourceSocket = new Socket("10.0.2.2", 18554);  // RTSP usually runs on port 554
                    BufferedReader sourceReader = new BufferedReader(new InputStreamReader(sourceSocket.getInputStream()));
                    BufferedWriter sourceWriter = new BufferedWriter(new OutputStreamWriter(sourceSocket.getOutputStream()));

                    // Step 2: Connect to the destination RTSP server
                    //Socket destSocket = new Socket("10.0.2.2", 8555);
                    //BufferedReader destReader = new BufferedReader(new InputStreamReader(destSocket.getInputStream()));
                    //PrintWriter destWriter = new PrintWriter(destSocket.getOutputStream(), true);

                    // Step 3: Send RTSP commands to the source server
                    sendRTSPCommand(sourceWriter, "OPTIONS " + sourceRTSPUrl + " RTSP/1.0\r\nCSeq: 1\r\nUser-Agent: Lavf58.76.100\r\n");
                    RTSPResponse rtspResponseOptions = readResponse(sourceReader);

                    // Step 3: Send RTSP commands to the source server
                    sendRTSPCommand(sourceWriter, "DESCRIBE " + sourceRTSPUrl + " RTSP/1.0\r\nCSeq: 2\r\nUser-Agent: Lavf58.76.100\r\n");
                    {
                        RTSPResponse rtspResponse = readResponse(sourceReader);
                        RTSPDescribeResponse rtspDescribeResponse = new RTSPDescribeResponse(rtspResponse);
                    }

                    /*
                    // Parse and forward SDP information and setup streams on destination
                    // Assume some SDP info extraction logic here

                    // Step 4: Send RTSP commands to the destination server
                    sendRTSPCommand(destWriter, "OPTIONS " + destRTSPUrl + " RTSP/1.0");
                    printRTSPResponse(destReader);

                    sendRTSPCommand(destWriter, "DESCRIBE " + destRTSPUrl + " RTSP/1.0");
                    printRTSPResponse(destReader);
                     */
                    // Cleanup and close sockets when done
                    sourceSocket.close();
                    //destSocket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        TODO: implement methods and appropriate response types
        https://www.rfc-editor.org/rfc/rfc2326.html

        PLAY              C->S             P,S        required
        SETUP             C->S             S          required
        TEARDOWN          C->S             P,S        required
         */

        thread.start();

        // Forward SETUP and PLAY commands
        // Assume you extract and forward the necessary session and transport info

        // Step 5: Forward the RTP packets from source to destination
        // This step requires reading RTP packets from the source and sending them to the destination

        // Step 6: Handle RTCP and session maintenance
    }
}
