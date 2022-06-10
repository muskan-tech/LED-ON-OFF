package com.example.lasttry;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button  On, ld ;
    EditText hour,min,sec,hour2,min2,sec2;
    boolean connectionsuccessful=false;
    boolean timerrunning=false;
    boolean greater=false;
    BluetoothAdapter bluetoothAdapter;
    ListView list;
    TextView text;
    Set<BluetoothDevice> paireddevices;
    ArrayList al = new ArrayList();
    BluetoothDevice[] btarray;
    ConnectThread c;
    Long duration =Long.valueOf("0");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectedThread connectedThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        On = (Button) findViewById(R.id.On);
        On.setEnabled(false);
        hour=(EditText)findViewById(R.id.hour);
        min=(EditText)findViewById(R.id.min);
        sec=(EditText)findViewById(R.id.sec);
        hour2=(EditText)findViewById(R.id.hour2);
        min2=(EditText)findViewById(R.id.min2);
        sec2=(EditText)findViewById(R.id.sec2);
        list = (ListView) findViewById(R.id.list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.d("Ajhgfg", "we will just call after this btonmethod");
        BluetoothOnMethod();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("TAG", String.valueOf(i));
                c = new ConnectThread(btarray[i]);
                c.start();
            }
        });

        sec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String h=hour.getText().toString();
//                String m=min.getText().toString();
//
                String s=sec.getText().toString();
//                Long hr=Long.parseLong(h)*3600;
//                Long mn=Long.parseLong(m)*60;

//                Long b= ((hr+mn+se)*1000);
                if(!s.isEmpty()) {
                    Long se=Long.parseLong(s);
                    if ((se >= 10) && greater) {
                        On.setEnabled(true);
                    } else {
                        On.setEnabled(false);
                    }
                }
            }
        });

        sec2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String h1=hour2.getText().toString();
//                String m1=min2.getText().toString();
//
                String s1 = sec2.getText().toString();
//
//                Long hr1=Long.parseLong(h1)*3600;
//                Long mn1=Long.parseLong(m1)*60;

