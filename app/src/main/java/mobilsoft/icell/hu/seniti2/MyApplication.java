package mobilsoft.icell.hu.seniti2;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service <-> application onCreate ");
    }
}
