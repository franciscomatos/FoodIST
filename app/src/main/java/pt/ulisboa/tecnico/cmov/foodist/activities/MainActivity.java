package pt.ulisboa.tecnico.cmov.foodist.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.wifi.WifiManager;
import java.util.Date;
import 	android.net.NetworkInfo;
import android.net.ConnectivityManager;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.fetch.registerUser;
import pt.ulisboa.tecnico.cmov.foodist.fetch.toggleQueue;
import pt.ulisboa.tecnico.cmov.foodist.receivers.WifiBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;
import pt.ulisboa.tecnico.cmov.foodist.fetch.prefetch;

public class MainActivity extends Activity implements SimWifiP2pManager.PeerListListener {


    public static final String TAG = "msgsender";

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private WifiBroadcastReceiver mReceiver;
    private GlobalClass global;

    private Handler handler = new Handler();
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };



    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureFoodListButton();

        this.global = (GlobalClass) getApplicationContext();
        global.setLocationManager( (LocationManager) getSystemService(Context.LOCATION_SERVICE));
        global.getLocation2(MainActivity.this);

        startWifi();

        registerUser(global);
        global.setConnected(isOnline());
    }

    private void registerUser(GlobalClass global) {
        registerUser registry = new registerUser(global);
        registry.execute();
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
    public void setConnected(boolean val){
        global.setConnected(val);
    }

    public void prefetch() {
            prefetch process = new prefetch(global);
            process.execute();
    }

    private void startWifi() {
        makeToast("Service started");
        //SimWifiP2pSocketManager.Init(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mReceiver = new WifiBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        //WIFI ON
        Intent intent = new Intent(MainActivity.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
        Log.i("WIFI", "wifi on");

    }

    public void makeToast(String text) {
        handler.post(new MainActivity.ToastRunnable(text));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GlobalClass global = (GlobalClass) getApplicationContext();
                    global.getLocation2(MainActivity.this);
                }
        }
    }


    private void configureFoodListButton() {
        Button foodListButton = (Button) findViewById(R.id.foodServicesButton);
        foodListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, ListFoodServicesActivity.class));
            }
        });
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
/*
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
            Log.i("Devices:","" + device.deviceName + " (" + device.getVirtIp() + ")\n");

        }

        makeToast("in queue");

        // display list of devices in range
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
*/
        //GlobalClass global = (GlobalClass) getApplicationContext();

        //DateFormat presDateFormat = new SimpleDateFormat("HH:mm");
        Date current = new Date();   // given date

        String currentTime = String.valueOf(current.getTime()/6000);

        //String currentTime = presDateFormat.format(current);
        Log.i("Time", currentTime);
        boolean inAQueue = false;
        for (SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()) {
            Log.i("Devices:", device.deviceName);
            if(global.isFoodService(device.deviceName) && global.getCurrentFoodService() == null ) {
                Log.i("Devices:", device.deviceName + " is a food service.");
                toggleQueue toggle = new toggleQueue(global, device.deviceName, currentTime);
                toggle.execute();
                global.setCurrentFoodService(device.deviceName);
                inAQueue = true;
            }
        }
        if (!inAQueue) { //left queue
            if (global.getCurrentFoodService() != null) {
                toggleQueue toggle = new toggleQueue(global, global.getCurrentFoodService().getName(), currentTime);
                toggle.execute();
                global.setCurrentFoodService("");
            }

        }

    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unbindService(mConnection);
        Log.i("WIFI", "destroyed");
        super.onDestroy();
    }

    public void logQueue () {
        Log.i ("Peers:", "got here");
        mManager.requestPeers(mChannel, MainActivity.this);
    }

}
