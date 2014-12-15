package com.cac.camp.camp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jensemil on 11/12/14.
 */
public interface ClientActivity {

    public void setCurrentPlaylist(String id, ArrayList<String> playlist);
    //Used to calculate the context and users to create a playlist for.
    public void deriveCommonContext(ArrayList<String> users, ArrayList<String> contexts);
}
