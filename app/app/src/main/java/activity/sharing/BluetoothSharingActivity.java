package activity.sharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import org.unfoldingword.mobile.R;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BluetoothSharingActivity extends ActionBarActivity {

    private static final String TAG = "BluetoothSharingAct";
    public static final String TEXT_PARAM = "TEXT_PARAM";

    private BluetoothSPP bluetooth;

    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_sharing);

        text = getIntent().getStringExtra(TEXT_PARAM);
        bluetooth = new BluetoothSPP(getApplicationContext());

        bluetooth.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String s, String s1) {
                Log.i(TAG, "device Connected");
            }

            @Override
            public void onDeviceDisconnected() {
                Log.i(TAG, "device disconnected");
            }

            @Override
            public void onDeviceConnectionFailed() {
                Log.i(TAG, "device failed connection");
            }
        });

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i(TAG, "data: " + message);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!bluetooth.isBluetoothEnabled()) {

            new AlertDialog.Builder(this)
                    .setTitle("Bluetooth")
                    .setMessage("Enable Bluetooth?")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            bluetooth.enable();
                            startBluetoothService();
                        }
                    })
                    .setNegativeButton("false", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();

        } else if(!bluetooth.isAutoConnecting()){
            startBluetoothService();
        }
    }

    private void startBluetoothService(){

        bluetooth.setupService();
        bluetooth.startService(BluetoothState.DEVICE_ANDROID);

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {

                String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_ANDROID);
                bluetooth.connect(address);
//                bluetooth.send(text, true);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_ANDROID);
                startBluetoothService();
            } else {
                finish();
            }
        }
    }

}
