package com.itss.dev.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.itss.dev.BleActivity;
import com.itss.dev.BleActivity.BleStateHolder;

import java.util.UUID;

import static com.itss.dev.BleActivity.State.CONNECTED;
import static com.itss.dev.BleActivity.State.DISCONNECTED;

public class MyGattCallback extends BluetoothGattCallback {

    private static final String TAG = "MyGattCallback";
    BleStateHolder stateHolder;

    public MyGattCallback(BleStateHolder stateHolder) {
        this.stateHolder = stateHolder;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            stateHolder.updateState(CONNECTED);
            Log.i(TAG, "Connected to GATT server.");
            Log.i(TAG, "Attempting to start service discovery:" +
                    gatt.discoverServices());


        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            stateHolder.updateState(DISCONNECTED);
            Log.i(TAG, "Disconnected from GATT server.");
        }
    }

    @Override
// New services discovered
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      /*  if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService service = gatt.getService(UUID.fromString(MESSAGE_SERVICE_NAME));
            BluetoothGattCharacteristic cha = service.getCharacteristic(UUID.fromString(MESSAGE_CHAR_NAME));
            if ((cha.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0) {
                send(cha, "Fuc");
            }
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }*/
        Log.w(TAG, "onServicesDiscovered received: " + status);
    }

    @Override
// Result of a characteristic read operation
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Characteristic " + characteristic.getUuid() + " was read");
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic,
                                      int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Write characteristic " + characteristic.getUuid() + " successfull");
        }
    }

    public void send(BluetoothGattCharacteristic charac, byte[] data) {
/*
        long beginMillis = System.currentTimeMillis();
        if (charac == null || data == null || data.length == 0) {
            return;
        }
        charac.setValue(data);
        gatt.writeCharacteristic(charac);
        while (writeInProgress) {
            if (System.currentTimeMillis() - beginMillis > SEND_TIME_OUT_MILLIS) {
                break;
            }
        }
*/

    }

    public void send(BluetoothGattCharacteristic charac,
                     String string) {
        int len = string.length();
        int pos = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (len != 0) {
            stringBuilder.setLength(0);
            if (len >= 20) {
                stringBuilder.append(string.toCharArray(), pos, 20);
                len -= 20;
                pos += 20;
            } else {
                stringBuilder.append(string.toCharArray(), pos, len);
                len = 0;
            }
            send(charac, stringBuilder.toString().getBytes());
        }
    }
};

