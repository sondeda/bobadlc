package com.lotusor.bobadlc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private static final int OVERLAY_PERMISSION_REQ = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("BobaDLC External by Lotusor");
        title.setTextSize(20);
        layout.addView(title);

        Button btn = new Button(this);
        btn.setText("апустить оверлей");
        btn.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_PERMISSION_REQ);
            } else {
                startOverlay();
            }
        });
        layout.addView(btn);

        Button stop = new Button(this);
        stop.setText("становить оверлей");
        stop.setOnClickListener(v -> stopService(new Intent(this, OverlayService.class)));
        layout.addView(stop);

        setContentView(layout);

        if (Settings.canDrawOverlays(this)) startOverlay();
    }

    private void startOverlay() {
        startForegroundService(new Intent(this, OverlayService.class));
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == OVERLAY_PERMISSION_REQ && Settings.canDrawOverlays(this))
            startOverlay();
    }
}
