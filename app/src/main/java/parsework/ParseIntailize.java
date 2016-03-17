package parsework;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

/**
 * Created by osx on 29/07/15.
 */
public class ParseIntailize extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        Parse.initialize(this, "w2n2OvoIGiI4hmVpH0wKrgzBHA0U1l6zCe3HU1iK", "MLONVW924iAB0Ov6m8rzrsCaueMkwJryoc0wqfJv");
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);


    }
}
