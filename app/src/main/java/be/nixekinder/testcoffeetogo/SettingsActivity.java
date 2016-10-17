package be.nixekinder.testcoffeetogo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    public Button save_button;
    public Button edit_button;
    public EditText known_username;
    public EditText known_url;
    public EditText known_api;
    EditText known_username_f, inputTextField;
    HashMap<String, Color> listener;
    ArrayList<HashMap<String, String>> servicelist;
    private String kUsername;
    private String kUrl;
    private String kApi;
    private String kAction;
    private boolean prefsSaved;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String kHash;
    private Color bgcolor;
    private ListView lv;
    private String TAG = SettingsActivity.class.getSimpleName();

    public static boolean isEmptyString(String text) {
        boolean b = false;
        if (text == null || text.trim().equals("null") || text.trim().length() <= 0) {
            b = true;
        }
        return b;
    }

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

        known_username_f = (EditText) findViewById(R.id.k_username_f);
        known_username_f.setText(kUsername);

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

        edit_button = (Button) findViewById(R.id.set_edit);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_button.setEnabled(true);
                LinearLayout lv = (LinearLayout) findViewById(R.id.root_et);
                for (int i = 0, count = lv.getChildCount(); i < count; ++i) {
                    View v = lv.getChildAt(i);
                    if (v instanceof EditText) {
                        EditText e = (EditText) v;

                        enableEditText(e);
                    }
                }

            }
        });


        servicelist = new ArrayList<>();
        Set setServiceList = getPrefsSet("setServiceList");
        List sl = new ArrayList(setServiceList);
        servicelist.addAll(sl);
        lv = (ListView) findViewById(R.id.services_list);
        if (servicelist.isEmpty() == false) {
            setServicelist();
        }

        prefsSaved = getPrefs("savedPrefs", false);
        if (prefsSaved) {
            save_button.setEnabled(false);
            LinearLayout lv = (LinearLayout) findViewById(R.id.root_et);
            for (int i = 0, count = lv.getChildCount(); i < count; ++i) {
                View v = lv.getChildAt(i);
                if (v instanceof EditText) {
                    EditText e = (EditText) v;
                    int id = e.getId();
                    String ids = Integer.toString(id);
                    Log.i(TAG, "EditText ID: " + id + " " + e.getKeyListener());
                    /*if (e.getBackground() != null && isEmptyString(ids)==false) {
                      //  listener.add(ids, e.getKeyListener());
                    }*/
                    disableEditText(e);
                }
            }
        }
    }

    private void saveSettings() {
        boolean b = true;
        String toastMsg = "";
        if (isEmptyString(known_username.getText().toString())) {
            toastMsg = getString(R.string.empty_username);
            b = b & false;
        } else {
            savePrefs("knownUsername", known_username.getText().toString());
            kUsername = getPrefs("knownUsername", "");
            Log.i("settings", "saveSettings: " + known_username.getText().toString());
            b = b & true;
        }
        if (isEmptyString(known_url.getText().toString())) {
            toastMsg = String.valueOf(R.string.empty_url);
            b = b & false;
        } else {
            savePrefs("knownUrl", known_url.getText().toString());
            savePrefs("knownAction", "/status/edit");
            Log.i("settings", "saveSettings: " + known_url.getText().toString());
            kUrl = getPrefs("knownUrl", "");
            kAction = getPrefs("knownAction", "/status/edit");
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
            kApi = getPrefs("knownApi", "");
        }
        if (b) {
            toastMsg = getString(R.string.all_saved);
            savePrefs("savedPrefs", b);
            getSignature();
            getKnown();
            save_button.setEnabled(false);
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

    public void savePrefsSet(String name, Set val) {
        editor.putStringSet(name, val);
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

    public Set getPrefsSet(String name) {
        return pref.getStringSet(name, new HashSet<String>());
    }

    ;

    private void getSignature() {

        ApiSecurity api = new ApiSecurity();
        String action = kUrl.replaceAll("/$", "") + "/status/edit";
        kAction = action;
        action = "/status/edit";

        api.setSecurity(action, kApi);
        String hash = api.getHash();
        if (isEmptyString(hash)) {
            toastMessage(getString(R.string.empty_hash));
        } else {
            savePrefs("knownHash", hash);
            kHash = hash;
            Log.i("HASH", "getSignature: " + hash);
        }

    }

    private void getKnown() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            toastMessage(getString(R.string.networkOk));
            new PostAsync().execute("user", "password");
        } else {
            toastMessage(getString(R.string.noNetwork));
        }

    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        //  editText.setKeyListener(null);
        //   editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusable((true));
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        int id = editText.getId();
        String ids = Integer.toString(id);
        Log.i(TAG, "enableEditText: " + ids);
        // editText.setKeyListener(listener.get(ids));
    }

    private void setServicelist() {
        ListAdapter adapter = new SimpleAdapter(
                SettingsActivity.this, servicelist, R.layout.services_list, new String[]{"service_name"}, new int[]{R.id.service_name});
        lv.setAdapter(adapter);
        Set foo = new HashSet(servicelist);
        savePrefsSet("setServiceList", foo);
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        String inputText = known_username_f.getText().toString();

        DialogFragment newFragment = MyDialogFragment.newInstance(inputText, inputTextField);
        newFragment.show(ft, "dialog");
    }

    public static class MyDialogFragment extends DialogFragment {

        static EditText backTextField;
        String mText;

        static MyDialogFragment newInstance(String text, EditText editText) {
            backTextField = editText;
            MyDialogFragment f = new MyDialogFragment();

            Bundle args = new Bundle();
            args.putString("text", text);
            f.setArguments(args);

            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mText = getArguments().getString("text");

            //create custom LinearLayout prgr
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText editField = new EditText(getActivity());
            editField.setHint(getText(R.string.username));
            Button btnAccept = new Button(getActivity());
            btnAccept.setText(getText(R.string.set_save));
            final EditText textField = new EditText(getActivity());
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textField.setText(editField.getText().toString());
                }
            });

            layout.addView(editField);
            layout.addView(btnAccept);
            layout.addView(textField);

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.alert_dialog))
                    .setMessage(mText)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(getActivity(), textField.getText(), Toast.LENGTH_SHORT).show();
                            backTextField.setText(textField.getText());
                        }
                    }).setNegativeButton(getText(R.string.cancel), null).setView(layout).create();
        }

    }

    class PostAsync extends AsyncTask<String, String, JSONObject> {


        private static final String TAG_SUCCESS = "location";
        private static final String TAG_MESSAGE = "user";
        JSONParser jsonParser = new JSONParser();
        String url = getPrefs("knownUrl", "");
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(SettingsActivity.this);
            pDialog.setMessage(getString(R.string.retrievedata));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();
                params.put("name", args[0]);
                params.put("password", args[1]);

                Log.d("request", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        kAction, "GET", params, kUsername, kHash);

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(JSONObject json) {

            int success = 1;
            String message = "";

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {

                try {

//                    JSONArray aServices = json.getJSONArray("services");
                    JSONObject oServices = json.getJSONObject("services");
                    servicelist.clear();

                    for (int i = 0; i < oServices.length(); i++) {
                        String key = oServices.names().getString(i);
                        JSONArray arrJ = oServices.getJSONArray(key);
                        JSONObject value = arrJ.getJSONObject(0);
                        String username = value.getString("username");
                        String name = value.getString("name");
                        String service = key + " (" + name + ")";

                        HashMap<String, String> sl = new HashMap<>();
                        sl.put("service_name", service);
                        servicelist.add(sl);
                        Log.e("JSO", "Key = " + oServices.names().getString(i) + " value = " + oServices.get(oServices.names().getString(i)));

                    }
                    setServicelist();
                    Log.i("Json", "onPostExecute: service " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }


}