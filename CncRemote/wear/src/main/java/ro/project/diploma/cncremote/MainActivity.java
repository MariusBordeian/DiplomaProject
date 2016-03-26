package ro.project.diploma.cncremote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity {
    public final Context context = this;

    private static String IP = "http://192.168.1.143:15500";    // demo
    private RequestQueue requestsQueue;
    private TextView coordsX;
    private TextView coordsY;
    private TextView coordsZ;
    private TextView incrementScaleView;
    private StringRequest getSpindleRequest;
    private Integer incrementScale = 1;
    private Integer delayBetweenSteps = 460; // minimum working delay!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        requestsQueue = Volley.newRequestQueue(context);
        ConnectivityManager connManager;
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            Toast.makeText(context, "Please connect to the network to access the machine via IP Address", Toast.LENGTH_LONG).show();
        } else {
            findViewById(R.id.button_Xminus).setEnabled(true);
            findViewById(R.id.button_Xplus).setEnabled(true);
            findViewById(R.id.button_Yplus).setEnabled(true);
            findViewById(R.id.button_Yminus).setEnabled(true);
            findViewById(R.id.button_Zplus).setEnabled(true);
            findViewById(R.id.button_Zminus).setEnabled(true);
            findViewById(R.id.button_ZeroMachine).setEnabled(true);
            findViewById(R.id.scaleMinus).setEnabled(true);
            findViewById(R.id.scalePlus).setEnabled(true);

            Timer t = new Timer();
            TimerTask tt = new TimerTask() {
                public void run() {
                    if (IP != null && getSpindleRequest != null)
                        requestsQueue.add(getSpindleRequest);
                }
            };
            t.schedule(tt, 0, 100);

            coordsX = (TextView) findViewById(R.id.text_X);
            coordsY = (TextView) findViewById(R.id.text_Y);
            coordsZ = (TextView) findViewById(R.id.text_Z);
            incrementScaleView = (TextView) findViewById(R.id.incrementScale);
            incrementScaleView.setText("" + incrementScale);

            TextView zeroMachine;
            zeroMachine = (TextView) findViewById(R.id.button_ZeroMachine);
            zeroMachine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGet(IP + "/CNC/GUI?load=whatever&speed=" + delayBetweenSteps + "&action=zeroMachine");
                }
            });

            doGet(IP + "/CNC/GUI");
            getSpindleRequest = new StringRequest(Request.Method.GET, IP + "/CNC/GUI?load=whatever&action=getSpindlePosition",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (IP != null) {
                                String[] coords = response.split("#");
                                if (coords.length == 3) {
                                    coordsX.setText(coords[0]);
                                    coordsY.setText(coords[1]);
                                    coordsZ.setText(coords[2]);
                                }
                            }
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

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            coordsX.setTextColor(getResources().getColor(android.R.color.white));
            coordsY.setTextColor(getResources().getColor(android.R.color.white));
            coordsZ.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            coordsX.setTextColor(getResources().getColor(android.R.color.black));
            coordsY.setTextColor(getResources().getColor(android.R.color.black));
            coordsZ.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    public void alterPosition(View v) {
        if (IP != null)
        {
            String incrementStr = incrementScaleView.getText().toString();
            switch (v.getId()) {
                case R.id.button_Xminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=X&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Xplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=X&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Yminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=Y&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Yplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=Y&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Zminus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=Z&dir=minus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
                    break;
                case R.id.button_Zplus:
                    doGet(IP + "/CNC/GUI?load=whatever&action=alterPosition&speed=" + delayBetweenSteps + "&incrementScale=" + incrementStr + "&axis=Z&dir=plus&currX=" + coordsX.getText().toString() + "&currY=" + coordsY.getText().toString() + "&currZ=" + coordsZ.getText().toString());
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
                if (incrementScale > 1)
                    incrementScale--;
                break;
        }
        incrementScaleView.setText(incrementScale.toString());
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestsQueue.cancelAll(Request.Priority.NORMAL);
                Log.d("Request : ", "That did NOT work for " + url);
                Toast.makeText(context, "That did NOT work for " + url, Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        //*/
    }

}
