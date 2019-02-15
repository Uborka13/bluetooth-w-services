package mobilsoft.icell.hu.seniti2.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import mobilsoft.icell.hu.seniti2.R;
import mobilsoft.icell.hu.seniti2.activities.MainActivity;
import mobilsoft.icell.hu.seniti2.activities.ParkingActivity;
import mobilsoft.icell.hu.seniti2.helpers.BluetoothController;

import static android.app.NotificationManager.IMPORTANCE_MIN;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.NotificationStatus.NOT_SEARCHING_FOUND;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.NotificationStatus.NOT_SEARCHING_NOT_FOUND;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.NotificationStatus.SEARCHING_FOUND;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.NotificationStatus.SEARCHING_NOT_FOUND;

public class BluetoothService extends Service implements BluetoothController.BluetoothListener {

    public static final int STOP_SERVICE = -1;
    public static final int START_PARKING = -1;
    public static final String BLUETOOTH_SERVICE = "BLUETOOTH_SERVICE";
    private static final String TAG = "BLUETOOTH_SERVICE";
    private static final String NOTIFICATION_CHANNEL_ID = "100";
    private BluetoothController bluetoothController;
    private String zoneId;
    private boolean scanFinished = false;
    private Timer timer = new Timer();
    private TimerTask notificationClearTask;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "Service bluetoothReceiver FOUND -->Service<--");
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG, "Service bluetoothReceiver STATE_OFF -->Service<--");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "Service bluetoothReceiver STATE_TURNING_OFF -->Service<--");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG, "Service bluetoothReceiver STATE_ON -->Service<--");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "Service bluetoothReceiver STATE_TURNING_ON -->Service<--");
                        break;
                    default:
                        Log.i(TAG, "Service bluetoothReceiver default -->Service<--");
                        break;
                }
            }
        }
    };

    public BluetoothService() {
        // Empty
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "service <-> onStartCommand");
        startForeground(10, createNotification(null, SEARCHING_NOT_FOUND));
        startScanning();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service <-> onCreate");
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
        bluetoothController = new BluetoothController(this, this, "00005eba-0000-1000-8000-00805f9b34fb");
        createNotificationChannel();
    }

    private PendingIntent createStopPendingIntent() {
        Intent stopIntent = new Intent(this, MainActivity.class);
        stopIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(BLUETOOTH_SERVICE, STOP_SERVICE);
        stopIntent.putExtras(bundle);
        Random rn = new Random();
        return PendingIntent.getActivity(this, rn.nextInt(1000), stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createStartParkingPendingIntent() {
        Intent startParkingIntent = new Intent(this, ParkingActivity.class);
        startParkingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(BLUETOOTH_SERVICE, START_PARKING);
        startParkingIntent.putExtras(bundle);
        Random rn = new Random();
        return PendingIntent.getActivity(this, rn.nextInt(1000), startParkingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, "noteChannel", NotificationManager.IMPORTANCE_MIN));
        }
    }

    private Notification createNotification(@Nullable final String zone, NotificationStatus status) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        if (status == SEARCHING_NOT_FOUND) {
            builder.setContentTitle("Parking zone searching")
                    .addAction(R.drawable.ic_highlight_off_white_24dp, "Stop searching", createStopPendingIntent())
                    .setContentText("No zone found");
        } else if (status == SEARCHING_FOUND) {
            builder.setContentTitle("Parking zone searching")
                    .addAction(R.drawable.ic_highlight_off_white_24dp, "Stop searching", createStopPendingIntent())
                    .addAction(R.drawable.ic_check_circle_white_24dp, "Start parking", createStartParkingPendingIntent())
                    .setContentText("Zone found: " + zone);
        } else if (status == NOT_SEARCHING_FOUND) {
            builder.setContentTitle("Parking zone search stopped")
                    .addAction(R.drawable.ic_highlight_off_white_24dp, "Close search", createStopPendingIntent())
                    .addAction(R.drawable.ic_check_circle_white_24dp, "Start parking", createStartParkingPendingIntent())
                    .setContentText("Zone found: " + zone);
        } else if (status == NOT_SEARCHING_NOT_FOUND) {
            builder.setContentTitle("Parking zone search stopped")
                    .addAction(R.drawable.ic_highlight_off_white_24dp, "Close search", createStopPendingIntent())
                    .setContentText("No zone found");
        }
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setPriority(IMPORTANCE_MIN);
        builder.setSmallIcon(R.drawable.ic_local_parking_white_24dp);
        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startScanning() {
        Log.d(TAG, "service <-> scan started");
        setScanFinished(false);
        bluetoothController.startLeScanning(600);
    }

    @Override
    public void found(ScanResult device) {
        zoneId = device.getDevice().getAddress();
        startForeground(10, createNotification(zoneId, SEARCHING_FOUND));
        Log.d(TAG, "service <-> found device --> " + device.getDevice().getAddress());
        if (notificationClearTask != null) {
            notificationClearTask.cancel();
        }
        notificationClearTask = refreshedTimerTask();
        timer.schedule(notificationClearTask, 10000L);
    }

    @Override
    public void finished() {
        startForeground(10, createNotification(zoneId, (zoneId == null ? NOT_SEARCHING_NOT_FOUND : NOT_SEARCHING_FOUND)));
        setScanFinished(true);
        Log.d(TAG, "service <-> finished scanning");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service <-> destroyed");
        unregisterReceiver(receiver);
        if (bluetoothController != null) {
            bluetoothController.stopLeScanning();
        }
    }

    @Override
    public void showDialog(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isScanFinished() {
        return scanFinished;
    }

    public void setScanFinished(boolean scanFinished) {
        this.scanFinished = scanFinished;
    }

    private TimerTask refreshedTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "service <-> runner --> stopped");
                Log.d(TAG, "service <-> noZoneNotification --> entered");
                if (!isScanFinished()) {
                    Log.d(TAG, "service <-> noZoneNotification --> notification cleared");
                    startForeground(10, createNotification(null, SEARCHING_NOT_FOUND));
                    zoneId = null;
                }
                Log.d(TAG, "service <-> noZoneNotification --> exited");
                setScanFinished(false);
            }
        };
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "service <-> task removed");
    }

    enum NotificationStatus {
        SEARCHING_NOT_FOUND(100),
        SEARCHING_FOUND(101),
        NOT_SEARCHING_FOUND(102),
        NOT_SEARCHING_NOT_FOUND(103);

        private final int status;

        NotificationStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
