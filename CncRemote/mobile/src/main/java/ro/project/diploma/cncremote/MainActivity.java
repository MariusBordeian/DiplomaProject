package ro.project.diploma.cncremote;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    ConnectivityManager connManager;

    InetAddress myInet;

    private static final int URL_LOADER = 0;

    private static String IP;

    private TextView coordsX;
    private TextView coordsY;
    private TextView coordsZ;
    private Button zeroMachine;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        coordsX = (TextView) findViewById(R.id.text_X);
        coordsY = (TextView) findViewById(R.id.text_Y);
        coordsZ = (TextView) findViewById(R.id.text_Z);

        zeroMachine = (Button) findViewById(R.id.button_ZeroMachine);
        zeroMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (!connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            Toast.makeText(context, "Please connect to the network to access the machine via IP Address", Toast.LENGTH_LONG).show();
        }

        getLoaderManager().initLoader(URL_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
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
                                    IP = input.getText().toString();
                                    Toast.makeText(context, IP, Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return null;
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // got data need to update coords
        String rec_X = "";
        String rec_Y = "";
        String rec_Z = "";

        //

        coordsX.setText(rec_X);
        coordsY.setText(rec_Y);
        coordsZ.setText(rec_Z);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
