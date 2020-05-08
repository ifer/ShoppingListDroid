package ifer.android.shoplist.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.util.AndroidUtils;
import ifer.android.shoplist.util.Constants;


import static ifer.android.shoplist.util.AndroidUtils.*;
import static ifer.android.shoplist.util.GenericUtils.isEmptyOrNull;

public class SettingsActivity extends AppCompatActivity {

    private EditText prefServer;
    private EditText prefUsername;
    private EditText prefPassword;

    private SharedPreferences settings;

    private String initial_server;
    private String initial_username;
    private String initial_password;

    // When run on the emulator, localhost server address is http://10.0.2.2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefServer = (EditText) findViewById(R.id.pref_server);
        prefUsername = (EditText) findViewById(R.id.pref_username);
        prefPassword = (EditText) findViewById(R.id.pref_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadSettings();
    }

    private void loadSettings(){
        settings = getSharedPreferences(Constants.SETTINGS_NAME, 0);
        prefServer.setText(settings.getString(Constants.PrefServerKey, null));
        prefUsername.setText(settings.getString(Constants.PrefUsernameKey, null));
        prefPassword.setText(settings.getString(Constants.PrefPasswordKey, null));

        initial_server = prefServer.getText().toString();
        initial_username = prefUsername.getText().toString();
        initial_password = prefPassword.getText().toString();
    }

    private void saveSettings (){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.PrefServerKey, prefServer.getText().toString());
        editor.putString(Constants.PrefUsernameKey, prefUsername.getText().toString());
        editor.putString(Constants.PrefPasswordKey, prefPassword.getText().toString());
        editor.apply();
    }

    private boolean validateSettings(){
        if(isEmptyOrNull(prefServer.getText().toString()))
            return (false);
        if(isEmptyOrNull(prefUsername.getText().toString()))
            return (false);
        if(isEmptyOrNull(prefPassword.getText().toString()))
            return (false);
        return (true);
    }

    private boolean settingsChanged (){
        if (!prefServer.getText().toString().equals(initial_server))
            return true;
        if (!prefUsername.getText().toString().equals(initial_username))
            return true;
        if (!prefPassword.getText().toString().equals(initial_password))
            return true;

        return (false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings_save:
                if (validateSettings() == false){
                    showToastMessage(this, getString(R.string.error_empty_setting));
                    return true;
                }
                AppController.connectionEstablished = false;
                saveSettings();
                MainActivity.setupConnection(this);
                finish();
                return true;

            case R.id.action_settings_cancel:
                cancelEdit ();
                return true;

            case android.R.id.home:    //make toolbar home button behave like cancel, when in edit mode
                cancelEdit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void cancelEdit (){

        if (settingsChanged()){   //New visit or data changed
            showPopup(this, Popup.WARNING, getString(R.string.warn_not_saved),  new CancelPosAction(), new CancelNegAction());
        }
        else {                                          //Data not changed
            finish();
        }
    }

    //Make android back button behave like cancel, when in edit mode
    @Override
    public void onBackPressed() {
        cancelEdit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    class CancelPosAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    }

    class CancelNegAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    }

}
