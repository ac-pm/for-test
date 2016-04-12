package mobi.acpm.example;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import mobi.acpm.example.http.HttpUtil;

public class WebActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        TextView txtName = (TextView) findViewById(R.id.txtWebName);

        mPrefs = getSharedPreferences(HttpUtil.HTTP_PREFS, MODE_PRIVATE);

        String error = mPrefs.getString("Error","");

        if(error.equals("")){
            txtName.setText("Login: "+mPrefs.getString("name", ""));
            txtName.setTextColor(Color.BLUE);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.setWebChromeClient(new WebChromeClient());

            webView.loadUrl("http://acpm.mobi/");

        }else {
            txtName.setText(error);
            txtName.setTextColor(Color.RED);
        }
    }
}
