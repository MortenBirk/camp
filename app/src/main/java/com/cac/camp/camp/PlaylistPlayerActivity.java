package com.cac.camp.camp;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;

import com.spotify.sdk.android.playback.Player;

import java.util.ArrayList;
import java.util.List;


public class PlaylistPlayerActivity extends Activity {
    Player mPlayer;
    List<String> playlist = new ArrayList<String>();
    ListView playlistView;
    Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_player);

        playlistView = (ListView) findViewById(R.id.playlistView);

        playlist.add("spotify:track:5QruhkvTtEi6cR8nSr6LwT");
        playlist.add("spotify:track:4KqQfcx8RM9liPiHEvEJNg");
        playlist.add("spotify:track:768j11Y0ksbgdncnFuAEZ4");
        playlist.add("spotify:track:3DqhtDaWayCtfV69d2p5vH");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(),
                android.R.layout.simple_list_item_1,
                playlist
        );

        playlistView.setAdapter(adapter);

        mPlayer = SpotifySingleton.getInstance().getPlayer();

        if (isPlaying == false) {
            mPlayer.play(playlist);
            isPlaying = true;
        }

        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button playbutton = (Button) findViewById(R.id.playbutton);
        Button nextbutton = (Button) findViewById(R.id.nextbutton);
        Button previousbutton = (Button) findViewById(R.id.previousbutton);
        playbutton.setTypeface(font);
        nextbutton.setTypeface(font);
        previousbutton.setTypeface(font);

    }

    public void play(View view){
        Button playbutton = (Button) findViewById(R.id.playbutton);
        if (isPlaying) {
            mPlayer.pause();
            playbutton.setText(R.string.fa_pause);

        } else {
            mPlayer.resume();
            playbutton.setText(R.string.fa_play);
        }
        isPlaying = !isPlaying;
    }

    public void next(View view){
        mPlayer.skipToNext();
    }

    public void previous(View view) {
        mPlayer.skipToPrevious();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
