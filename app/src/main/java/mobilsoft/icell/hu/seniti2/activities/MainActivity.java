package mobilsoft.icell.hu.seniti2.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobilsoft.icell.hu.seniti2.R;
import mobilsoft.icell.hu.seniti2.helpers.SensorHandler;
import mobilsoft.icell.hu.seniti2.helpers.SensorObject;
import mobilsoft.icell.hu.seniti2.receivers.AlarmReceiver;
import mobilsoft.icell.hu.seniti2.services.BluetoothJobService;
import mobilsoft.icell.hu.seniti2.services.BluetoothService;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static mobilsoft.icell.hu.seniti2.helpers.SensorHandler.Listener;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.START_PARKING;
import static mobilsoft.icell.hu.seniti2.services.BluetoothService.STOP_SERVICE;

public class MainActivity extends AppCompatActivity implements BluetoothJobService.Listener {

    public static final String TAG = "Main";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    @BindView(R.id.current_x)
    TextView currentX;
    @BindView(R.id.current_y)
    TextView currentY;
    @BindView(R.id.current_z)
    TextView currentZ;
    @BindView(R.id.max_x)
    TextView maxX;
    @BindView(R.id.max_y)
    TextView maxY;
    @BindView(R.id.max_z)
    TextView maxZ;
    @BindView(R.id.gyro_x)
    TextView gX;
    @BindView(R.id.gyro_y)
    TextView gY;
    @BindView(R.id.gyro_z)
    TextView gZ;
    @BindView(R.id.reset_button)
    Button resetButton;
    @BindView(R.id.main_button)
    Button mainButton;
    @BindView(R.id.go_alarm)
    Button goAlarmButton;
    private Map<String, BluetoothDevice> deviceMap = new HashMap<>();
    private Intent serviceIntent;
    private Intent serviceIntent2;
    private SensorHandler sensorHandler;
    private int jobId = 0;
    private ComponentName serviceComponent;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private BluetoothJobService jobService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            if (BluetoothJobService.class.getClass().equals(name.getClass().getClass())) {
//                jobService = ((BluetoothJobService.ServiceBinder) service).getService();
//                jobService.setListener(MainActivity.this);
//            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            jobService.setListener(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        serviceIntent = new Intent(this, BluetoothService.class);
        serviceIntent2 = new Intent(this, BluetoothJobService.class);
        sensorHandler = SensorHandler.getInstance();
        sensorHandler.init(this);
        sensorHandler.addListener(
                new Listener() {
                    @Override
                    public void onMotionDetected(SensorEvent event, SensorObject sensorObject) {
                        setAcceleratorTextViews(sensorObject);
                        if (sensorObject.getDiff() > 0.5) {
                            setTextColors(RED);
                        } else {
                            setTextColors(BLUE);
                        }
                    }

                    @Override
                    public void onTiltDetected(SensorEvent event, SensorObject sensorObject) {
                        setGyroscopeTextViews(sensorObject);
                    }
                }
        );
        serviceComponent = new ComponentName(this, BluetoothJobService.class);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

    }

    private void setGyroscopeTextViews(SensorObject sensorObject) {
        gX.setText("" + sensorObject.getGyroX());
        gY.setText("" + sensorObject.getGyroY());
        gZ.setText("" + sensorObject.getGyroZ());
    }

    @OnClick(R.id.reset_button)
    public void resetMaxTextViews() {
        sensorHandler.resetMaxes();
    }

    @OnClick(R.id.go_alarm)
    public void onAlarmButtonPressed() {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, alarmIntent);
    }

    private void setAcceleratorTextViews(SensorObject sensorObject) {
        currentX.setText("" + (sensorObject.getX()));
        currentY.setText("" + (sensorObject.getY()));
        currentZ.setText("" + (sensorObject.getZ()));
        maxX.setText("" + (sensorObject.getMaxX()));
        maxY.setText("" + (sensorObject.getMaxY()));
        maxZ.setText("" + (sensorObject.getMaxZ()));
    }

    private void setTextColors(int color) {
        currentX.setTextColor(color);
        currentY.setTextColor(color);
        currentZ.setTextColor(color);
        maxX.setTextColor(color);
        maxY.setTextColor(color);
        maxZ.setTextColor(color);

    }

    @OnClick(R.id.main_button)
    public void startScan() {
        if (!isMyServiceRunning(BluetoothService.class)) {
            this.startService(serviceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent2, serviceConnection, BIND_AUTO_CREATE);
//        handleIncomingIntent(getIntent());
//        sensorHandler.start(BOTH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHandler.stop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        Bundle serviceBundle = intent.getExtras();
        int serviceStatus;
        if (serviceBundle != null) {
            serviceStatus = serviceBundle.getInt(BluetoothService.BLUETOOTH_SERVICE);
            if (serviceStatus != 0) {
                handleService(serviceStatus);
            }
        }
    }

    private void handleService(final int serviceStatusCode) {
        if (serviceStatusCode == STOP_SERVICE) {
            if (isMyServiceRunning(BluetoothService.class)) {
                stopService(serviceIntent);
            }
        } else if (serviceStatusCode == START_PARKING) {

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();

//        startService(new Intent(this, BluetoothJobService.class));
    }

    @OnClick(R.id.start_job)
    public void onStartJobPressed() {
        JobInfo.Builder builder = new JobInfo.Builder(jobId++, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setBackoffCriteria(5000L, JobInfo.BACKOFF_POLICY_LINEAR);
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(builder.build());
    }


    @Override
    public void sendData(String value) {
        gX.setText(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
