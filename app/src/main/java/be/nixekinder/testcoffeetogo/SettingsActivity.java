package be.nixekinder.testcoffeetogo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public Button save_button;
    public EditText known_username;
    public EditText known_url;
    public EditText known_api;

    private String kUsername;
    private String kUrl;
    private String kApi;
    private boolean prefsSaved;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //
        pref = getSharedPreferences("settings", 0);
        editor = pref.edit();

        //populate fields with loaded params or blank if first time
        kUsername = getPrefs("knownUsername", "");
        known_username = (EditText) findViewById(R.id.k_username);
        known_username.setText(kUsername);

        kUrl = getPrefs("knownUrl", "");
        known_url = (EditText) findViewById(R.id.k_url);
        known_url.setText(kUrl);

        kApi = getPrefs("knownApi", "");
        known_api = (EditText) findViewById(R.id.k_api);
        known_api.setText(kApi);

        save_button = (Button) findViewById(R.id.set_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        boolean b = true;
        String toastMsg = "";
        if (isEmptyString(known_username.getText().toString()) ) {
            toastMsg = getString(R.string.empty_username);
            b = b & false;
        } else {
            savePrefs("knownUsername", known_username.getText().toString());
            Log.i("settings", "saveSettings: " + known_username.getText().toString());
            b = b & true;
        }
        if (isEmptyString(known_url.getText().toString() )) {
            toastMsg = String.valueOf(R.string.empty_url);
            b = b & false;
        } else {
            savePrefs("knownUrl", known_url.getText().toString());
            Log.i("settings", "saveSettings: " + known_url.getText().toString());

            b = b & true;
        }
        ;
        if (isEmptyString(known_api.getText().toString())) {
            toastMsg = getString(R.string.empty_api);
            b = b & false;
        } else {
            savePrefs("knownApi", known_api.getText().toString());
            Log.i("settings", "saveSettings: " + known_api.getText().toString());
            b = b & true;
        }
        if (b) {
            toastMsg = getString(R.string.all_saved);
            savePrefs("savedPrefs", b);
            getSignature();
        }

        toastMessage(toastMsg);
    }

    private void toastMessage(String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * save sharedPreferences "settings"
     *
     * @param name
     * @param val
     */

    public void savePrefs(String name, String val) {
        editor.putString(name, val);
        editor.commit();
    }

    public void savePrefs(String name, Boolean val) {
        editor.putBoolean(name, val);
        editor.commit();
    }

    /**
     * get sharedPreferences "settings"
     *
     * @param name
     * @param defVal
     * @return
     */
    public Boolean getPrefs(String name, Boolean defVal) {
        return pref.getBoolean(name, defVal);
    }

    public String getPrefs(String name, String defVal) {
        return pref.getString(name, defVal);
    }

    private void getSignature() {
        Log.i("hmac", "getSignature: ini");
        //ApiSecurity
        ApiSecurity api = new ApiSecurity();
        String action = kUrl.replaceAll("/$", "") + "/status/edit";
        Log.i("hmac", "getSignature: "+ action);

        api.setSecurity(action, kApi);
        String hash = api.getHash();
        toastMessage("hash: " + hash);

    }

    public static boolean isEmptyString(String text) {
        boolean b = false;
        if (text == null || text.trim().equals("null") || text.trim().length() <= 0) {
            b = true;
        }
        return b;
    }
}