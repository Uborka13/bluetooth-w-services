package mobilsoft.icell.hu.seniti2.receivers;

import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mobilsoft.icell.hu.seniti2.controller.NetworkController;
import mobilsoft.icell.hu.seniti2.controller.dao.EventRequest;
import mobilsoft.icell.hu.seniti2.controller.dao.EventsItem;
import mobilsoft.icell.hu.seniti2.helpers.BluetoothController;
import mobilsoft.icell.hu.seniti2.persistence.entity.Event;
import mobilsoft.icell.hu.seniti2.utils.DeviceInfoUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmReceiver extends BroadcastReceiver implements BluetoothController.BluetoothListener {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private BluetoothController bluetoothController;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.i(TAG, "Alarm Receiver received -->AlarmReceiver<--");
        if (bluetoothController == null) {
            bluetoothController = new BluetoothController(this.context, this, "00005eba-0000-1000-8000-00805f9b34fb");
        }
        if (!bluetoothController.isScanning()) {
            bluetoothController.startLeScanning(30);
        }
//        EventRequest eventRequest = new EventRequest();
//        List<EventsItem> eventsItemsList = new ArrayList<>();
//        eventsItemsList.add(new EventsItem().setEventEntity(new Event("Alarm Receiver received", System.currentTimeMillis(), this.getClass(), this.context)));
//        eventRequest.setDevicename(DeviceInfoUtils.getDeviceName());
//        eventRequest.setOsversion(Build.VERSION.SDK_INT + " - " + Build.VERSION.RELEASE);
//        eventRequest.setEvents(eventsItemsList);
//        NetworkController.getInstance().sendRequest(eventRequest).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Log.i(TAG, "Alarm Receiver response -->AlarmReceiver<--");
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    public void showDialog(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void found(ScanResult device) {
        Log.i(TAG, "Alarm Receiver device found -->AlarmReceiver<--");
    }

    @Override
    public void finished() {
        Log.i(TAG, "Alarm Receiver scan finished -->AlarmReceiver<--");
//        EventRequest eventRequest = new EventRequest();
//        List<EventsItem> eventsItemsList = new ArrayList<>();
//        eventsItemsList.add(new EventsItem().setEventEntity(new Event("Alarm Receiver finished scan", System.currentTimeMillis(), this.getClass(), this.context)));
//        eventRequest.setDevicename(DeviceInfoUtils.getDeviceName());
//        eventRequest.setOsversion(Build.VERSION.SDK_INT + " - " + Build.VERSION.RELEASE);
//        eventRequest.setEvents(eventsItemsList);
//        NetworkController.getInstance().sendRequest(eventRequest).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Log.i(TAG, "Alarm Receiver response -->AlarmReceiver<--");
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//
//            }
//        });
    }
}
