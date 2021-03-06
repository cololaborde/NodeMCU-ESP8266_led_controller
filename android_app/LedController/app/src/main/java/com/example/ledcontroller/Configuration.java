package com.example.ledcontroller;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.util.Locale;

public class Configuration extends AppCompatActivity implements ColorPicker.OnColorChangedListener {


    private boolean state = false;
    private String aux, url;
    private int r = 128, g = 255, b = 0;
    private EditText hex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        String host = getIntent().getExtras().getString("host");

        hex = findViewById(R.id.hexa_edit);

        url = "http://" + host;

        //first time to sync app state with led state
        setEndpoint();
        sendRequest(url);

        setColorPickerListener();
        setSwitchListener();
    }

    protected void sendRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                System.out::println, System.out::println);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    protected void setEndpoint() {
        String red = String.format(Locale.getDefault(), "%03d", r);
        String green = String.format(Locale.getDefault(), "%03d", g);
        String blue = String.format(Locale.getDefault(), "%03d", b);
        if (!state) {
            aux = url + "/LED=OFF&R=" + red + "&V=" + green + "&B=" + blue;
        } else {
            aux = url + "/LED=ON&R=" + red + "&V=" + green + "&B=" + blue;
        }
    }

    protected void setSwitchListener() {
        SwitchCompat onOffSwitch = findViewById(R.id.onOff_switch);
        onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            state = !state;
            setEndpoint();
            sendRequest(aux);
        });
    }

    protected void setColorPickerListener() {
        ColorPicker picker = findViewById(R.id.picker);

        //To get the color
        hex.setText(String.format("#%06X", (0xFFFFFF & picker.getColor())));

        //To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());

        // adds listener to the colorPicker which is implemented
        //in the activity
        picker.setOnColorChangedListener(this);

        //to turn of showing the old color
        picker.setShowOldCenterColor(false);

    }

    protected void intToRGB(int colorInt) {
        //String hex = String.format("#%06X", (0xFFFFFF & colorInt));
        r = Color.red(colorInt);
        g = Color.green(colorInt);
        b = Color.blue(colorInt);
    }

    @Override
    public void onColorChanged(int color) {
        intToRGB(color);
        setEndpoint();
        hex.setText(String.format("#%06X", (0xFFFFFF & color)));
        sendRequest(aux);
    }
}