package xdean.msgew.client.android;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import xdean.msgew.client.android.listener.SmsListener;
import xdean.msgew.client.android.model.Message;
import xdean.msgew.client.android.model.Messager;

public class MessageFetchService extends Service {

    public class Binder extends android.os.Binder {
        public boolean run(String url, String encode) {
            return MessageFetchService.this.run(url, encode);
        }

        public boolean stop() {
            return MessageFetchService.this.stop();
        }
    }

    public static final String TAG = "MessageFetch";
    private final SmsListener receiver = new SmsListener();
    private final AtomicBoolean run = new AtomicBoolean(false);
    private volatile boolean running = false;
    private volatile String url;
    private volatile String encode;
    private volatile MsgewServer server;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        receiver.observe()
                .filter(e -> running)
                .observeOn(Schedulers.io())
                .subscribe(e -> processMsg(e));
        IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(10000);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        unregisterReceiver(receiver);
        receiver.done();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public boolean run(String url, String encode) {
        if (!running && run.compareAndSet(false, true)) {
            this.url = url;
            this.encode = encode;
            this.running = true;
            this.server = new Retrofit.Builder()
                    .baseUrl(url)
                    .build()
                    .create(MsgewServer.class);
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (running && run.compareAndSet(true, false)) {
            this.running = false;
            return true;
        }
        return false;
    }

    private void processMsg(SmsMessage msg) {
        Log.d("processMsg", msg.toString());
        this.server.send(Message.builder()
                .content(msg.getMessageBody())
                .timestamp(msg.getTimestampMillis())
                .from(Messager.builder()
                        .id(msg.getOriginatingAddress())
                        .build())
                .build());
    }
}
