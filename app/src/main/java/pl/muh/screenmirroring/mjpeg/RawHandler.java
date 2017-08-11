package pl.muh.screenmirroring.mjpeg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import pl.muh.screenmirroring.http.HttpHandler;

public class RawHandler extends HttpHandler {

    private byte[] mFrameBuffer;
    private int[] mColors;
    private int mWidth;
    private int mHeight;

    public RawHandler(Socket sock, DisplayMetrics dm) {
        super(sock);
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mFrameBuffer = new byte[mWidth * mHeight * 4];
        mColors = new int[mWidth * mHeight];
    }

    @Override
    public void run() {
        try {
            int prevHash = 0;
            while (!isInterrupted()) {
                // grab screenshot ~10ms, TODO: no root
                DataInputStream dis = new DataInputStream(
                        new FileInputStream(
                                "/dev/graphics/fb0"));
                dis.read(mFrameBuffer);
                dis.close();

                // compress ~150 ms
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

                if (prevHash != hash) {
                    prevHash = hash;
                    Bitmap bmp = Bitmap.createBitmap(mColors, mWidth,
                            mHeight, Bitmap.Config.ARGB_8888);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    baos.flush();
                    byte[] imageData = baos.toByteArray();
                    baos.close();

                    // send single JPEG binary
                    getSock().setSendBufferSize(128 * 1024);
                    OutputStream os = getSock().getOutputStream();
                    os.write(ByteBuffer.allocate(4).putInt(imageData.length)
                            .array());
                    os.write(imageData);

                    // wait for ping
                    getSock().getInputStream().read();
                }
            }

            getSock().close();
        } catch (Exception e) {
            Log.e("SM", "Raw handler error", e);
        }
    }
}
