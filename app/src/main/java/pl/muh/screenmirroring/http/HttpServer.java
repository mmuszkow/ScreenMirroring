package pl.muh.screenmirroring.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

/**
 * Created by MJM on 2017-08-11.
 */
public abstract class HttpServer extends Thread {

    private ServerSocket mServerSock;
    private byte[] mReadBuffer;

    public HttpServer(int port)
            throws IOException {
        mServerSock = new ServerSocket(port);
        mReadBuffer = new byte[4096];
    }

    @Override
    public void run() {
        Log.i("SM", "Server thread started");
        while (!isInterrupted()) {
            try {
                // read request
                Socket incoming = mServerSock.accept();
                incoming.setSendBufferSize(32 * 1024);
                InputStream is = incoming.getInputStream();
                if (is.read(mReadBuffer) != -1) {
                    // parse
                    String req = new String(mReadBuffer, "UTF-8");
                    String[] lines = req.split("\\r?\\n");
                    if (lines.length > 0) {
                        String[] head = lines[0].split("\\s+");
                        if (head.length == 3 && head[0].equals("GET")) {
                            // handle GET
                            handlePath(incoming, head[1]);
                        } else {
                            Log.i("SM", "Method unsupported or wrong head");
                        }
                    } else {
                        Log.i("SM", "Empty request");
                        incoming.close();
                    }
                } else {
                    Log.i("SM", "Server read error");
                }
            } catch (Exception e) {
                Log.e("SM", "Server error", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    return;
                }
            }
        }
        try {
            mServerSock.close();
        } catch (IOException e) {
            Log.e("SM", "Error closing server socket", e);
        }
        Log.i("SM", "Server thread finished");
    }

    public abstract void handlePath(Socket sock, String path);
}
