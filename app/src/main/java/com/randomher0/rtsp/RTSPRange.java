package com.randomher0.rtsp;

import java.util.Locale;

public class RTSPRange {
    private Float start;
    private Float end;

    public RTSPRange(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    @Override
    public String toString() {
        if(end == null) return String.format(Locale.US, "%.3f-", start);
        return String.format(Locale.US, "%.3f-%.3f", start, end);
    }
}
