ScreenMirroring
============

Screen mirroring HTTP server for Android

It's more a proof-of-concept than a working application. After starting it for the first time press "Fix permissions" (needs ROOT!) and re-open the application.
I didn't put much effort into the screen capturing method. I used the /dev/fd0 method which needs root permissions and probably won't work on newer devices. Here is the great answer how you can improve this: http://stackoverflow.com/a/10069679/1552030 .
Current method allowed me to achieve ~5FPS on Samsung Galaxy SIII mini, but most of the time was spent for JPEG compression.
As a live stream video I used M-JPEG.
