package com.atmecs.taxi.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
//    Boolean isMessageRecieved;
    String messageString;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        if (data != null){
            for(int index = 0;index < pdus.length; index++){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[index]);
                String sender = smsMessage.getDisplayOriginatingAddress();
//                isMessageRecieved = :sender.endsWith("Naiknaware");
//                isMessageRecieved = sender.contains("Naiknaware");
                String messageBody = smsMessage.getMessageBody();
                messageString = messageBody.replaceAll("[^0-9]","");
                mListener.messageReceived(messageString);
                Log.v("mListener: ",""+ messageString);
            }
        }else {
            Log.v("error null obj: ",""+ pdus.length);
        }
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
