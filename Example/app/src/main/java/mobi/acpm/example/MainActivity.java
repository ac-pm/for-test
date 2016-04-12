package mobi.acpm.example;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mobi.acpm.example.http.HttpUtil;

public class MainActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        final Button buttonNew = (Button) findViewById(R.id.btnNew);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), NewEntryActivity.class);
                startActivity(intent);
            }
        });

        final Button buttonShow = (Button) findViewById(R.id.btnShow);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PasswordsActivity.class);
                startActivity(intent);
            }
        });

        final Button btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://exemplo-cert-pinning.herokuapp.com/login.php";
                String parameters = "name=antonio&pass=pinning";
                HttpUtil.Request(getApplicationContext(), url, parameters, "", HttpUtil.LOGIN);
            }
        });

        final Button btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences http_pref = getSharedPreferences(HttpUtil.HTTP_PREFS, 0);
                SharedPreferences.Editor http_edit = http_pref.edit();
                http_edit.putString("session","");
                http_edit.putString("Error","");
                http_edit.putString("name","");
                http_edit.apply();

                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("active_user", "");
                edit.putString("app_started", "");
                edit.putString("aes_key", "");
                edit.apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
