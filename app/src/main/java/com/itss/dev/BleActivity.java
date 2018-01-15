package com.itss.dev;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BleActivity extends AppCompatActivity{

    public static final long SCAN_PERIOD = 7000;
    private final String TAG = "BleActivity";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    public static final int REQUEST_BLE_CODE = 10888;
    public static final int REQUEST_LOCATION_CODE = 20888;

    protected BluetoothGatt gatt;
    protected DeviceItem selectedDeviceItem;
    protected Callback scanCallback;

    protected LinearLayout devicesList;
    protected Button scanBut;
    protected Button sendBut;
    protected Button connectBut;
    protected Button disconnectBut;
    protected ProgressBar scanningBar;
    protected TextView selectedDeviceLabel;
    Map<String, DeviceItem> devicesMap = new HashMap<>();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    protected void turnOnBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "Bluetooth LE dose not support");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLE_CODE);
        } else {
            scanCallback = new Callback();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    protected void scanBLEDevice(boolean enable) {
       /* if (device != null) {
            return;
        }
*/
        if (enable) {
            devicesMap.clear();
            if (devicesList != null) {
                devicesList.removeAllViews();
            }
        }

        if (enable) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothLeScanner.stopScan(scanCallback);
                    scanUiUpdate(false);
                }
            }, SCAN_PERIOD);

            bluetoothLeScanner.startScan(scanCallback);
        } else {
            bluetoothLeScanner.stopScan(scanCallback);
        }
        scanUiUpdate(enable);
    }

    private void scanUiUpdate(boolean scanning) {
        if (scanBut != null) scanBut.setVisibility(scanning ? View.GONE : View.VISIBLE);
        if (sendBut != null) sendBut.setVisibility(scanning ? View.GONE : View.VISIBLE);
        if (connectBut != null) connectBut.setVisibility(scanning ? View.GONE : View.VISIBLE);
        if (scanningBar != null) scanningBar.setVisibility(!scanning ? View.GONE : View.VISIBLE);
    }

    public class Callback extends ScanCallback {

        private static final String TAG = "Callback";


        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            if (!foundDevice && name != null && name.trim().equals("ITSS")) {
//                foundDevice = true;
//                scanBLEDevice(false);
//                Log.d(TAG, "Got device " + name);
//                device = result.getDevice();
//                connectGatt(device);
//            }
            BluetoothDevice device = result.getDevice();
            DeviceItem deviceItem = new DeviceItem(device);
            if (devicesMap.get(deviceItem.name) == null) {
                devicesMap.put(deviceItem.name, deviceItem);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, 8, 0, 8);

                devicesList.addView(deviceItem.lable, layoutParams);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.d(TAG, sr.getDevice().getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BLE scna faild with err code " + errorCode);
        }

    }

    public class DeviceItem {
        TextView lable;
        String name;
        BluetoothDevice device;

        DeviceItem(final BluetoothDevice device) {
            this.device = device;
            lable = new TextView(getApplicationContext());
            if (device.getName() != null) {
                name = device.getName();
            } else {
                name = device.getAddress();
            }
            lable.setText(name);
            lable.setTextSize(20);
            lable.setTextColor(getResources().getColor(R.color.colorPrimary));

            lable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedDeviceLabel != null) ;
                    selectedDeviceLabel.setText(name);
                    selectedDeviceItem = DeviceItem.this;
                }
            });

        }
    }


    public enum State {
        CONNECTED, DISCONNECTED, CONNECTING
    }

    public interface BleStateHolder {
        void updateState(State state);
    }

}

