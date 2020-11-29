package org.techtown.foryou;

import android.view.Menu;

public interface OnRequestListener {
    boolean onCreateOptionMenu(Menu menu);

    public void onRequest(String command);
}
