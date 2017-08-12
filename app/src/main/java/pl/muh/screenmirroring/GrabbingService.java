package pl.muh.screenmirroring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.muh.screenmirroring.mjpeg.MJPEGHServer;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

public class GrabbingService extends Service {

    private MJPEGHServer mServer;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent statusUpdate = new Intent(StatusChangeReceiver.ACTION);
        try {
            // fix permissions
            Process proc = Runtime.getRuntime().exec(
                    "su -c chmod 777 /dev/graphics/fb0");
            if (proc.waitFor() != 0) {
                throw new IOException("chmod failed");
            }
            proc = Runtime.getRuntime().exec(
                    "su -c setenforce 0");
            proc.waitFor();

            // start server
            Resources res = getApplicationContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            mServer = new MJPEGHServer(dm, 6100);
            mServer.start();

            // set ok status
            Log.i("SM", "Server started");
            statusUpdate.putExtra("ret", 0);
            statusUpdate.putExtra("addr", getIp() + ":" + 6100);
            sendBroadcast(statusUpdate);
        } catch (Exception e) {
            // set status with error
            Log.e("SM", "Cannot start server", e);
            statusUpdate.putExtra("ret", 1);
            if (e.getMessage() != null) {
                statusUpdate.putExtra("err", e.getMessage());
            } else {
                statusUpdate.putExtra("err", e.getClass());
            }
            sendBroadcast(statusUpdate);
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

    private String getIp() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }
}
