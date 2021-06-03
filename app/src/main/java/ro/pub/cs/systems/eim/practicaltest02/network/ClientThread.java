package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String hourServer;
    private String minServer;
    private String opServer;
    private Socket socket;
    private TextView responseTextView;

    public ClientThread(String address, int port, String hourServer, String minServer, String opServer, TextView responseTextView) {
        this.address = address;
        this.port = port;
        this.hourServer = hourServer;
        this.minServer = minServer;
        this.opServer = opServer;
        this.responseTextView = responseTextView;
    }

    @Override
    public void run() {
        try {
            Log.i(Constants.TAG, "[CLIENT THREAD] Searching for server!" + hourServer + " " + minServer + " " + opServer);
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println("id");
            printWriter.flush();

            if (opServer.equals("set"))
            {
                printWriter.println(opServer+","+hourServer+","+minServer+"\n");
                printWriter.flush();
            }
            else
            {
                printWriter.println(opServer+"\n");
                printWriter.flush();
            }

            String responeServer;
            while ((responeServer = bufferedReader.readLine()) != null) {
                final String finalizedInformation = responeServer;
                responseTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        responseTextView.setText(finalizedInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
