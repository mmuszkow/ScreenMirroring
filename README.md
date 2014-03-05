ScreenMirroring
============

Screen mirroring HTTP server for Android

It's more a proof-of-concept, than a working application. If you are starting the app for the first time, press the "Fix permissions" button (needs ROOT!), close it and open again. After opening the http://YOUR_PHONE_IP:6100 you should see the live image of your phone's screen.

I didn't put much effort into the screen capturing method. I used the /dev/fd0 method which needs the root permissions and which probably won't work on newer devices. Here is the great summary of other methods: http://stackoverflow.com/a/10069679/1552030 . I achieved ~5FPS on Samsung Galaxy SIII mini, most of the time was spent on the JPEG compression (as a live stream video I used M-JPEG).
