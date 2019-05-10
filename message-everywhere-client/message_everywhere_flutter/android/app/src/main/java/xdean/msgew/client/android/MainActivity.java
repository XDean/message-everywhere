package xdean.msgew.client.android;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "message-everywhere-fetch";
    private MessageFetchService.Binder binder;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MessageFetchService.Binder) service;
            Log.d("MainActivity", "service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MainActivity", "service disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);
        bindService(new Intent(this, MessageFetchService.class),
                connection, BIND_AUTO_CREATE);
        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                        if (call.method.equals("run")) {
                            if (binder == null) {
                                result.success(false);
                            } else {
                                result.success(binder.run(call.argument("url"), call.argument("encode")));
                            }
                        } else if (call.method.equals("stop")) {
                            if (binder == null) {
                                result.success(false);
                            } else {
                                result.success(binder.stop());
                            }
                        } else {
                            result.notImplemented();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
