package mobi.acpm.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class OpenBackupActivity_old extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_backup_activity_old);

        TextView txtInfo = (TextView) findViewById(R.id.txtOpen);
        Backup backup = null;
        try
        {
            FileInputStream fileIn = new FileInputStream(getFilesDir().getAbsolutePath()+"/backup.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            backup = (Backup) in.readObject();
            in.close();
            fileIn.close();
            String info = backup.getEntries();

            txtInfo.setText("From backup: ../files/backup.ser\n\n"+info);

            Log.d("MOBIACPM", "Deserialized.");

        }catch(IOException i)
        {
            i.printStackTrace();
            return;
        }catch(ClassNotFoundException c) {
            Log.d("MOBIACPM", "Backup class not found");
            c.printStackTrace();
            return;
        }
    }
}
