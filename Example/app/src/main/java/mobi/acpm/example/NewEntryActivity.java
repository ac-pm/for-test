package mobi.acpm.example;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mobi.acpm.example.crypt.AESCrypt;
import mobi.acpm.example.providers.KeeeyProvider;

public class NewEntryActivity extends AppCompatActivity {

    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        mPrefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        final Button buttonSave = (Button) findViewById(R.id.btnSavePass);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView txtName = (TextView) findViewById(R.id.txtName);
                TextView txtPassw = (TextView) findViewById(R.id.txtPasswd);
                TextView txtConf = (TextView) findViewById(R.id.txtConfirm);

                if (txtName != null && txtConf != null && txtPassw != null) {

                    try {
                        String encrypted = AESCrypt.encrypt(mPrefs.getString("aes_key", ""), txtPassw.getText().toString());

                        // Add a new user
                        ContentValues values = new ContentValues();
                        values.put(KeeeyProvider.Name, txtName.getText().toString());
                        values.put(KeeeyProvider.Password, encrypted);
                        values.put(KeeeyProvider.UserId, mPrefs.getString("active_user", "0"));

                        //Uri uri = Uri.parse("content://" + KeeeyProvider.PROVIDER_NAME + "/keeey");
                        getContentResolver().insert(KeeeyProvider.CONTENT_URI_KEY, values);

                        SharedPreferences.Editor edit = mPrefs.edit();
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        edit.putString("last_entry", date);
                        edit.apply();

                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
