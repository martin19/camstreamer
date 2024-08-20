package com.randomher0.rtsp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

public class RTSPForwarder {

    static String userAgent = "Lavf58.76.100";
    String sdp;
    String sourceSession;
    String destSession;
    Integer sourceCseq;
    Integer destCseq;

    public RTSPForwarder() {
        sourceCseq = 1;
        destCseq = 1;
    }

    public RTSPResponse readResponse(BufferedReader reader) throws Exception {
        RTSPStatusCode rtspStatusCode = null;
        String responseLine;
        StringBuilder responseHeaders = new StringBuilder();
        StringBuilder responseBody = new StringBuilder();
        boolean isHeader = true;

        while ((responseLine = reader.readLine()) != null) {
            if(responseLine.contains("RTSP/1.0 200 OK")) {
                rtspStatusCode = RTSPStatusCode.OK;
            }

            if(isHeader && responseLine.isEmpty()) {
                isHeader= false;
            } else if(isHeader) {
                responseHeaders.append(responseLine).append("\r\n");
            } else {
                responseBody.append(responseLine).append("\r\n");
            }

            if(!isHeader && !reader.ready()) {
                break;
            }
        }

        RTSPResponse rtspResponse = new RTSPResponse();
        rtspResponse.setStatusCode(rtspStatusCode);
        rtspResponse.setHeaders(responseHeaders.toString());
        rtspResponse.setBody(responseBody.toString());

        return rtspResponse;
    }

