package xdean.msgew.client.android;

import android.content.Intent;

import io.flutter.app.FlutterApplication;

public class MainApplication extends FlutterApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, MessageFetchService.class));
    }
}
