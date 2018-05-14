package com.example.ramon.portoncinwifi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private final String ipToConnect = "192.168.8.36";
    private final int portToConnect = 2000;

    private Client mTcpClient;
    private Button boton;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        boton = (Button) findViewById(R.id.boton);
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        boton.setText("Hola :)");
    }

    public void send(View v)
    {
        vibrator.vibrate(250);
        boton.setText("...");
        new ConnectTask().execute("Copacetic");
    }

    public class ConnectTask extends AsyncTask<String, String, Client>
    {
        @Override
        protected Client doInBackground(String... message)
        {
            //we create a TCPClient object and
            mTcpClient = new Client(new Client.OnMessageReceived()
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    publishProgress(message, "");
                }

                @Override
                public void error(String message)
                {
                    publishProgress("error", message);
                }
            }, ipToConnect, portToConnect);

            mTcpClient.run(message[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

            if (values[0].contains("error"))
            {
                Toast.makeText(MainActivity.this, values[1], Toast.LENGTH_SHORT).show();
            }
            boton.setText("Hola c:");
        }
    }
}