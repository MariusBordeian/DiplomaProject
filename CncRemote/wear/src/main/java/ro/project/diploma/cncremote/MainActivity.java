package ro.project.diploma.cncremote;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView coordsX;
    private TextView coordsY;
    private TextView coordsZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        coordsX = (TextView) findViewById(R.id.text_X);
        coordsY = (TextView) findViewById(R.id.text_Y);
        coordsZ = (TextView) findViewById(R.id.text_Z);
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
}
