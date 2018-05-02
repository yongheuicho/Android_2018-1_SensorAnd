package kr.ac.mokwon.ice.sensorand;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    protected BluetoothAdapter bthAdapter;
    protected BluetoothManager bthManager;
    protected Button btFind, btConnect, btRead, btWrite;
    protected EditText edWrite;
    protected TextView txRead;

    protected void showMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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

            showMsg("Bluetooth is not enabled.");
        } else showMsg("Bluetooth is enabled.");

        btFind = (Button) findViewById(R.id.btFind);
        btConnect = (Button) findViewById(R.id.btConnect);
        btRead = (Button) findViewById(R.id.btRead);
        btWrite = (Button) findViewById(R.id.btWrite);
        edWrite = (EditText) findViewById(R.id.edWrite);
        txRead = (TextView) findViewById(R.id.txRead);
    }
}
