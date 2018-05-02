package kr.ac.mokwon.ice.sensorand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int BTH_ENABLE = 1010;
    protected String sBthName = "yhcho";
    protected BluetoothAdapter bthAdapter;
    protected BluetoothDevice bthDevice;
    protected BluetoothManager bthManager;
    protected BthReceiver bthReceiver;
    protected AceBluetoothSerialService bthService;
    protected Button btFind, btConnect, btRead, btWrite;
    protected EditText edWrite;
    protected TextView txRead;

    protected void showMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BTH_ENABLE) {
            if (resultCode == RESULT_OK)
                showMsg("Bluetooth is enabled by a user.");
            else showMsg("Bluetooth is disable by a user.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bthManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        if (bthManager == null) return;
        showMsg("BluetoothManager is found.");
        bthAdapter = bthManager.getAdapter();
        if (bthAdapter == null) return;
        showMsg("BluetoothAdapter is found.");
        if (!bthAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BTH_ENABLE);
            showMsg("Bluetooth is not enabled.");
        } else showMsg("Bluetooth is enabled.");

        btFind = (Button) findViewById(R.id.btFind);
        btConnect = (Button) findViewById(R.id.btConnect);
        btRead = (Button) findViewById(R.id.btRead);
        btWrite = (Button) findViewById(R.id.btWrite);
        edWrite = (EditText) findViewById(R.id.edWrite);
        txRead = (TextView) findViewById(R.id.txRead);

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bthAdapter.isDiscovering()) bthAdapter.cancelDiscovery();
                bthAdapter.startDiscovery();
                showMsg("Discovering...");
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bthReceiver.sAddress.isEmpty()) {
                    showMsg("MAC address is empty.");
                } else {
                    bthDevice = bthAdapter.getRemoteDevice(bthReceiver.sAddress);
                    bthService.connect(bthDevice);
                    showMsg(sBthName + " is connected.");
                }
            }
        });

        bthReceiver = new BthReceiver(sBthName);
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bthReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bthReceiver, intentFilter);

        Set<BluetoothDevice> setDevice = bthAdapter.getBondedDevices();
        if (setDevice != null) {
            for (BluetoothDevice device : setDevice) {
                if (device.getName().equalsIgnoreCase(sBthName)) {
                    bthReceiver.sAddress = device.getAddress();
                    showMsg("MAC address of " + sBthName + "is set.");
                }
            }
        }

        bthService = new AceBluetoothSerialService(this, bthAdapter);
    }

    @Override
    protected void onDestroy() {
        if (bthAdapter.isDiscovering()) bthAdapter.cancelDiscovery();
        unregisterReceiver(bthReceiver);
        super.onDestroy();
    }
}
