package pl.muh.screenmirroring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends AppCompatActivity {

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
        if (startService(new Intent(MainActivity.this, GrabbingService.class)) != null)
            statusLabel.setText("Service already running");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusChangeReceiver);
    }

}
