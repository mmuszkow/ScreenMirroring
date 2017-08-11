package pl.muh.screenmirroring.mjpeg;

import java.io.IOException;
import java.net.Socket;

import android.util.DisplayMetrics;
import android.util.Log;

import pl.muh.screenmirroring.http.HttpServer;

public class MJPEGHServer extends HttpServer {

    private DisplayMetrics mDm;

    public MJPEGHServer(DisplayMetrics dm, int port) throws IOException {
        super(port);
        mDm = dm;
    }

    @Override
    public void handlePath(Socket sock, String path) {
        Log.i("SM", "HTTP req: " + path);
        if (path.equals("/")) {
            new HomeHandler(sock);
        } else if (path.equals("/frame.mjpeg")) {
            new MJPEGHandler(sock, mDm);
        } else if (path.equals("/raw")) {
            new RawHandler(sock, mDm);
        } else {
            new ErrorHandler(sock);
        }
    }
}
