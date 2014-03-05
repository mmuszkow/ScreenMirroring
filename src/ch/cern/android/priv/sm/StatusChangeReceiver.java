package ch.cern.android.priv.sm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

public class StatusChangeReceiver extends BroadcastReceiver {

    public final static String ACTION = "ch.cern.android.priv.sm.status";

    private TextView mStatusLabel;

    public StatusChangeReceiver(TextView label) {
        mStatusLabel = label;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int ret = intent.getIntExtra("ret", 1);
        if (ret == 0) {
            String addr = intent.getStringExtra("addr");
            mStatusLabel.setText("Service started at " + addr);
        } else {
            String err = intent.getStringExtra("err");
            mStatusLabel.setText("Error: " + err);
        }
    }
}
