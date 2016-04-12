package mobi.acpm.xposedlabs;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private SharedPreferences mPrefs;
    private ArrayList<PackageDInfo> mApps;
    private ExpandableListView mExpandableList;
    private List<String> mListDataHeader;
    private HashMap<String, List<PackageDInfo>> mListDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences(Module.PREFS, MODE_WORLD_READABLE);

        boolean b = isModuleEnabled();

        TextView tv = (TextView) findViewById(R.id.txtEnabled);
        tv.setText("Module Disabled!");

        if(b){
            tv.setText("Module Enabled!");
        }


        mExpandableList = (ExpandableListView) findViewById(R.id.expandableListView);
        loadListView();

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        mExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                TextView pkg = (TextView) v.findViewById(R.id.txtListPkg);
                TextView appName = (TextView) v.findViewById(R.id.txtListItem);

                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString("package", pkg.getText().toString());
                edit.commit();

                Toast.makeText(getApplicationContext(), "" + appName.getText().toString(), Toast.LENGTH_SHORT).show();
                loadListView();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<PackageDInfo> getInstalledApps() {
        ArrayList<PackageDInfo> appsList = new ArrayList<>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {

            PackageInfo p = packs.get(i);
            // Installed by user
            if ((p.applicationInfo.flags & 129) == 0) {
                PackageDInfo pInfo = new PackageDInfo();
                pInfo.setAppName(p.applicationInfo.loadLabel(getPackageManager()).toString());
                pInfo.setPckName(p.packageName);
                pInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));

                String pack = mPrefs.getString("package", "");

                if (p.packageName.trim().equals(pack.trim())) {
                    pInfo.setProxied(true);
                }

                appsList.add(pInfo);
            }
        }

        PackageDInfo pInfo = new PackageDInfo();
        pInfo.setAppName("ALL");
        pInfo.setPckName("ALL");
        pInfo.setIcon(getApplicationInfo().loadIcon(getPackageManager()));
        appsList.add(pInfo);
        //Collections.sort(appsList);
        return appsList;
    }

    private void loadListView() {
        mListDataHeader = new ArrayList<String>();
        mListDataHeader.add("Target");

        mListDataChild = new HashMap<String, List<PackageDInfo>>();
        List<PackageDInfo> applications = new ArrayList<PackageDInfo>();

        mApps = getInstalledApps();
        for (int i = 0; i < mApps.size(); i++) {
            applications.add(mApps.get(i));
        }

        ExpandableListView appList = (ExpandableListView) findViewById(R.id.expandableListView);

        mListDataChild.put(mListDataHeader.get(0), mApps);
        appList.setAdapter(new ExpandableListAdapter(this, mListDataHeader, mListDataChild));

    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public boolean isModuleEnabled(){
        return false;
    }
}
