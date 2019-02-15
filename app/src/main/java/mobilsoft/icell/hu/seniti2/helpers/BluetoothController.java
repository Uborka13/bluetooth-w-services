package mobilsoft.icell.hu.seniti2.helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_POWER;

public class BluetoothController {

    //9c5746bb-5aca-4b74-8670-11f2080694c8
    //59e00fe4-520f-4985-909f-43ae9b25062f
    //00005eba-0000-1000-8000-00805f9b34fb

    private static final String TAG = BluetoothController.class.getSimpleName();
    private final Context context;
    private final BluetoothListener listener;
    private final BluetoothAdapter adapter;
    private final String serviceUuid;
    private boolean scanning = false;
    private BluetoothLeScanner ble;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            listener.found(result);
        }
    };

    public BluetoothController(Context context, BluetoothListener listener, String serviceUuid) {
        this.context = context;
        this.listener = listener;
        this.serviceUuid = serviceUuid;
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
    }

    public void startLeScanning(int scanTime) {
        if (adapter != null) {
            ble = adapter.getBluetoothLeScanner();
            if (adapter.isEnabled() && ble != null) {
                List<ScanFilter> filters = new ArrayList<>();
                ScanFilter.Builder builder = new ScanFilter.Builder();
                ScanFilter filter = builder.setServiceUuid(ParcelUuid.fromString(serviceUuid)).build();
                ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
                ScanSettings settings = settingBuilder.setScanMode(SCAN_MODE_LOW_POWER).build();
                filters.add(filter);
                ble.startScan(filters, settings, scanCallback);
                setScanning(true);
                final Handler handler = new Handler();
                handler.postDelayed(this::stopLeScanning, scanTime * 1000);
            } else {
                listener.showDialog(context, "Bluetooth engedélyezése szükséges");
            }
        } else {
            listener.showDialog(context, "Bluetooth adapter nem található");
        }

    }

    public void stopLeScanning() {
        if (ble != null && isScanning()) {
            ble.stopScan(scanCallback);
            setScanning(false);
            listener.finished();
        }
    }

    public boolean isScanning() {
        return scanning;
    }

    private void setScanning(boolean scanning) {
        this.scanning = scanning;
    }

    public interface BluetoothListener {

        void showDialog(Context context, String message);

        void found(ScanResult device);

        void finished();
    }
}
