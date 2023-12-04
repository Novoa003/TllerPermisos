package co.edu.ue.uniempresarail2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

public class MainActivity extends AppCompatActivity {

   private Context context;
   private Activity activity;

   private TextView verndroid;

   private int versdk;

   private ProgressBar pbLevelBaterry;
   private TextView tvLevelBaterry;
   IntentFilter batteryFilter;

   private TextView tvConexion;
   ConnectivityManager conexion;

   CameraManager cameraManager;
   String cameraId;

   private EditText nameFile;

   private Button onFlash;
   private Button offFlash;
   private Button btnSaveFile;
   private Button onBT;
   private Button offBT;
   private BluetoothAdapter bluetoothAdapter;
   private static final int REQUEST_ENABLE_BT = 1;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       this.context = getApplicationContext();
       this.activity = this;
       setContentView(R.layout.activity_main);
       ObjInit();
       onFlash.setOnClickListener(this::Onligth);
       offFlash.setOnClickListener(this::OffLigth);
       batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
       registerReceiver(broReceiber,batteryFilter);
       onBT.setOnClickListener(this::enableBluetooth);
       offBT.setOnClickListener(this::disableBluetooth);
       btnSaveFile.setOnClickListener(this::saveFile);
   }

   //Bateria
   BroadcastReceiver broReceiber = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
           pbLevelBaterry.setProgress(levelBattery);
           tvLevelBaterry.setText("Nivel de bateria"+levelBattery+ "%");
       }
   };

   // Conexion
   private void chekConnection() {
       try {
           conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           if (conexion != null) {
               NetworkInfo networkInfo = conexion.getActiveNetworkInfo();
               boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
               if (stateNet) tvConexion.setText("On");
               else tvConexion.setText("Off");
           }else{
               tvConexion.setText("No info");
           }
       }catch (Exception e){
           Log.i("Con",e.getMessage());
       }
   }

   //Version android
   @Override
   protected void onResume() {
       super.onResume();
       String versionSO= Build.VERSION.RELEASE;
       versdk = Build.VERSION.SDK_INT;
       verndroid.setText("Version SO:"+versionSO+"/ SDK:"+versdk);
       chekConnection();
   }

   private void OffLigth(View view) {
       try {
           cameraManager.setTorchMode(cameraId,false);
       } catch (CameraAccessException e) {
           throw new RuntimeException("En la literna"+e);
       }
   }

   //flash light
   private void Onligth(View view) {
       try {
           cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
           cameraId = cameraManager.getCameraIdList()[0];
           cameraManager.setTorchMode(cameraId,true);
       } catch (CameraAccessException e) {
           throw new RuntimeException(e);
       }
   }
    private void checkAndToggleBluetooth(boolean enable) {
        checkBluetoothPermission();

        if (bluetoothAdapter == null) {
            return;
        }

        if (enable && !bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        } else if (!enable && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
            }
            return;
        }

        initializeBluetoothAdapter();
    }

    private void initializeBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT >= 31) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    private void enableBluetooth(View view) {
        checkAndToggleBluetooth(true);
    }

    private void disableBluetooth(View view) {
        checkAndToggleBluetooth(false);
    }
   //Linterna
   //linterna Off
   public void saveFile (View view){
       String name = nameFile.getText().toString() + ".txt";
       String dateBattery = tvLevelBaterry.getText().toString();
       Clfile clfile = new Clfile(context, this);
       clfile.saveFile(name, dateBattery);
   }
    private  void ObjInit(){
        this.verndroid= findViewById(R.id.tvVersionAndroid) ;
        this.pbLevelBaterry = findViewById(R.id.pbLevelBaterry);
        this.tvLevelBaterry= findViewById(R.id.tvLevelBaterryLB);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.onFlash =findViewById( R.id.btnOn);
        this.offFlash = findViewById(R.id.btnOff2);
        this.nameFile = findViewById(R.id.etNameFile);
        this.btnSaveFile = findViewById(R.id.btnSaveFile);
        this.onBT = findViewById(R.id.btnBTON);
        this.offBT = findViewById(R.id.btnBTOFF);
    }

}