package ch.cern.android.priv.sm;

import java.io.IOException;

import ch.cern.android.priv.sm.mjpeg.MJPEGHServer;


import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;

public class GrabbingService extends Service {
    private MJPEGHServer mServer;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Resources res = getApplicationContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            mServer = new MJPEGHServer(dm, 6100);
            mServer.start();
        } catch (IOException e) {
            Log.e("SM", "Cannot start server", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServer != null) {
            mServer.interrupt();
            Log.i("SM", "Server stopped");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
