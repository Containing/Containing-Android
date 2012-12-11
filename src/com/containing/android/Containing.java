package com.containing.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Containing extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_containing, menu);
        return true;
    }
}
