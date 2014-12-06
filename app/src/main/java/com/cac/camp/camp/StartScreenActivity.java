package com.cac.camp.camp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.util.ArrayList;


public class StartScreenActivity extends Activity {
    private ServerCommunicator sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sc = new ServerCommunicator(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartViewFragment())
                    .commit();
        }
    }

    public void runLogAcc(View view) {
        Intent intent = new Intent(this, LogAccActivity.class);
        startActivity(intent);
    }


    //Create a fixed user, no feedback is given
    public void createUser(View view) {
        ArrayList<String> chill = new ArrayList();
        chill.add("God Sang");

        ArrayList<String> calm = new ArrayList();
        calm.add("rolig Sang");

        ArrayList<String> normal = new ArrayList();
        normal.add("normal Sang");

        ArrayList<String> wild = new ArrayList();
        wild.add("Vild Sang");

        sc.createUser("User4", chill, calm, normal, wild);
    }

    //Create playlist
    public void createPlaylist(View view) {

        ArrayList<String> users = new ArrayList();
        users.add("User4");

        sc.createPlaylist(users, "normalParty");
    }

    //Update playlist
    public void updatePlaylist(View view) {
        ArrayList<String> users = new ArrayList();
        users.add("User4");

        sc.updatePlaylist(users, "normalParty", "5482dcf2c9284");
    }

    //get playlist
    public void getPlaylist(View view) {
        sc.getPlaylist("5482dcf2c9284");
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StartViewFragment extends Fragment {

        public StartViewFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
            return rootView;
        }
    }
}
