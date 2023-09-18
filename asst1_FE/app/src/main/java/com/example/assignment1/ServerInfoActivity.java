package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerInfoActivity extends AppCompatActivity {
    private static final String TAG = "ServerInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_info);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        TextView googleNameText = findViewById(R.id.google_name_text);
        assert account != null;
        String googleFirstLast = account.getGivenName() + " " + account.getFamilyName();
        googleNameText.setText(googleFirstLast);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        TextView clientTimeText = findViewById(R.id.client_time_text);
        clientTimeText.setText(sdf.format(new Date()));

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        TextView clientIpText = findViewById(R.id.client_ip_text);
        clientIpText.setText(ipAddress);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://107.20.118.211:8088/";
        String[] requestEndPoints = {"ipaddress", "time", "name"};
        int[] textViews = {R.id.server_ip_text, R.id.server_time_text, R.id.my_name_text};
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            StringRequest request = new StringRequest(Request.Method.GET, url + requestEndPoints[i],
                    response -> responseHandler(textViews[finalI], response), error -> Log.d(TAG, "Error:" + error));
            queue.add(request);
        }
    }
    private void responseHandler(int textId, String textValue) {
        TextView textField = findViewById(textId);
        textField.setText(textValue);
    }
}