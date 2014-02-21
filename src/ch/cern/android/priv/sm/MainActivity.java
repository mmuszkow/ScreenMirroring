package ch.cern.android.priv.sm;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

    private TextView mStatusLabel;
    private Button mFixPermButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // UI
        setContentView(R.layout.activity_main);
        mStatusLabel = (TextView) findViewById(R.id.statusLabel);
        mFixPermButton = (Button) findViewById(R.id.fixPermButton);

        // Fix permissions click
        mFixPermButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Process proc = Runtime.getRuntime().exec(
                            "su -c chmod 777 /dev/graphics/fb0");
                    if (proc.waitFor() != 0) {
                        mStatusLabel.setText("Error: chmod failed");
                    } else {
                        mStatusLabel.setText("Permissions fixed");
                    }
                } catch (Exception e) {
                    mStatusLabel.setText("Error: chmod failed, "
                            + e.getMessage());
                }
            }
        });

        Intent i = new Intent(MainActivity.this, GrabbingService.class);
        i.putExtra("port", 6100);
        if (startService(i) != null) {
            mStatusLabel.setText("Service started at " + getIp() + ":6100");
        } else {
            mStatusLabel.setText("Service already running");
        }
    }

    private String getIp() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }
}
