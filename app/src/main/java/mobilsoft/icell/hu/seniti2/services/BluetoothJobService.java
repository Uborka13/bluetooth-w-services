package mobilsoft.icell.hu.seniti2.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import mobilsoft.icell.hu.seniti2.helpers.BluetoothController;

public class BluetoothJobService extends JobService implements BluetoothController.BluetoothListener, BeaconConsumer {

    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String JOB_SERVICE_NOTIFICATION_CHANNEL_ID = "201";
    private static final String TAG = BluetoothJobService.class.getSimpleName();
    private static final String CTAG = "<-- Seniti2 -->";

    private BeaconManager beaconManager;
    private Region region;
    private JobParameters params;
    private Listener listener;

    public BluetoothJobService() {
        //Empty constructor
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Micsoda?", Toast.LENGTH_SHORT).show();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_LAYOUT));
        beaconManager.setForegroundBetweenScanPeriod(100L);
        beaconManager.setBackgroundBetweenScanPeriod(100L);
        beaconManager.bind(this);
        Log.i(TAG, "Job service created -->JobService<--");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Job service command started -->JobService<--");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JobInfo.Builder builder = new JobInfo.Builder(2, new ComponentName(this, BluetoothJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setBackoffCriteria(300000L, JobInfo.BACKOFF_POLICY_LINEAR);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(builder.build());
        Log.i(TAG, "Job service destroyed -->JobService<--");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        Toast.makeText(this, "Job started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Job service job started " + CTAG);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job service job stopped " + CTAG);
        return false;
    }

    @Override
    public void showDialog(Context context, String message) {
        // Do nothing warn a different way
    }

    @Override
    public void found(ScanResult device) {
        Log.i(TAG, "Job service device found " + device.getDevice().getAddress() + "  -->JobService<--");
    }

    @Override
    public void finished() {
        Log.i(TAG, "Job service scan finished -->JobService<--");
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(JOB_SERVICE_NOTIFICATION_CHANNEL_ID, "noteChannel", NotificationManager.IMPORTANCE_MIN));
        }
    }


    @Override
    public void onBeaconServiceConnect() {
        region = new Region("BluetoothService", null, null, null);
        beaconManager.addRangeNotifier((collection, region) -> {
            Log.d(TAG, "onBeaconServiceConnect: range: " + collection.size() + " " + CTAG);
            if (listener != null) {
                listener.sendData(collection.size() + " méter");
            }
        });
        try {
            beaconManager.startRangingBeaconsInRegion(region);
            Log.d(TAG, "Ranging Started" + CTAG);
            Toast.makeText(this, "Ranging Started", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                beaconManager.stopRangingBeaconsInRegion(region);
                handler.postDelayed(() -> {
                    jobFinished(params, false);
                }, 300000L);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }, 10000L);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void sendData(String value);
    }

    public class ServiceBinder extends Binder {
        public BluetoothJobService getService() {
            return BluetoothJobService.this;
        }
    }
}
