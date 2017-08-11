package pl.muh.screenmirroring.mjpeg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import pl.muh.screenmirroring.http.HttpHandler;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

public class MJPEGHandler extends HttpHandler {

    private byte[] mFrameBuffer;
    private int[] mColors;
    private int mWidth;
    private int mHeight;

    public MJPEGHandler(Socket sock, DisplayMetrics dm) {
        super(sock);
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mFrameBuffer = new byte[mWidth * mHeight * 4];
        mColors = new int[mWidth * mHeight];
    }

    @Override
    public void run() {
        try {
            Log.i("SM", "Send buffer size: " + getSock().getSendBufferSize());
            OutputStream os = getSock().getOutputStream();
            String boundary = "screenFrame";
            os.write(("HTTP/1.1 200 OK\r\n" +
                    "Date: " + getServerTime() + "\r\n" +
                    "Server: " + SERVER_NAME + "\r\n" +
                    "Content-Type: multipart/x-mixed-replace; boundary=--" +
                    boundary + "\r\n" +
                    "Cache-Control: no-cache, private\r\n" +
                    "Pragma: no-cache\r\n" +
                    "Max-Age: 0\r\n" +
                    "Expires: 0\r\n" +
                    "Connection: keep-alive\r\n\r\n").getBytes());

            int prevHash = 0;
            // Speed speed = new Speed();
            // Timer timer = new Timer();
            while (!isInterrupted()) {

                // grab screenshot ~10ms, TODO: no root
                DataInputStream dis = new DataInputStream(
                        new FileInputStream(
                                "/dev/graphics/fb0"));
                dis.read(mFrameBuffer);
                dis.close();

                // compress ~150 ms
                // timer.reset("compress");
                int r, g, b, a, index;
                int hash = 0;
                for (int m = 0; m < mColors.length; m++) {
                    index = m * 4;
                    r = (mFrameBuffer[index] & 0xFF);
                    g = (mFrameBuffer[index + 1] & 0xFF);
                    b = (mFrameBuffer[index + 2] & 0xFF);
                    a = (mFrameBuffer[index + 3] & 0xFF);
                    mColors[m] = (a << 24) | (b << 16) | (g << 8) | r;
                    hash ^= mColors[m];
                }
                // send only if changed, TODO: when no changes, image sometimes
                // is not refereshing in the browser...
                if (prevHash != hash) {
                    prevHash = hash;
                    Bitmap bmp = Bitmap.createBitmap(mColors, mWidth,
                            mHeight, Bitmap.Config.ARGB_8888);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    baos.flush();
                    byte[] imageData = baos.toByteArray();
                    baos.close();
                    // timer.stop("compress");

                    // send single JPEG
                    // timer.reset("send");
                    // speed.reset();
                    os.write(("--" + boundary + "\r\n" +
                            "Content-type: image/jpeg\r\n" +
                            "Content-Length: " + imageData.length +
                            "\r\n\r\n").getBytes());
                    os.write(imageData);
                    os.write(("\r\n").getBytes());
                    os.flush();
                    // timer.stop("send");
                    // speed.print(imageData.length);
                }
            }
            getSock().close();
        } catch (Exception e) {
            Log.e("SM", "MJPEG stream error", e);
        }
    }

    @SuppressWarnings("unused")
    private class Timer {
        private Map<String, Long> mTimers;

        public Timer() {
            mTimers = new HashMap<String, Long>();
        }

        public void reset(String name) {
            mTimers.put(name, System.currentTimeMillis());
        }

        public void stop(String name) {
            if (mTimers.containsKey(name)) {
                long curr = System.currentTimeMillis();
                Log.i("SM", name + " timing is " + (curr - mTimers.get(name))
                        + "ms");
            }
        }
    }

    @SuppressWarnings("unused")
    private class Speed {
        private long mSpeedStart;

        public Speed() {
            reset();
        }

        public void reset() {
            mSpeedStart = System.currentTimeMillis();
        }

        public void print(int sent) {
            float kb = sent / 1024.0f;
            float dt = (System.currentTimeMillis() - mSpeedStart) / 1000.0f;
            if (dt != 0) {
                Log.i("SM", "Speed " + (kb / dt) + "KB/s");
            }
        }
    }
}
