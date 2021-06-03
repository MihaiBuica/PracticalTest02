package ro.pub.cs.systems.eim.practicaltest02.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText hourEditText = null;
    private EditText minEditText = null;
    private Button setButton = null;
    private Button pollButton = null;
    private Button resetButton = null;
    private TextView responeServerTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.i(Constants.TAG, "[MAIN ACTIVITY] Server button");;
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private ClientButtonClickListener clientButtonClickListener = new ClientButtonClickListener();
    private class ClientButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String operation = "";
            if (view.getId() == R.id.set_button)
            {
                operation = "set";
            }
            else if (view.getId() == R.id.reset_button)
            {
                operation = "reset";
            }
            else if (view.getId() == R.id.poll_button)
            {
                operation = "poll";
            }
            Log.i(Constants.TAG, "[MAIN ACTIVITY] Client button " + operation);

            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String hour = hourEditText.getText().toString();
            String min = minEditText.getText().toString();
            responeServerTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), hour, min, operation, responeServerTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        hourEditText = findViewById(R.id.hour_edit_text);
        minEditText = findViewById(R.id.min_edit_text);
        setButton = findViewById(R.id.set_button);
        resetButton = findViewById(R.id.reset_button);
        pollButton = findViewById(R.id.poll_button);

        setButton.setOnClickListener(clientButtonClickListener);
        resetButton.setOnClickListener(clientButtonClickListener);
        pollButton.setOnClickListener(clientButtonClickListener);

        responeServerTextView = (TextView)findViewById(R.id.server_info_text_view);
    }
}
