package com.randomher0.rtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RTSPForwarder {

    static String userAgent = "Lavf58.76.100";
    String sdp;
    String sourceSession;
    String destSession;
    Integer cseq;

    public RTSPForwarder() {
        cseq = 1;
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

    private void sendRTSPOptionsCommand(BufferedWriter writer, String sourceRTSPUrl) {
        try {
            writer.write("OPTIONS " + sourceRTSPUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + (cseq++) +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPDescribeCommand(BufferedWriter writer, String sourceRTSPUrl) {
        try {
            writer.write("DESCRIBE " + sourceRTSPUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + (cseq++) +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPSetupCommand(BufferedWriter writer, String streamUrl) {
        try {
            writer.write("SETUP " + streamUrl + "/streamid=0" + " RTSP/1.0" +
                    "\r\nCSeq: " + (cseq++) +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\nTransport: RTP/AVP/TCP;unicast;interleaved=0-1;mode=record" +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPRecordCommand(BufferedWriter writer, String streamUrl, String session, RTSPRange range) {
        try {
            writer.write("RECORD " + streamUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + (cseq++) +
                    "\r\nRange: npt=" + range.toString() +
                    "\r\nSession: " + session +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPPlayCommand(BufferedWriter writer, String mediaUrl, String session, RTSPRange range) {
        try {
            writer.write("PLAY " + mediaUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + (cseq++) +
                    "\r\nSession: " + session +
                    "\r\nRange: npt=" + range.toString() +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forwardStream(String sourceRTSPUrl, String destRTSPUrl) {
        try {
            // Step 1: Connect to the source RTSP server
            Socket sourceSocket = new Socket("10.0.2.2", 18554);  // RTSP usually runs on port 554
            BufferedReader sourceReader = new BufferedReader(new InputStreamReader(sourceSocket.getInputStream()));
            BufferedWriter sourceWriter = new BufferedWriter(new OutputStreamWriter(sourceSocket.getOutputStream()));

            // Step 1: determine valid methods from source server
            sendRTSPOptionsCommand(sourceWriter, sourceRTSPUrl);
            RTSPResponse rtspResponseOptions = readResponse(sourceReader);

            // Step 2: determine stream descriptor from source server
            sendRTSPDescribeCommand(sourceWriter, sourceRTSPUrl);
            RTSPDescribeResponse rtspDescribeResponse = new RTSPDescribeResponse(readResponse(sourceReader));

            // Step 3: setup source stream
            sendRTSPSetupCommand(sourceWriter, sourceRTSPUrl);
            RTSPSetupResponse sourceRtspSetupResponse = new RTSPSetupResponse(readResponse(sourceReader));
            sourceSession = sourceRtspSetupResponse.getSession();

            // Step4: play stream
            String mediaUrl = sourceRtspSetupResponse.headers.getOrDefault("Content-Base",sourceRTSPUrl);
            sendRTSPPlayCommand(sourceWriter, mediaUrl, sourceSession, new RTSPRange(0, 60));
            RTSPPlayResponse sourceRtspPlayResponse = new RTSPPlayResponse(readResponse(sourceReader));

//            // Step 3: open connection to destination server
//            Socket destSocket = new Socket("10.0.2.2", 28554);  // RTSP usually runs on port 554
//            BufferedReader destReader = new BufferedReader(new InputStreamReader(destSocket.getInputStream()));
//            BufferedWriter destWriter = new BufferedWriter(new OutputStreamWriter(destSocket.getOutputStream()));
//
//            // Step 4: determine valid methods from source server
//            sendRTSPOptionsCommand(destWriter, sourceRTSPUrl);
//            RTSPResponse destRtspResponseOptions = readResponse(destReader);
//
//            // Step 5: determine stream descriptor from source server
//            sendRTSPDescribeCommand(destWriter, sourceRTSPUrl);
//            RTSPDescribeResponse destRtspDescribeResponse = new RTSPDescribeResponse(readResponse(destReader));
//
//            //setup dest stream (forward sdp info)
//            sendRTSPSetupCommand(destWriter, destRTSPUrl);
//            RTSPSetupResponse destRtspSetupResponse = new RTSPSetupResponse(readResponse(destReader));
//
//            //record dest stream
//            sendRTSPRecordCommand(destWriter, destRTSPUrl, destRtspSetupResponse.session);

            // Cleanup and close sockets when done
            sourceSocket.close();
//            destSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        String sourceRTSPUrl = "rtsp://localhost:8554/live.stream";
        String destRTSPUrl = "rtsp://localhost:8555";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                forwardStream(sourceRTSPUrl, destRTSPUrl);
            }
        });

        thread.start();
    }
}
