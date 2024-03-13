DONE: add static video player

TODO: display video stream from rollei camera

    DONE: add static configuration values (see [1])
    DONE: add configuration fragment to main activity
    
    next up:
    TODO: connect to camera network
        DONE: use NetworkRequest api.
        TODO: app wide preferences using SharedPreferences, look up an example and implement.

    TODO: display camera stream in preview
    TODO: create an ssh tunnel from android to virtual machine (obsbox)
    TODO: publish stream to obsbox

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