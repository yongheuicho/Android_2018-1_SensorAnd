package kr.ac.mokwon.ice.sensorand;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BthReceiver extends BroadcastReceiver {
    public String sName, sAddress;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device;
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getName().equalsIgnoreCase(sName)) {
                sAddress = device.getAddress();
                Toast.makeText(context, sName + "is found: " + sAddress, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
