package mobi.acpm.example;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mobi.acpm.example.providers.UsersProvider;
import mobi.acpm.example.util.StringHelper;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);

        Intent intent = getIntent();
        if(intent.hasExtra("email")){
            txtEmail.setText(intent.getStringExtra("email"));
        }

        mPrefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        final Button button = (Button) findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
                TextView txtPassw = (TextView) findViewById(R.id.txtPassword);

                if (txtEmail != null && txtPassw != null) {

                    String passw = StringHelper.computeSha1OfString(txtPassw.getText().toString());
                    // Add a new user
                    ContentValues values = new ContentValues();
                    values.put(UsersProvider.Email, txtEmail.getText().toString());
                    values.put(UsersProvider.Password, passw);
                    getContentResolver().insert(UsersProvider.CONTENT_URI, values);

                    SharedPreferences.Editor edit = mPrefs.edit();
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    edit.putString("last_account", date);
                    edit.apply();

                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button buttonBack = (Button) findViewById(R.id.btnBackLogin);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
