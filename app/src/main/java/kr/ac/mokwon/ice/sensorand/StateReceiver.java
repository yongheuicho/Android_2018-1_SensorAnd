package kr.ac.mokwon.ice.sensorand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StateReceiver extends BroadcastReceiver {
    protected MainActivity mainActivity;

    StateReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //       throw new UnsupportedOperationException("Not yet implemented");
        String sAction = intent.getAction();
        if (sAction.equals(MainActivity.BTH_CONN_OK)) {
            mainActivity.setStateText("Bluetooth 연결 성공");
        }
        else if (sAction.equals(MainActivity.BTH_CONN_FAIL)) {
            int nCountFail = intent.getIntExtra(MainActivity.BTH_CONN_FAIL_COUNT, 0);
            String sCount = String.format("%d회 시도중...", nCountFail);
            mainActivity.setStateText("Bluetooth 연결 실패: " + sCount);
        }
    }
}
