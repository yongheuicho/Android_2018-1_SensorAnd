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

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int BTH_ENABLE = 1010;
    protected String sBthName = "yhcho";
    protected BluetoothAdapter bthAdapter;
    protected BluetoothDevice bthDevice;
    protected BluetoothManager bthManager;
    protected BthReceiver bthReceiver;
    protected AceBluetoothSerialService bthService;
    protected Button btFind, btConnect, btRead, btWrite, btViewSensor0;
    protected EditText edWrite;
    protected TextView txRead;
    protected StringTok stSensorInput = new StringTok("");
    protected ArrayList<Double> arSensor0, arSensor1, arSensor2;

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
        btViewSensor0 = (Button) findViewById(R.id.btViewSensor0);

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
                connectBth();
            }
        });

        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = edWrite.getText().toString();
                bthService.println(str);
            }
        });

        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = bthService.getSerialInput();
                stSensorInput.appendString(str);
                parseSensor(stSensorInput);
            }
        });

        btViewSensor0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arSensor0.size() > 0) {
                    Double[] arDouble = new Double[arSensor0.size()];
                    arDouble = arSensor0.toArray(arDouble); // ArrayList -> array
                    String str = "";
                    for (double x : arDouble)
                        str += String.format("%g ", x);
                    showMsg(str);
                } else showMsg("arSensor0 is empty.");
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

        arSensor0 = new ArrayList<>();
        arSensor1 = new ArrayList<>();
        arSensor2 = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                repeatConnectBth(10, 100000);
            }
        }).start(); // Non-blocking execution
    }

    private void repeatConnectBth(int nRepeat, int millis) {
        for (int i = 0; i < nRepeat; i++) {
            connectBthWithMsg();
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (bthService.getState() == AceBluetoothSerialService.STATE_CONNECTED)
            {
                break;
            }
            else {

            }
        }

    }

    private void connectBthWithMsg() {
        if (!bthReceiver.sAddress.isEmpty()) {
            bthDevice = bthAdapter.getRemoteDevice(bthReceiver.sAddress);
            bthService.connect(bthDevice);
        }
    }

    private void connectBth() {
        if (bthReceiver.sAddress.isEmpty()) {
            showMsg("MAC address is empty.");
        } else {
            bthDevice = bthAdapter.getRemoteDevice(bthReceiver.sAddress);
            bthService.connect(bthDevice);
            showMsg(sBthName + " is connected.");
        }
    }

    private void parseSensor(StringTok stSensorInput) {
        while (stSensorInput.hasLine()) {
            String sLine = stSensorInput.cutLine();
            parseSensorLine(sLine);
        }
    }

    private void parseSensorLine(String sLine) {
        StringTok stInput = new StringTok(sLine);
        StringTok stToken = stInput.getToken(); // Sensor keyword
        if (stToken.toString().equals("getsen")) {
            stToken = stInput.getToken();   // Sensor #
            long nSensor = stToken.toLong();
            stToken = stInput.getToken();   // Sensor value
            double sensorVal = stToken.toDouble();
            showMsg(String.format("%d: %g", nSensor, sensorVal));
            saveSensorVal(nSensor, sensorVal);
        }
    }

    private void saveSensorVal(long nSensor, double sensorVal) {
        if (nSensor == 0) arSensor0.add(sensorVal);
        else if (nSensor == 1) arSensor1.add(sensorVal);
        else if (nSensor == 2) arSensor2.add(sensorVal);
    }

    @Override
    protected void onDestroy() {
        if (bthAdapter.isDiscovering()) bthAdapter.cancelDiscovery();
        unregisterReceiver(bthReceiver);
        super.onDestroy();
    }
}
