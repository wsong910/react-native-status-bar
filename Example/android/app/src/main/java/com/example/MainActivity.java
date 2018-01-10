package com.example;

import android.os.Bundle;

import com.facebook.react.ReactActivity;

import im.shi.statusbarmanager.RNStatusbarManagerModule;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "Example";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RNStatusbarManagerModule.translucentStatusBar(this, true);
//        setContentView(R.layout.test);
//        View content = LayoutInflater.from(this).inflate(R.layout.test, null);
//        RNStatusbarManagerModule.steepStatusbarView(this, content, android.R.color.white);
    }
}
