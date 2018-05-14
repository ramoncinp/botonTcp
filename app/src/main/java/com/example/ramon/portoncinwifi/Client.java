package com.example.ramon.portoncinwifi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client
{

    private String serverIp; //your computer IP address
    private int serverPort;
    // sends message received notifications
    private OnMessageReceived mMessageListener;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    public Client(OnMessageReceived listener, String serverIp, int serverPort)
    {
        mMessageListener = listener;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void stopClient()
    {
        if (mBufferOut != null)
        {
            mBufferOut.flush();
            mBufferOut.close();
        }
    }

    public void run(String mensaje)
    {
        try
        {
            InetAddress serverAddress = InetAddress.getByName(serverIp);

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, serverPort), 4000);
            socket.setSoTimeout(20000);

            char[] buffer = new char[2048];
            int charsRead;

            //sends the message to the server
            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //receives the message which the server sends back
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (mBufferOut != null && !mBufferOut.checkError())
            {
                mBufferOut.print(mensaje);
                mBufferOut.flush();
            }

            if ((charsRead = mBufferIn.read(buffer)) != -1 && mMessageListener != null)
            {
                String mServerMessage = new String(buffer).substring(0, charsRead);
                mMessageListener.messageReceived(mServerMessage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mMessageListener.error(e.getMessage());
        }
        finally
        {
            stopClient();
        }
    }

    public interface OnMessageReceived
    {
        void messageReceived(String message);

        void error(String message);
    }
}
