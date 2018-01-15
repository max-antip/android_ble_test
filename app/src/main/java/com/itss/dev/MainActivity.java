package com.itss.dev;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.itss.dev.ble.Constants;
import com.itss.dev.ble.MyGattCallback;

import java.util.UUID;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.itss.dev.BleActivity.State.CONNECTED;
import static com.itss.dev.BleActivity.State.DISCONNECTED;
import static com.itss.dev.ble.Constants.DATA_CHARACTERISTIC;
import static com.itss.dev.ble.Constants.DATA_SERVICE;

public class MainActivity extends BleActivity implements BleActivity.BleStateHolder {


    private static final String TAG = "MainActivity";

    private State state = DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBut = findViewById(R.id.send_mess);
        scanBut = findViewById(R.id.scan_but);
        devicesList = findViewById(R.id.ble_devices_list);
        scanningBar = findViewById(R.id.scanning_bar);
        selectedDeviceLabel = findViewById(R.id.selected_device);
        connectBut = findViewById(R.id.connect);
        disconnectBut = findViewById(R.id.disconnect);
        scanBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBLEDevice(true);
            }
        });

        sendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothGattService service = gatt.getService(UUID.fromString(DATA_SERVICE));
                if (service == null) return;
                BluetoothGattCharacteristic cha = service.getCharacteristic(UUID.fromString(DATA_CHARACTERISTIC));
                if ((cha.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                    cha.setValue("999");
                    gatt.writeCharacteristic(cha);
                }
            }
        });

        connectBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDeviceItem != null) {
                    boolean connected = connectGatt(selectedDeviceItem.device);
                    Log.i(TAG, "GATT connected is : " + connected);
                }
            }
        });

        turnOnBLE();
    }

    public boolean connectGatt(BluetoothDevice device) {
        gatt = device.connectGatt(getApplicationContext(), true, new MyGattCallback(this));
        return gatt.connect();
    }

    public void disconnectGatt() {
        if (gatt != null)
            gatt.disconnect();
    }

    @Override
    public void updateState(final State state) {
        this.state = state;
        runOnUiThread(new Runnable() {
            public void run() {
                if (state == CONNECTED) {
                    disconnectBut.setVisibility(VISIBLE);
                } else {
                    disconnectBut.setVisibility(GONE);
                }
            }
        });

    }
}
