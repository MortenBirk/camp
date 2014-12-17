package com.cac.camp.camp;

import com.android.volley.RequestQueue;

/**
 * Created by Birk on 15-12-2014.
 */
public class RequestUpdates extends Thread {

    private StartScreenActivity activity = null;

    public RequestUpdates(StartScreenActivity activity) {
        this.activity = activity;
    }

    public void run() {
        while (true) {
            activity.getContexts();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.updateView();
                }
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
