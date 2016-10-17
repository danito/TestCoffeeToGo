package be.nixekinder.testcoffeetogo;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.NumberFormat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    //Variablen
    public float geld;
    public float gewinn;
    public int recycling;
    public int shops;
    public int rabatt;
    public ImageView kaffeeBild;
    public ImageView recyclingBild;
    public ImageView shopsBild;
    public ImageView rabattBild;
    public TextView geldAnzeige;
    public NumberFormat format = NumberFormat.getCurrencyInstance();

    public Button save_button;
    public EditText known_username;
    public EditText known_url;
    public EditText known_api;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int mWindow = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWindow = 0;
        geld = 0F;
        gewinn = 0.1F;
        recycling = 0;
        shops = 0;
        rabatt = 1;
        kaffeeBild = (ImageView) findViewById(R.id.kaffeeBild);
        recyclingBild = (ImageView) findViewById(R.id.recyclingBild);
        shopsBild = (ImageView) findViewById(R.id.shopBild);
        rabattBild = (ImageView) findViewById(R.id.rabattBild);
        geldAnzeige = (TextView) findViewById(R.id.geldAnzeige);
        pref = getSharedPreferences("KaffeeClicker",0);
        editor = pref.edit();
        geld = laden("Geld", geld);
        recycling = laden("Recycling", recycling);
        shops = laden("Shops", shops);
        rabatt = laden("Rabatt", rabatt);
        geldAnzeigeText();

        kaffeeBild.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                verkaufeKaffee();
            }
        });

        shopsBild.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                kaufeShop();
            }
        });

        recyclingBild.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void  onClick(View view) {
                kaufeRecycling();
            }
        });

        /**
         *
         */
        rabattBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kaufeRabatt();
            }
        });

/*        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });*/


        vergebeBelohnung();
        NotificationManager mNotifificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE) ;
        mNotifificationManager.cancelAll();
        cancelAlarm(this);
        setAlarm(this);


    }

    private boolean saveSettings() {
        known_api = (EditText) findViewById(R.id.k_api);
        String vKnownApi = known_api.getText().toString();
        known_url = (EditText) findViewById(R.id.k_url);
        String vKnownUrl = known_url.getText().toString();
        known_username = (EditText) findViewById(R.id.k_username);
        String vKnownUsername = known_username.getText().toString();
        boolean vSave = false;

        if (vKnownUsername == "") {
            Toast.makeText(this, R.string.empty_username,Toast.LENGTH_SHORT).show();
            vSave = false;
        } else {
            speichern("KnownUsername",vKnownUsername);
            vSave = true;
        }
        if (vKnownUrl == "") {
            Toast.makeText(this, R.string.empty_url,Toast.LENGTH_SHORT).show();
            vSave = false;
        } else {
            speichern("KnownUrl", vKnownUrl);
            vSave = true;
        }
        if (vKnownApi == "") {
            Toast.makeText(this, R.string.empty_api,Toast.LENGTH_SHORT).show();
            vSave = false;
        } else {
            speichern("KnownApi",vKnownApi);
            vSave = true;
        }
        if (vSave){
            Toast.makeText(this, R.string.settings_saved,Toast.LENGTH_SHORT).show();
        }
        return  vSave;
    }

    @Override
    public void onBackPressed(){
        if(mWindow == 1){
            setContentView(R.layout.activity_main);
            mWindow = 0;
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ueber:
                //
                DialogFragment newFragment = new UeberDialog();
                newFragment.show(this.getFragmentManager(),getString(R.string.about));
                return true;
            case R.id.menu_settings:
                //
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                mWindow = 1;
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void kaufeRabatt() {
        if(10 >= rabatt) {
            int preis = (int) Math.pow(2,rabatt) * 10;
            if (geld >= preis){
                geld -= preis;
                rabatt ++;
                geldAnzeigeText();
                speichern("Rabatt", this.rabatt);
            } else {
                Toast.makeText(this,preis + "€ sind nötig.", Toast.LENGTH_SHORT).show();
            }
        }   else {
            Toast.makeText(this, "Es wurden bereits 10% Rabatt erteilt.", Toast.LENGTH_SHORT).show();
        }
    }

    public void kaufeRecycling() {
        int preis = (int) Math.pow(2,recycling)*50;

        if(geld >= preis) {
            geld -= preis;
            recycling ++;
            geldAnzeigeText();
            speichern("Recycling", this.recycling);
        } else {
            Toast.makeText(this, preis + "€ sind nötig.",Toast.LENGTH_SHORT).show();
        }
    }

    public void kaufeShop() {
        int preis = (int) Math.pow(2, shops) * 1000;
        if(geld >= preis){
            geld -= preis;
            shops ++;
            geldAnzeigeText();
            speichern("Shops", this.shops);
        } else {
            Toast.makeText(this, preis + "€ sind nötig", Toast.LENGTH_SHORT).show();
        }
    }

    public void verkaufeKaffee(){
        float gewinn = this.gewinn;
        gewinn = gewinn + (recycling * 0.001F);
        if(rabatt > 1) {
            gewinn = (gewinn * (rabatt +1)) * (1 - (rabatt * 0.01F));
        }
        gewinn = gewinn + (gewinn * shops);

        geld += gewinn;
        geldAnzeigeText();
        speichern("Geld", this.geld);
    }

    public void geldAnzeigeText() {
        geldAnzeige.setText("Geld: " + NumberFormat.getCurrencyInstance().format(geld));
    }

    public void speichern(String name, int wert) {
        editor.putInt(name,wert);
        editor.commit();
    }

    public void speichern(String name, float wert){
        editor.putFloat(name, wert);
        editor.commit();
    }
    public void speichern(String name, String wert){
        editor.putString(name, wert);
        editor.commit();
    }
    public int laden(String name, int standardwert){
        return pref.getInt(name, standardwert);
    }

    public float laden(String name, float standardwert) {
        return pref.getFloat(name,standardwert);
    }

    public static class UeberDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedinstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dial_username);
            builder.setMessage(R.string.ueber_text)
                    .setPositiveButton(R.string.ok, new
                            DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id) {
                                    dismiss();
                                }
                            });
            return builder.create();
        }
    }

    public void vergebeBelohnung(){
        long zeitMilli = System.currentTimeMillis();
        int zeit = (int) (zeitMilli/1000);
        int letzteZeit = laden("letzteZeit",zeit);
        int stunden = (zeit - letzteZeit)/3600;
        for (int i = 0; i < stunden; i++){
            verkaufeKaffee();
        }
        speichern("letzteZeit",zeit);
        Toast.makeText(this,"Du hast eine Belohnung erhalten", Toast.LENGTH_SHORT).show();
    }

    public void setAlarm(Context context){
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Erinnerung.class);
        PendingIntent pi = PendingIntent.getBroadcast(context,0,i,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60*60*24, pi);
    }

    public static class UeberDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedinstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dial_username);
            builder.setMessage(R.string.ueber_text)
                    .setPositiveButton(R.string.ok, new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dismiss();
                                }
                            });
            return builder.create();
        }
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Erinnerung.class);
        PendingIntent sender  = PendingIntent.getBroadcast(context,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



    // this is the END
}
