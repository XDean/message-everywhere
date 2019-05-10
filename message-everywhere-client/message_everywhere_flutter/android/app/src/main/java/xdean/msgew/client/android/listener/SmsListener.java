package xdean.msgew.client.android.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SmsListener extends BroadcastReceiver {

    private final Subject<SmsMessage> messageSubject = PublishSubject.create();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Sms", "wow: " + intent);
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (Object pdu : pdus) {
                        messageSubject.onNext(SmsMessage.createFromPdu((byte[]) pdu));
                    }
                } catch (Exception e) {
                    Log.e("SmsListener", "Exception caught when receive msg", e);
                }
            }
        }
    }

    public Observable<SmsMessage> observe() {
        return messageSubject;
    }

    public void done() {
        messageSubject.onComplete();
    }
}