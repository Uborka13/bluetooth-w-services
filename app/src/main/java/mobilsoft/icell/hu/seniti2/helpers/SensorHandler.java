package mobilsoft.icell.hu.seniti2.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static mobilsoft.icell.hu.seniti2.helpers.SensorHandler.UsedSensors.BOTH;
import static mobilsoft.icell.hu.seniti2.helpers.SensorHandler.UsedSensors.GYROSCOPE;
import static mobilsoft.icell.hu.seniti2.helpers.SensorHandler.UsedSensors.ACCELEROMETER;

public class SensorHandler implements SensorEventListener {

    private static final String TAG = SensorHandler.class.getSimpleName();
    private static SensorHandler instance;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private List<Listener> listeners = new ArrayList<>();
    private SensorObject currentObject = new SensorObject();
    private Timer timer = new Timer();
    private boolean shouldCollectAcceleroValue = false;
    private boolean shouldCollectGyroValue = false;
    private TimerTask acceleroSamplingTask;
    private TimerTask gyroSamplingTask;

    private int frequency = 1000;


    public SensorHandler() {
        Log.d(TAG, "sensor <-> constructor");
        // Empty
    }

    public static SensorHandler getInstance() {
        if (instance == null) {
            instance = new SensorHandler();
        }
        return instance;
    }

    public void init(Context context) {
        Log.d(TAG, "sensor <-> init");
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        currentObject.setStandardGravity(SensorManager.STANDARD_GRAVITY);
    }

    public void start(UsedSensors sensors) {
        if (sensors == BOTH) {
            startAccelero();
            startGyro();
            acceleroSamplingTask = createAcceleroSamplerTask();
            gyroSamplingTask = createGyroSamplerTask();
            timer.schedule(acceleroSamplingTask, 0, frequency);
            timer.schedule(gyroSamplingTask, 0, frequency);
        } else if (sensors == ACCELEROMETER) {
            startAccelero();
            acceleroSamplingTask = createAcceleroSamplerTask();
            timer.schedule(acceleroSamplingTask, 0, frequency);
        } else if (sensors == GYROSCOPE) {
            startGyro();
            gyroSamplingTask = createGyroSamplerTask();
            timer.schedule(gyroSamplingTask, 0, frequency);
        }

    }

    private void startAccelero() {
        Log.d(TAG, "sensor <-> startAccelero");
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        Log.d(TAG, "sensor <-> stop");
        sensorManager.unregisterListener(this);
    }

    private void startGyro() {
        Log.d(TAG, "sensor <-> startGyro");
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void resetMaxes() {
        currentObject.resetMax();
    }

    public void addListener(Listener listener) {
        Log.d(TAG, "sensor <-> listener added");
        listeners.add(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (shouldCollectAcceleroValue) {
                shouldCollectAcceleroValue = false;
                currentObject.setX(event.values[0]);
                currentObject.setY(event.values[1]);
                currentObject.setZ(event.values[2]);

                for (Listener listener : listeners) {
                    listener.onMotionDetected(event, currentObject);
                }
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (shouldCollectGyroValue) {
                shouldCollectGyroValue = false;
                float alpha = 0.7f;
                currentObject.setGyroX(alpha * event.values[0]);
                currentObject.setGyroY(alpha * event.values[1]);
                currentObject.setGyroZ(alpha * event.values[2]);


                for (Listener listener : listeners) {
                    listener.onTiltDetected(event, currentObject);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "sensor <-> accuracy changed --> " + accuracy);
    }

    private TimerTask createAcceleroSamplerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                shouldCollectAcceleroValue = true;
            }
        };
    }

    private TimerTask createGyroSamplerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                shouldCollectGyroValue = true;
            }
        };
    }

    public enum UsedSensors {
        ACCELEROMETER(2),
        GYROSCOPE(1),
        BOTH(0);
        private int value;

        UsedSensors(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public interface Listener {
        void onMotionDetected(SensorEvent event, SensorObject sensorObject);

        void onTiltDetected(SensorEvent event, SensorObject sensorObject);
    }
}
