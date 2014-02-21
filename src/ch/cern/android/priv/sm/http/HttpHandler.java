package ch.cern.android.priv.sm.http;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public abstract class HttpHandler extends Thread {
    private Socket mSock;

    public HttpHandler(Socket sock) {
        this.start();
        mSock = sock;
    }

    public Socket getSock() {
        return mSock;
    }

    public static final String SERVER_NAME = "M-JPEG server by MJM";

    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
}