    private RTSPResponse readStreamingResponse(BufferedReader reader,
                                               BufferedInputStream binaryInputStream,
                                               RTSPStreamDataEventListener listener) throws IOException {
        RTSPStatusCode rtspStatusCode = null;
        String responseLine;
        StringBuilder responseHeaders = new StringBuilder();
        boolean isHeader = true;

        while ((responseLine = reader.readLine()) != null) {
            if(responseLine.contains("RTSP/1.0 200 OK")) {
                rtspStatusCode = RTSPStatusCode.OK;
            }

            if(isHeader && responseLine.isEmpty()) {
                isHeader= false;
            } else if(isHeader) {
                responseHeaders.append(responseLine).append("\r\n");
            } else {
                break;
            }
        }

        //stream binary data
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = binaryInputStream.read(buffer)) != -1) {
            listener.onData(buffer, bytesRead);
        }

        RTSPResponse rtspResponse = new RTSPResponse();
        rtspResponse.setStatusCode(rtspStatusCode);
        rtspResponse.setHeaders(responseHeaders.toString());
        return rtspResponse;
    }

    private void sendRTSPOptionsCommand(BufferedWriter writer, Integer cseq, String sourceRTSPUrl) {
        try {
            writer.write("OPTIONS " + sourceRTSPUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPDescribeCommand(BufferedWriter writer, Integer cseq, String sourceRTSPUrl) {
        try {
            writer.write("DESCRIBE " + sourceRTSPUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
                    "\r\nAccept: application/sdp" +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPAnnounceCommand(BufferedWriter writer, Integer cseq, String streamUrl, List<String> sdpFields) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date now = new Date();
            String formattedDate = sdf.format(now);

            Long session = new Random().nextLong();
            String sdpBodyData = String.join("\r\n", sdpFields);
            sdpBodyData += "\r\n\r\n";

            writer.write("ANNOUNCE " + streamUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
                    "\r\nDate: " + formattedDate +
                    "\r\nSession: " + session +
                    "\r\nContent-Type: application/sdp" +
                    "\r\nContent-Length: " + sdpBodyData.length() +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\nTransport: RTP/AVP/TCP;unicast;interleaved=0-1" +
                    "\r\n\r\n" +
                    sdpBodyData
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPSetupCommand(BufferedWriter writer, Integer cseq, String streamUrl, boolean record) {
        try {
            writer.write("SETUP " + streamUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
                    "\r\nUser-Agent: " + userAgent +
                    "\r\nTransport: RTP/AVP/TCP;unicast;interleaved=0-1" + (record ? ";mode=record" : "") +
                    "\r\n\r\n"
            );
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRTSPRecordCommand(BufferedWriter writer, Integer cseq, String streamUrl, String session, RTSPRange range) {
        try {
            writer.write("RECORD " + streamUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
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

    private void sendRTSPPlayCommand(BufferedWriter writer, Integer cseq, String mediaUrl, String session, RTSPRange range) {
        try {
            writer.write("PLAY " + mediaUrl + " RTSP/1.0" +
                    "\r\nCSeq: " + cseq +
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

    private void sendRtspStreamData(BufferedWriter writer, BufferedOutputStream binaryOutputStream, byte[] data, int bytes) {
        try {
            binaryOutputStream.write(data, 0, bytes);
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
            BufferedInputStream sourceBinaryInputStream = new BufferedInputStream(sourceSocket.getInputStream());

            // Step 1: determine valid methods from source server
            sendRTSPOptionsCommand(sourceWriter, sourceCseq++, sourceRTSPUrl);
            RTSPResponse rtspResponseOptions = readResponse(sourceReader);

            // Step 2: determine stream descriptor from source server
            sendRTSPDescribeCommand(sourceWriter, sourceCseq++, sourceRTSPUrl);
            RTSPDescribeResponse rtspDescribeResponse = new RTSPDescribeResponse(readResponse(sourceReader));

            // Step 3: setup source stream
            //String mediaUrl = rtspDescribeResponse.sdpFields.getOrDefault("a=control",null);
            Optional<String> controlField = rtspDescribeResponse.sdpFields.stream().filter((field) -> field.startsWith("a=control:")).findFirst();
            final String mediaUrl;
            mediaUrl = controlField.map(s -> s.substring("a=control:".length())).orElse(sourceRTSPUrl);
            sendRTSPSetupCommand(sourceWriter, sourceCseq++, mediaUrl, false);
            RTSPSetupResponse sourceRtspSetupResponse = new RTSPSetupResponse(readResponse(sourceReader));
            sourceSession = sourceRtspSetupResponse.getSession();

            // open connection to dest server
            Socket destSocket = new Socket("10.0.2.2", 28554);  // RTSP usually runs on port 554
            BufferedReader destReader = new BufferedReader(new InputStreamReader(destSocket.getInputStream()));
            BufferedWriter destWriter = new BufferedWriter(new OutputStreamWriter(destSocket.getOutputStream()));
            BufferedOutputStream destBinaryOutputStream = new BufferedOutputStream(destSocket.getOutputStream());

            // determine valid methods from source server
            sendRTSPOptionsCommand(destWriter, destCseq++, destRTSPUrl);
            RTSPResponse destRtspResponseOptions = readResponse(destReader);

            // announce a new stream
            sendRTSPAnnounceCommand(destWriter, destCseq++, destRTSPUrl, rtspDescribeResponse.sdpFields);
            RTSPResponse destRtspResponseAnnounce = readResponse(destReader);

            // describe dest stream
//            sendRTSPDescribeCommand(destWriter, destCseq++, destRTSPUrl);
//            RTSPDescribeResponse destRtspDescribeResponse = new RTSPDescribeResponse(readResponse(destReader));

            //setup dest stream (forward sdp info)
            Optional<String> controlField2 = rtspDescribeResponse.sdpFields.stream().filter((field) -> field.startsWith("a=control:")).findFirst();
            final String mediaUrl2;
            mediaUrl2 = controlField2.map(s -> s.substring("a=control:".length())).orElse(sourceRTSPUrl);
//            String mediaUrl2 = destRTSPUrl;
            sendRTSPSetupCommand(destWriter, destCseq++, mediaUrl2, true);
            RTSPSetupResponse destRtspSetupResponse = new RTSPSetupResponse(readResponse(destReader));
            destSession = destRtspSetupResponse.getSession();

            //record dest stream
            sendRTSPRecordCommand(destWriter, destCseq++, destRTSPUrl, destSession, new RTSPRange(0, 60));
            RTSPRecordResponse destRtspRecordResponse = new RTSPRecordResponse(readResponse(destReader));

            // Step4: forward stream data
            sendRTSPPlayCommand(sourceWriter, destCseq++, sourceRTSPUrl, sourceSession, new RTSPRange(0, 60));

            readStreamingResponse(sourceReader, sourceBinaryInputStream, (byte[] data, int bytes)->{
                sendRtspStreamData(destWriter, destBinaryOutputStream, data, bytes);
                System.out.println(data);
            });

            //TODO: teardown.

            sourceSocket.close();
            destSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        String sourceRTSPUrl = "rtsp://localhost:8554/live.stream";
        String destRTSPUrl = "rtsp://localhost:8554/live.stream";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                forwardStream(sourceRTSPUrl, destRTSPUrl);
            }
        });

        thread.start();
    }
}