//                Long b1= ((hr1+mn1+se1)*1000);
         if(!s1.isEmpty()){
             Long se1 = Long.parseLong(s1);
                if (se1 >= 10) {
                    On.setEnabled(true);
                    greater = true;
                } else {
                    On.setEnabled(false);
                    greater = false;
                }
            }
        }

            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth is not enabled", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void BluetoothOnMethod() {
        On.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String h=hour.getText().toString();
                String m=min.getText().toString();

                String s=sec.getText().toString();

                Long hr=Long.parseLong(h)*3600;
                Long mn=Long.parseLong(m)*60;
                Long se=Long.parseLong(s);
                Long b= ((hr+mn+se)*1000);
                String h1=hour2.getText().toString();
                String m1=min2.getText().toString();

                String s1=sec2.getText().toString();

                Long hr1=Long.parseLong(h1)*3600;
                Long mn1=Long.parseLong(m1)*60;
                Long se1=Long.parseLong(s1);
                Long b1= ((hr1+mn1+se1)*1000);
                duration=b;

                Log.i("tag", "duration "+duration);

                if (bluetoothAdapter == null) {
                    Log.d("Ajhgfg", "bt on method");
                    Toast.makeText(getApplicationContext(), "Bluetooth is unavailable", Toast.LENGTH_LONG).show();
                } else {

                    Log.d("Ajhgfg", "bt on method");
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                        Log.i("Ajhgfg", "bt is working");

                    }
                    paireddevicelist();
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.i("TAG", String.valueOf(i));
                            c = new ConnectThread(btarray[i]);
                            c.start();
                        }
                    });

                    if(connectionsuccessful){
                        if(!timerrunning){
                            CountDownTimer t2=new CountDownTimer(b1, 1000) {
                                @Override
                                public void onTick(long l) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hour2.setEnabled(false);
                                            min2.setEnabled(false);
                                            sec2.setEnabled(false);
                                            String timer = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(l),
                                                    TimeUnit.MILLISECONDS.toMinutes(l)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                                                    TimeUnit.MILLISECONDS.toSeconds(l)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                                            final String[] hourminsec = timer.split(":");
                                            if(Integer.parseInt(hourminsec[2]) <= 10 && Integer.parseInt(hourminsec[1]) == 0 && Integer.parseInt(hourminsec[0])==0 ) {
                                                hour2.setText("00");
                                                min2.setText("00");
                                                sec2.setText("00");
                                                duration=Long.valueOf("0");
                                                Log.i("TAG","less than 10");
                                                cancel();
                                                connectedThread = new ConnectedThread(c.mmSocket);
                                                byte[] b=new byte[1];
                                                b[0]='0';
                                                connectedThread.write(b);
                                                timerrunning=false;
                                                hour2.setEnabled(true);
                                                min2.setEnabled(true);
                                                sec2.setEnabled(true);

                                            }
                                            else{
                                                hour2.setText(hourminsec[0]);
                                                min2.setText(hourminsec[1]);
                                                sec2.setText(hourminsec[2]);
                                                Log.i("TAG",hourminsec[2]);
                                            }

                                        }
                                    });

                                }

                                @Override
                                public void onFinish() {

//                           duration=120;



                                }
                            };

                            CountDownTimer jk=new CountDownTimer(duration, 1000) {
                                @Override
                                public void onTick(long l) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hour.setEnabled(false);
                                            min.setEnabled(false);
                                            sec.setEnabled(false);
                                            String timer = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(l),
                                                    TimeUnit.MILLISECONDS.toMinutes(l)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l)),
                                                    TimeUnit.MILLISECONDS.toSeconds(l)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                                            final String[] hourminsec = timer.split(":");
                                            if(Integer.parseInt(hourminsec[2]) <= 10 && Integer.parseInt(hourminsec[1]) == 0 && Integer.parseInt(hourminsec[0])==0 ) {
                                                hour.setText("00");
                                                min.setText("00");
                                                sec.setText("00");
                                                duration=Long.valueOf("0");
                                                Log.i("TAG","less than 10");
                                                cancel();
                                                connectedThread = new ConnectedThread(c.mmSocket);
                                                byte[] b=new byte[1];
                                                b[0]='1';
                                                connectedThread.write(b);
                                                hour.setEnabled(true);
                                                min.setEnabled(true);
                                                sec.setEnabled(true);
                                                t2.start();
                                                timerrunning=false;
                                            }
                                            else{
                                                hour.setText(hourminsec[0]);
                                                min.setText(hourminsec[1]);
                                                sec.setText(hourminsec[2]);
                                                Log.i("TAG",hourminsec[2]);
                                            }

                                        }
                                    });

                                }

                                @Override
                                public void onFinish() {

//                           duration=120;



                                }
                            }.start();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Timer is already Running",Toast.LENGTH_LONG).show();
                        }


                    }
                }

            }
        });
    }

    public void paireddevicelist() {
        paireddevices = bluetoothAdapter.getBondedDevices();
        ArrayList l = new ArrayList();
        int index = 0;
        btarray = new BluetoothDevice[paireddevices.size()];
        Log.i("Ajhgfg", "inside pariedDevidceList");
        if (paireddevices.size() > 0) {
            for (BluetoothDevice device : paireddevices) {
                Log.i("test", "hellkow");
                Log.i("Ajhgfg", device.getName());
                btarray[index] = device;
                l.add(device.getName());
                index++;
            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, l);
            list.setAdapter(arrayAdapter);
//            list.setOnItemClickListener(myListClickListener);
        } else {
            Log.i("test", "inside no devicdes");

        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        String TAG = "TAG";

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.i("TAG", "Temporary Socket");
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
//                Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Connection succesful");
                connectionsuccessful=true;
            } catch (IOException e) {

                Log.e("TAG", e.getMessage());
                try {
                    mmSocket.close();
                }
                // Unable to connect; close the socket and return.
                catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

        }

        public void write(byte[] bytes) {

                if(mmOutStream!=null){
                    try{
                mmOutStream.write(bytes);
                        Log.i("zTAG","mmsg probably sent to the esp");
                    }
                    catch(Exception e){}
                }
                else{
                    Log.i("zTAG","null stream");
                }


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}



