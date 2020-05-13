package pt.ulisboa.tecnico.cmov.foodist.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Date;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.ulisboa.tecnico.cmov.foodist.InputValidation;
import pt.ulisboa.tecnico.cmov.foodist.R;
import pt.ulisboa.tecnico.cmov.foodist.domain.User;
import pt.ulisboa.tecnico.cmov.foodist.fetch.prefetch;
import pt.ulisboa.tecnico.cmov.foodist.fetch.registerUser;
import pt.ulisboa.tecnico.cmov.foodist.fetch.toggleQueue;
import pt.ulisboa.tecnico.cmov.foodist.fetch.login;
import pt.ulisboa.tecnico.cmov.foodist.receivers.WifiBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.foodist.states.AnnotationStatus;
import pt.ulisboa.tecnico.cmov.foodist.states.GlobalClass;

public class LoginActivity extends Activity implements SimWifiP2pManager.PeerListListener {

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
        setContentView(R.layout.activity_login);

        Button createAccountButton = findViewById(R.id.createAccount);
        Button continueAsGuestButton = findViewById(R.id.continueAsGuestButton);
        Button loginButton = findViewById(R.id.login);

        final EditText nameText = findViewById(R.id.username);
        final EditText passwordText = findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                InputValidation inputValidatorHelper = new InputValidation();
                StringBuilder errMsg = new StringBuilder("Unable to save. Please fix the following errors and try again.\n");
                boolean allowSave = true;

                String username = nameText.getText().toString();
                String password = passwordText.getText().toString();

                if(inputValidatorHelper.isNullOrEmpty(username)) {
                    errMsg.append("- Username name cannot be empty.\n");
                    allowSave = false;
                }
                if(inputValidatorHelper.isNullOrEmpty(password) || !inputValidatorHelper.isValidPassword(password, true)) {
                    errMsg.append("- Invalid password.\n");
                    allowSave = false;
                }

                if(!allowSave) {
                    Toast.makeText(getApplicationContext(), errMsg.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // TO DO: login in server and then update user in global class
                login login = new login(global, username, password);
                login.execute();

                Intent listFoodServicesIntent =  new Intent(LoginActivity.this, ListFoodServicesActivity.class);
                startActivity(listFoodServicesIntent);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent createAccountIntent =  new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(createAccountIntent);
            }
        });

        continueAsGuestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ListFoodServicesActivity.class));
            }
        });
        GlobalClass global = (GlobalClass) getApplicationContext();
        this.global = (GlobalClass) getApplicationContext();
        global.setContext(this);
        global.setLocationManager( (LocationManager) getSystemService(Context.LOCATION_SERVICE));
        global.setLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LOCATION", String.valueOf(location.getLatitude()) );
                global.setLatitude(location.getLatitude());
                global.setLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("LOCATION", "oh no1");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("LOCATION", "oh no2");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("LOCATION", "oh no3");
            }
        });
        global.getLocation2(LoginActivity.this);
        global.setStatus("STUDENT"); //FIXME change this to user preference
        startWifi();

        global.setConnected(isOnline());
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
        Intent intent = new Intent(LoginActivity.this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
        Log.i("WIFI", "wifi on");

    }

    public void makeToast(String text) {
        handler.post(new LoginActivity.ToastRunnable(text));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GlobalClass global = (GlobalClass) getApplicationContext();
                    global.getLocation2(LoginActivity.this);
                }
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {

        makeToast("in queue");
        /*
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

        Date current = new Date();   // given date

        String currentTime = String.valueOf(current.getTime()/6000);

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
        mManager.requestPeers(mChannel, LoginActivity.this);
    }


   /* public class Connection extends AsyncTask<Void, Void, Void> {

        private String URL;
        private String data = "";
        private GlobalClass global;

        @Override
        protected Void doInBackground(Void... voids){
           connect()

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("RESPONSE:", "BOOOOOM!");
        }


        private void connect(){
            CertificateFactory cf = null;
            try{
                cf = CertificateFactory.getInstance("X.509");
            }catch(CertificateException e){
                e.printStackTrace();
            }

            try{
                httpsURLConnection
            }


        }
    }*/
}
