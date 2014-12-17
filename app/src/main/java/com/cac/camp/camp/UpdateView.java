package com.cac.camp.camp;

import android.os.AsyncTask;

/**
 * Created by Birk on 17-12-2014.
 */
public class UpdateView extends AsyncTask<StartScreenActivity, Void, Void> {

    @Override
    protected Void doInBackground(StartScreenActivity[] object) {
        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            object[0].updateView();
        }
    }
}
