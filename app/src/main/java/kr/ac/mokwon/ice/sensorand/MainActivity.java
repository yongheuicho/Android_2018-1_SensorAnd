package kr.ac.mokwon.ice.sensorand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    protected BluetoothAdapter bthAdapter;
    protected BluetoothManager bthManager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bthManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (bthManager == null) return;
        bthAdapter = bthManager.getAdapter();
        if (bthAdapter == null) return;
        if (!bthAdapter.isEnabled()) {

        }
    }
}
