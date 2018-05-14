package nl.easthome.testserviceapp;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public abstract class AndroidBasicServiceCombinator<T extends AndroidBasicServiceCombinator> extends Service {
    private String mLOG_TAG;
    private ServiceConnectioner mServiceConnectioner;
    private Binderer mBinder;
    private Intent mIntent;
    private int mBoundClients = 0;
    private boolean mNeedToLog;


    public AndroidBasicServiceCombinator() { }

    public AndroidBasicServiceCombinator(String LOG_TAG) {
        mNeedToLog = true;
        mLOG_TAG = LOG_TAG;
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        log("onCreate");
        mBinder = new Binderer();
        extraOnCreate();
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        extraOnDestroy();
    }

    @Nullable
    @Override
    public final IBinder onBind(Intent intent) {
        mBoundClients++;
        log("onBind - " + String.valueOf(mBoundClients) + " bound clients.");
        extraOnBind();
        return mBinder;
    }

    @Override
    public final boolean onUnbind(Intent intent) {
        mBoundClients--;
        log("onUnbind - " + String.valueOf(mBoundClients) + " bound clients.");
        extraOnUnbind();
        return super.onUnbind(intent);
    }

    public final static void sendMeIntent(Context context, Intent intent){
        try {
            PendingIntent.getService(context, 456, intent, PendingIntent.FLAG_UPDATE_CURRENT).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private final void log(String message){
        if (mNeedToLog && mLOG_TAG != ""){
            Log.d(mLOG_TAG, message);
        }
    }

    public final T bindMe(Context context, Class clazz, int contextBindMode){
        log("onBindMe");
        mIntent = new Intent(context, clazz);
        mServiceConnectioner = new ServiceConnectioner();
        context.bindService(mIntent, mServiceConnectioner, contextBindMode);
        return mServiceConnectioner.getService();
    }

    public final void unbindMe(Context context){
        log("onUnbindMe");
        context.unbindService(mServiceConnectioner);
    }

    private final class Binderer extends Binder {
        public T getService(){
            return returnOwnThis();
        }
    }

    private final class ServiceConnectioner implements ServiceConnection{

        T mService;
        private boolean mIsServiceBound;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Binderer binder  = (Binderer) service;
            mService = binder.getService();
            mIsServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

        public T getService() {
            return mService;
        }
    }

    protected abstract T returnOwnThis();
    protected abstract void extraOnCreate();
    protected abstract void extraOnDestroy();
    protected abstract void extraOnUnbind();
    protected abstract void extraOnBind();

}

