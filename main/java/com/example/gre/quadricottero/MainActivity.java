package com.example.gre.quadricottero;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    RelativeLayout layout_joystick;
    //ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;

    RelativeLayout layout_joystick1;
    //ImageView image_joystick1, image_border1;
    TextView textView22, textView33, textView44, textView55, textView66;

    Button btnYawSx, btnYawDx;
    Button SwitchOFF, btScanButton;
    TextView txtSend, txtReceved, txtSpeed, LOGTxtView;
    //TextView textView1bt;

    String txt_send;

    JoyStickClass js;
    JoyStickClassT jst;

    int Speed;

    Handler h;

    private static final String TAG = "bluetooth2";
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    public static String address = "98:D3:31:20:59:30"; //98:D3:31:20:59:30

    public static boolean entry_second_activity = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        h = new Handler() {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        init();
        txt_send = "Send: ";
        txtSend.setText(String.valueOf(txt_send));

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView1.setText("X : " + String.valueOf(js.getX()));
                    textView2.setText("Y : " + String.valueOf(js.getY()));
                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
                    textView4.setText("Distance : " + String.valueOf(js.getDistance()));

                    int direction = js.get8Direction();
                    if (direction == JoyStickClass.STICK_UP) {
                        textView5.setText("Direction : Up");
                        mConnectedThread.write("G");
                        txt_send = txt_send + 'G';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                        textView5.setText("Direction : Up");
                        mConnectedThread.write("G");
                        txt_send = txt_send + 'G';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_RIGHT) {
                        textView5.setText("Direction : Right");
                        mConnectedThread.write("D");
                        txt_send = txt_send + 'D';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView5.setText("Direction : Down");
                        mConnectedThread.write("E");
                        txt_send = txt_send + 'E';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_DOWN) {
                        textView5.setText("Direction : Down");
                        mConnectedThread.write("E");
                        txt_send = txt_send + 'E';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView5.setText("Direction : Down");
                        mConnectedThread.write("E");
                        txt_send = txt_send + 'E';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_LEFT) {
                        textView5.setText("Direction : Left");
                        mConnectedThread.write("F");
                        txt_send = txt_send + 'F';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_UPLEFT) {
                        textView5.setText("Direction : Up");
                        mConnectedThread.write("G");
                        txt_send = txt_send + 'G';
                        mConnectedThread.write(" ");

                    } else if (direction == JoyStickClass.STICK_NONE) {
                        textView5.setText("Direction : Center");
                        mConnectedThread.write("C");
                        txt_send = txt_send + 'C';
                        mConnectedThread.write(" ");
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView1.setText("X :");
                    textView2.setText("Y :");
                    textView3.setText("Angle :");
                    textView4.setText("Distance :");
                    textView5.setText("Direction :");
                    mConnectedThread.write("C");
                    mConnectedThread.write(" ");
                }
                return true;
            }
        });
        txtSend.setText(String.valueOf(txt_send));
        layout_joystick1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                jst.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    textView22.setText("X : " + String.valueOf(jst.getX()));
                    textView33.setText("Y : " + String.valueOf(jst.getY()));
                    textView44.setText("Angle : " + String.valueOf(jst.getAngle()));
                    textView55.setText("Distance : " + String.valueOf(jst.getDistance()));

                    int direction = jst.get8Direction();
                    if (direction == JoyStickClass.STICK_UP) {
                        textView66.setText("Direction : Up");
                        Speed++;
                        if (Speed == 255) {
                            Speed = 254;
                        }
                        txt_send = txt_send + 'X';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("X");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_UPRIGHT) {
                        textView66.setText("Direction : Up");
                        Speed++;
                        if (Speed == 255) {
                            Speed = 254;
                        }
                        txt_send = txt_send + 'X';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("X");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_RIGHT) {
                        textView66.setText("Direction : Right");
                        Speed++;
                        if (Speed == 255) {
                            Speed = 254;
                        }
                        txt_send = txt_send + 'X';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("X");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {
                        textView66.setText("Direction : Down");
                        Speed--;
                        if (Speed == 40) {
                            Speed = 41;
                        }
                        txt_send = txt_send + 'Z';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("Z");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_DOWN) {
                        textView66.setText("Direction : Down");
                        Speed--;
                        if (Speed == 40) {
                            Speed = 41;
                        }
                        txt_send = txt_send + 'Z';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("Z");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_DOWNLEFT) {
                        textView66.setText("Direction : Down");
                        Speed--;
                        if (Speed == 40) {
                            Speed = 41;
                        }
                        txt_send = txt_send + 'Z';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("Z");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_LEFT) {
                        textView66.setText("Direction : Left");
                        Speed--;
                        if (Speed == 40) {
                            Speed = 41;
                        }
                        txt_send = txt_send + 'Z';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("Z");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_UPLEFT) {
                        textView66.setText("Direction : Up");
                        Speed++;
                        if (Speed == 255) {
                            Speed = 254;
                        }
                        txt_send = txt_send + 'X';
                        txt_send = txt_send + 'C';
                        mConnectedThread.write("X");
                        mConnectedThread.write("C");
                        mConnectedThread.write(" ");
                    } else if (direction == JoyStickClass.STICK_NONE) {
                        textView66.setText("Direction : Center");
                    }
                    // mConnectedThread.write(""+Speed); //----------SPEED--------
                    txtSpeed.setText("Speed : " + Speed);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    textView22.setText("X :");
                    textView33.setText("Y :");
                    textView44.setText("Angle :");
                    textView55.setText("Distance :");
                    textView66.setText("Direction :");
                }
                return true;
            }
        });
        txtSend.setText(String.valueOf(txt_send));

        btnYawDx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("B"); // Send "1" via Bluetooth
                txt_send = txt_send + 'B';
                mConnectedThread.write("C");
                mConnectedThread.write(" ");
            }
        });
        txtSend.setText(String.valueOf(txt_send));

        btnYawSx.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("A"); // Send "1" via Bluetooth
                txt_send = txt_send + 'A';
                mConnectedThread.write("C");
                mConnectedThread.write(" ");
            }
        });
        txtSend.setText(String.valueOf(txt_send));

        SwitchOFF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("O"); // Send "1" via Bluetooth
                txt_send = txt_send + 'O';
                mConnectedThread.write(" ");
            }
        });
        txtSend.setText(String.valueOf(txt_send));

        btScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Pop.class));
            }
        });


        txtSpeed.setText((CharSequence) mConnectedThread);

    }

    private void init() {
        Speed = 40;

        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        textView22 = (TextView)findViewById(R.id.textView22);
        textView33 = (TextView)findViewById(R.id.textView33);
        textView44 = (TextView)findViewById(R.id.textView44);
        textView55 = (TextView)findViewById(R.id.textView55);
        textView66 = (TextView)findViewById(R.id.textView66);

        txtSend    = (TextView)findViewById(R.id.SendText);
        txtSpeed   = (TextView)findViewById(R.id.textSpeed);
        txtReceved = (TextView)findViewById(R.id.txtReceved);
        LOGTxtView = (TextView) findViewById(R.id.LOGtxtView);

        btnYawDx = (Button)findViewById(R.id.YawDX);
        btnYawSx = (Button)findViewById(R.id.YawSX);
        SwitchOFF = (Button)findViewById(R.id.Switch_Off);
        btScanButton = (Button)findViewById(R.id.btScan);

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);
        layout_joystick1 = (RelativeLayout)findViewById(R.id.layout_joystick1);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(250, 250);
        js.setLayoutSize(400, 400);
        js.setLayoutAlpha(100);
        js.setStickAlpha(50);
        js.setOffset(10);
        js.setMinimumDistance(10);

        jst = new JoyStickClassT(getApplicationContext(), layout_joystick1, R.drawable.image_button);
        jst.setStickSize(250, 250);
        jst.setLayoutSize(400, 400);
        jst.setLayoutAlpha(100);
        jst.setStickAlpha(50);
        jst.setOffset(10);
        jst.setMinimumDistance(10);

        //txtSend.setText(txt_send);

    }



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                LOGTxtView.setText("FATAL ERROR");
            }

            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            //btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.d(TAG, "....Connection ok...");
                LOGTxtView.setText("Connected");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Create Socket...");

            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();



    }


    @Override
        public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { LOGTxtView.setText("Error 002"); }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            LOGTxtView.setText("Send OK");
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                LOGTxtView.setText("Error 001");
            }
        }
    }


}
