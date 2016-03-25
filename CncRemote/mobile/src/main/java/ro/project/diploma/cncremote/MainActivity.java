package ro.project.diploma.cncremote;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  {
    public final Context context = this;

    private static String IP;
    private ConnectivityManager connManager;
    private RequestQueue requestsQueue;
    private TextView coordsX;
    private TextView coordsY;
    private TextView coordsZ;
    private TextView incrementScaleView;
    private Button zeroMachine;
    private StringRequest getSpindleRequest;
    private Integer incrementScale = 1;

    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestsQueue = Volley.newRequestQueue(context);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Timer t=new Timer();
        TimerTask tt=new TimerTask() {
            public void run() {
                if(IP!=null && getSpindleRequest!=null)
                    requestsQueue.add(getSpindleRequest);
            }};

        t.schedule(tt,0,100);

        coordsX = (TextView) findViewById(R.id.text_X);
        coordsY = (TextView) findViewById(R.id.text_Y);
        coordsZ = (TextView) findViewById(R.id.text_Z);
        incrementScaleView = (TextView)findViewById(R.id.incrementScale);
        incrementScaleView.setText(""+incrementScale);
        
        zeroMachine = (Button) findViewById(R.id.button_ZeroMachine);
        zeroMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGet(IP + "/CNC/GUI?load=whatever&action=zeroMachine");
            }
        });

        if (!connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            Toast.makeText(context, "Please connect to the network to access the machine via IP Address", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.config, menu);
        return true;
    }

    public void alterPosition(View v) {
        if (IP != null)
        {
            String incrementStr = incrementScaleView.getText().toString();
            switch (v.getId()) {
                case R.id.button_Xminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=X&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Xplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=X&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Yminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=Y&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Yplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=Y&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Zminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=Z&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Zplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&incrementScale=" + incrementStr + "&axis=Z&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
            }
        }
    }

    public void setScale(View v) {
        incrementScale = Integer.parseInt(incrementScaleView.getText().toString());
        switch (v.getId()){
            case R.id.scalePlus:
                incrementScale++;
                break;
            case R.id.scaleMinus:
                incrementScale--;
                break;
        }
        incrementScaleView.setText(incrementScale.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_IP:
                if (!connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                    Toast.makeText(context, "Please connect to the network to access the machine via IP Address", Toast.LENGTH_LONG);
                } else {
                    LayoutInflater layoutInflater = LayoutInflater.from(context);

                    View promptView = layoutInflater.inflate(R.layout.ip, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setView(promptView);

                    final EditText input = (EditText) promptView.findViewById(R.id.ip_address);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    IP = "http://" + input.getText().toString();
                                    doGet(IP + "/CNC/GUI");
                                    if (connected) {
                                        Toast.makeText(context, "Success on " + IP, Toast.LENGTH_SHORT).show();
                                        getSpindleRequest = new StringRequest(Request.Method.GET, IP + "/CNC/GUI?load=whatever&action=getSpindlePosition",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        String[] coords = response.split("#");
                                                        if (coords.length == 3) {
                                                            coordsX.setText(coords[0]);
                                                            coordsY.setText(coords[1]);
                                                            coordsZ.setText(coords[2]);
                                                        }
                                                        // Display the first 500 characters of the response string.
                                                        //Log.d("Response is: ", response.substring(0, 500));
                                                        //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("Request : ", IP + "/CNC/GUI?load=whatever&action=getSpindlePosition" + " That didn't work!");
                                                Toast.makeText(context, IP + "/CNC/GUI?load=whatever&action=getSpindlePosition" + " That didn't work!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();

                    alertD.show();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doGet(final String url) {
        //*
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("Response is: ", response.substring(0, 500));
                        //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        connected = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Request : ", "That did NOT work for " + url);
                Toast.makeText(context, "That did NOT work for " + url, Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        //*/
    }

}
