package com.pathtracker.android.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = (TextView) findViewById(R.id.tvText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /*GpsData data = GpsData.fromBytes(new byte[]{53, (byte)133, 54, 51, 66, 28, 2, (byte)188, 12, 66, 42, 56, 2, 0, 119, (byte)173, 3, 0});
            tvText.setText(String.format("State: %s\nLatitude: %d deg %f min\nLongitude: %d deg %f min\nTime: %d\nDate: %d",
                    data.isActive() ? "Active" : "Void",
                    data.getLatitudeDegrees(), data.getLatitudeMinutes(),
                    data.getLongitudeDegrees(), data.getLongitudeMinutes(),
                    data.getTime(), data.getDate()));*/
            /*byte[] buffer = PathTracker.newBuffer();
            int msgLen = PathTracker.commandStopPath(buffer);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < msgLen; i++){
                builder.append((int)buffer[i]);
                builder.append(',');
            }
            tvText.setText(builder.toString());*/
            PathTracker tracker = new PathTracker();
            tracker.analyze((byte) 3);
            tracker.analyze((byte) 65);
            tracker.analyze((byte) 66);
            tracker.analyze((byte) 65);
            tracker.analyze((byte) 66);
            tracker.analyze((byte) 65);
            tvText.setText(tracker.getMessage());
        }

        return super.onOptionsItemSelected(item);
    }
}
