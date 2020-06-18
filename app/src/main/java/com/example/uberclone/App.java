package com.example.uberclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("e4fwjA11tF7xY6bQAxSKxy3TvBgj4r6plOA1tadj")
                .clientKey("IycgDJazfzSLg9NUF1JFjhwoPDPjmrgmcQ2Yz49Z")
                .server("https://parseapi.back4app.com/")
                .build()
        );

    }
}
