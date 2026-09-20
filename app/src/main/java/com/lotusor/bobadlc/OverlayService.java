package com.lotusor.bobadlc;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class OverlayService extends Service {
    private WindowManager wm;
    private View overlay;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView tvMain;

    @Override
    public void onCreate() {
        super.onCreate();

        // Foreground notification
        NotificationChannel ch = new NotificationChannel("bobadlc","BobaDLC",NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        Notification notif = new Notification.Builder(this,"bobadlc")
            .setContentTitle("BobaDLC External").setContentText("Running").setSmallIcon(android.R.drawable.ic_menu_view).build();
        startForeground(1, notif);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Создаём overlay layout
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.argb(210, 18, 18, 18));
        root.setPadding(12, 8, 12, 8);

        // атермарка
        TextView wm_tv = new TextView(this);
        wm_tv.setText("● BobaDLC External");
        wm_tv.setTextColor(Color.rgb(229, 57, 53));
        wm_tv.setTextSize(13);
        root.addView(wm_tv);

        // гроки
        tvMain = new TextView(this);
        tvMain.setText("Waiting for game...");
        tvMain.setTextColor(Color.WHITE);
        tvMain.setTextSize(11);
        root.addView(tvMain);

        overlay = root;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            350, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 8; params.y = 8;

        wm.addView(overlay, params);

        // бновляем данные каждую секунду
        handler.post(updateTask);
    }

    private Runnable updateTask = new Runnable() {
        @Override public void run() {
            try {
                File f = new File("/data/local/tmp/bobadlc_data.txt");
                if (f.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line).append("\n");
                    br.close();
                    tvMain.setText(sb.toString());
                }
            } catch (Exception e) { tvMain.setText("Error: " + e.getMessage()); }
            handler.postDelayed(this, 1000);
        }
    };

    @Override public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
        if (overlay != null) wm.removeView(overlay);
    }

    @Override public IBinder onBind(Intent i) { return null; }
}
