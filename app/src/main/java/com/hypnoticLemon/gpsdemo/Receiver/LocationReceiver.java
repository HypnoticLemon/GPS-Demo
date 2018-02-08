package com.hypnoticLemon.gpsdemo.Receiver;


import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class LocationReceiver extends ResultReceiver {
    /** * Create a new ResultReceive to receive results. Your * {@link #onReceiveResult} method will be called from the thread running * <var>handler</var> if given, or from an arbitrary thread if null. * * @param handler */
    private Receiver mReceiver;
    public LocationReceiver(Handler handler) {
        super(handler);
    }
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
    @Override protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
