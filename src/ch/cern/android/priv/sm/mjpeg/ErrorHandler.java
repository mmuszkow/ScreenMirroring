package ch.cern.android.priv.sm.mjpeg;

import java.io.OutputStream;
import java.net.Socket;

import ch.cern.android.priv.sm.http.HttpHandler;

import android.util.Log;

public class ErrorHandler extends HttpHandler {
    public ErrorHandler(Socket sock) {
        super(sock);
    }

    @Override
    public void run() {
        try {
            OutputStream os = getSock().getOutputStream();
            os.write(("HTTP/1.1 404 Not Found\r\n" +
                    "Date: " + getServerTime() + "\r\n" +
                    "Server: " + SERVER_NAME + "\r\n" +
                    "Content-Type: text/plain;charset=utf-8\r\n" +
                    "Connection: close\r\n\r\n" +
                    "Not Found").getBytes());
            getSock().close();
        } catch (Exception e) {
            Log.e("SM", "Home page handler error", e);
        }
    }
}
