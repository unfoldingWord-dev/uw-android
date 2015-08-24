/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package peejweej.sideloading.wifiDirect;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import peejweej.sideloading.R;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    private ProgressDialog progressDialog = null;

    private Button findDevicesButton;
    private Button activateWifiButton;
    private Button connectButton;
    private Button disconnectButton;
    private Button startTransferButton;

    private TextView statusTextView;
    private TextView deviceInfoTextView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        setupViews();
        manageButtons();
        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        manageButtons();
    }

    private void setupViews(){

        findDevicesButton = (Button) mContentView.findViewById(R.id.btn_find_devices);
        findDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDevices();
            }
        });

        activateWifiButton = (Button) mContentView.findViewById(R.id.btn_activate_wifi);
        activateWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifi();
            }
        });

        statusTextView = (TextView) mContentView.findViewById(R.id.status_text);
        deviceInfoTextView = (TextView) mContentView.findViewById(R.id.device_info);

        connectButton = (Button) mContentView.findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect();
            }
        });

        disconnectButton  = (Button) mContentView.findViewById(R.id.btn_disconnect);
        disconnectButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        disconnect();
                    }
                });

        startTransferButton = (Button) mContentView.findViewById(R.id.btn_start_client);
        startTransferButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startTransfer();
                    }
                });
    }

    private void findDevices(){
        ((WiFiDirectActivity) getActivity()).startDiscovery();
    }

    private void openWifi(){
        ((WiFiDirectActivity) getActivity()).openWifiPreferences();
    }

    private void startTransfer(){
        ((WiFiDirectActivity) getActivity()).transferFile();
    }

    private void disconnect(){
        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
    }

    public void manageButtons(){

        boolean hasDevice = device != null;
        boolean hasConnected = info != null;

        findDevicesButton.setVisibility((!hasDevice &&!hasConnected && wifiIsOn())? View.VISIBLE : View.GONE);
        activateWifiButton.setVisibility((!wifiIsOn())? View.VISIBLE : View.GONE);

        connectButton.setVisibility((hasDevice && !hasConnected)? View.VISIBLE : View.GONE);
        disconnectButton.setVisibility((hasDevice && hasConnected)? View.VISIBLE : View.GONE);
        startTransferButton.setVisibility((hasDevice && hasConnected) ? View.VISIBLE : View.GONE);
    }

    private void connect(){

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
        );
        ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
    }

    public void transferFile(Uri uri){
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;

        // The owner IP is now known.
//        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
//        view.setText(getResources().getString(R.string.group_owner_text)
//                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
//                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        deviceInfoTextView.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), statusTextView).execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            startTransferButton.setVisibility(View.VISIBLE);
            statusTextView.setText(getResources().getString(R.string.client_text));
        }

        // hide the connect button
        manageButtons();
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(device.deviceAddress);
        deviceInfoTextView.setText(device.toString());
        manageButtons();
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
//        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(R.string.empty);
        deviceInfoTextView.setText(R.string.empty);
//        view = (TextView) mContentView.findViewById(R.id.group_owner);
//        view.setText(R.string.empty);
        statusTextView.setText(R.string.empty);
//        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
//        this.getView().setVisibility(View.GONE);
        manageButtons();
    }

    public void resetData(){
        device = null;
        info = null;
    }

    private boolean wifiIsOn() {
        return ((WiFiDirectActivity) getActivity()).isWifiP2pEnabled();
    }
}
