package mobi.acpm.example;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import mobi.acpm.example.crypt.AESCrypt;
import mobi.acpm.example.providers.KeeeyProvider;

public class PasswordsActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    ListView listViewPasswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords);

        listViewPasswd = (ListView) findViewById(R.id.listViewPasswd);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        mPrefs = getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);

        String userid = mPrefs.getString("active_user", "0");
        Cursor cursor = managedQuery(KeeeyProvider.CONTENT_URI_KEY, null, KeeeyProvider.UserId + "='" + userid + "'", null, null);

        //TextView txtpass = (TextView) findViewById(R.id.txtPasswords);

        StringBuilder result = new StringBuilder();
        ArrayList<String> names = new ArrayList<>();
        if(cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(KeeeyProvider.Name));
                    String passwd = cursor.getString(cursor.getColumnIndex(KeeeyProvider.Password));

                    result.append("Name: " + name + "\n" +
                            "Password: " + passwd + "\n\n");


                    names.add(name + "\n"+ passwd);


                } while (cursor.moveToNext());


                Backup backup = new Backup();
                backup.User = userid;
                backup.addEntries(result);

                try
                {
                    FileOutputStream fileOut = new FileOutputStream(this.getFilesDir().getAbsolutePath()+"/backup.ser");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(backup);
                    out.close();
                    fileOut.close();
                    Log.d("MOBIACPM", "Serialized data saved.");

                }catch(IOException i)
                {
                    i.printStackTrace();
                }

                //txtEmail.setText(result.toString()+"Backup saved!");


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, names);

                listViewPasswd.setAdapter(adapter);

            }
        }

        listViewPasswd.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String  itemValue = (String) listViewPasswd.getItemAtPosition(position);

                String encrypted = itemValue.split("\n")[1];

                try {

                    String decrypted = AESCrypt.decrypt(mPrefs.getString("aes_key", ""), encrypted);

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setText(decrypted);

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "" + itemValue.split("\n")[0] + "  copied to clipboard!", Toast.LENGTH_LONG).show();

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
