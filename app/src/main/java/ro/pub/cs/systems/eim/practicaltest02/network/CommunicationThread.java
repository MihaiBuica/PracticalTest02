package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.ServerInformationModel;

public class CommunicationThread extends Thread{
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client!");
            String clientId = bufferedReader.readLine();
            String infoServer = bufferedReader.readLine();
            if (infoServer == null || infoServer.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Client info: " + infoServer + " " + clientId);


            HashMap<String, ServerInformationModel> data = serverThread.getData();

            if (infoServer.equals("reset"))
            {
                data.remove(clientId);
                serverThread.setData(data);
                String result = null;
                result = clientId + " command: " + infoServer + " removed alarm";
                printWriter.println(result);
                printWriter.flush();
            }
            else if (infoServer.equals("poll"))
            {
                ServerInformationModel serverInformation = data.get(clientId);
                if (serverInformation.isActivated())
                {
                    String result = null;
                    result = clientId + "command: " + infoServer + " already activated";
                    printWriter.println(result);
                    printWriter.flush();
                }
                else
                {
                    // Verifica daca alarma a fost activata
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
//                    HttpClient httpClient = new DefaultHttpClient();
//                    String pageSourceCode = "===============NOT READY=====================";
//                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS);
//                    HttpResponse httpGetResponse = httpClient.execute(httpGet);
//                    HttpEntity httpGetEntity = httpGetResponse.getEntity();
//                    if (httpGetEntity != null) {
//                        pageSourceCode = EntityUtils.toString(httpGetEntity);
//
//                    }
                    String host = Constants.WEB_SERVICE_ADDRESS;
                    int port = 13;
                    Socket socketWebServer = new Socket(host, port);
                    BufferedReader bufferedReaderWeb = Utilities.getReader(socketWebServer);
                    PrintWriter printWriterWeb = Utilities.getWriter(socketWebServer);
                    if (bufferedReaderWeb == null || printWriterWeb == null) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer for Web Server are null!");
                        return;
                    }
                    printWriterWeb.println("5");
                    printWriterWeb.flush();
                    printWriterWeb.println("2");
                    printWriterWeb.flush();
                    String responeWebServer = bufferedReaderWeb.readLine();
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] response: " + responeWebServer);

                    if (responeWebServer == null)
                    {
                        String result = null;
                        result = "none\n";
                        printWriter.println(result);
                        printWriter.flush();
                    }
                    else
                    {
                        String result = null;
                        result = "activated\n";
                        printWriter.println(result);
                        printWriter.flush();
                    }

                    serverInformation.setActivated();
                    socketWebServer.close();
                }
            }
            else
            {
                serverThread.setData(clientId, new ServerInformationModel(clientId, "10", "2"));
                String result = null;
                result = "client: " + clientId + " command: " + infoServer;
                printWriter.println(result);
                printWriter.flush();
            }
//            if (data.get(clientId).isActivated())
//            {
//                String result = null;
//                result = clientId + "command: " + infoServer + " already activated";
//                printWriter.println(result);
//                printWriter.flush();
//            }
//            if (data.containsKey(clientId))
//            {
//                // Cached info
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
//                serverInformation = data.get(infoServer);
//            }
//            else
//            {
//                // TODO info from server
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
//                HttpClient httpClient = new DefaultHttpClient();
//                String pageSourceCode = "===============NOT READY=====================";
//
//                // TODO Server Request
//
//                if (pageSourceCode == null) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
//                    return;
//                } else
//                {
//                    Log.i(Constants.TAG, pageSourceCode );
//                }
//
//                // TODO Parsing the response
//
//
//                if (serverInformation == null) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Server Information is null!");
////                    return;
//                }
//
//                // Create result
////                result = serverInformation.toString();
//                // Switch for multiple choice
//                String result = null;
////                result = serverInformation.toString();
//
//                printWriter.println(result);
//                printWriter.flush();
//            }

//            String result = null;
//            result = "Bla Bla Blaaa";
//            printWriter.println(result);
//            printWriter.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
