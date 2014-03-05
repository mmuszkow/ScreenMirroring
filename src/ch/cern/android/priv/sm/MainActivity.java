package ch.cern.android.priv.sm;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends Activity {

    private static final int PORT = 6100;

    private BroadcastReceiver mStatusChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView statusLabel = (TextView) findViewById(R.id.statusLabel);

        // this will present current status of service on the label
        mStatusChangeReceiver = new StatusChangeReceiver(statusLabel);
        registerReceiver(
                mStatusChangeReceiver,
                new IntentFilter(StatusChangeReceiver.ACTION));

        // start service
        Intent i = new Intent(MainActivity.this, GrabbingService.class);
        i.putExtra("port", PORT);
        if (startService(i) != null) {
            statusLabel.setText("Service already running");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusChangeReceiver);
    }

}
