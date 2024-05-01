DONE: add static video player

TODO: display video stream from rollei camera

    DONE: add static configuration values (see [1])
    DONE: add configuration fragment to main activity 
    DONE: create navigation concept [5].
    DONE: connect to camera network
    DONE: use NetworkRequest api.
    DONE: app wide preferences using SharedPreferences, look up an example and implement.
    
    next:    
    
    DONE: display camera stream in preview
        TODO: make stream start reliably
            ANALYSIS: nordvpn causes EPERM error, need to deactivate nord vpn connection before using app.
            https://stackoverflow.com/questions/74263177/exoplayer-not-sending-rtsp-request
        -> stream does not start reliably, what is required? it works with ffplay
        -> crashes after a few seconds, debug this.

    TODO: tunnel any rtsp video stream from src (wifi) to dst (public net) 
        - use ffmpeg to read the stream
        - optinally recode it   
        - send the data to remote server (in our case the destination needs to be)

    TODO: access camera stream data
    TODO: create an ssh tunnel from android to virtual machine (obsbox)
    

    later:
    investigate ambarella-api-pytools
    - implement api in java module and expose parameters via android ui
        - high speed shutter (high fps mode)
        - night vision?

[1] https://developer.android.com/develop/ui/views/components/settings#java
[2] https://developer.android.com/reference/android/net/wifi/package-summary
[3] https://gist.github.com/liu7yong/f3765398b2ea5f30c986
[4] https://stackoverflow.com/questions/53620234/android-wifimanager-enablenetwork-returning-false
[5] https://developer.android.com/develop/connectivity/wifi/wifi-suggest
[6] https://developer.android.com/guide/navigation/design
[7] https://stackoverflow.com/questions/54668864/mediacodec-output-buffer-doesnt-generate-correct-output-when-its-input-surface

snippets:

final Handler handler = new Handler(Looper.getMainLooper());
handler.postDelayed(new Runnable() {
    @Override
    public void run() {
        //Do something after 3000ms
        displayLiveStream(network);
    }
}, 3000);
