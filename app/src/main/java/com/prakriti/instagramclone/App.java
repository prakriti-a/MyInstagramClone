package com.prakriti.instagramclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("2JrhuvpnqqAPAYkjlJ3l7n7XQqWVXdBwjy1hh5Zf")
                // if defined
                .clientKey("cAaktirGBhn2FVIJ1g2YVfLyKFS9gXALt4vCfqF0")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
