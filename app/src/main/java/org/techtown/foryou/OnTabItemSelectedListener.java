package org.techtown.foryou;

import android.view.Menu;

public interface OnTabItemSelectedListener {
    public void onTabSelected(int position);
    public void showFragment2(Note item);

    void showdiary1(Note item);
    boolean onCreateOptionMenu(Menu menu);
}
