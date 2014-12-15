package com.cac.camp.camp;

/**
 * Created by Birk on 15-12-2014.
 */
public class SendUpdated extends Thread {

    private StartScreenActivity activity = null;
    public SendUpdated(StartScreenActivity activity) {
        this.activity = activity;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.updateContextAndPosition();
        }
    }
}
