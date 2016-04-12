package mobi.acpm.example.http;

/**
 * Created by acpm on 07/04/16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;

import mobi.acpm.example.WebActivity;


public class HttpUtil {

    public static final int LOGIN = 0;
    public static final int CHANGE = 1;
    public static String HTTP_PREFS = "http_prefs";


    public static void Request(final Context ctx, final String urlp, final String params, final String cookie, final int action) {

        new Thread(new Runnable()

        {
            public void run() {

                try {

                    byte[] postData = params.getBytes(Charset.forName("UTF-8"));
                    int postDataLength = postData.length;

                    URL url = new URL(urlp);

                    TrustManager tm[] = {new PubKeyManager()};

                    SSLContext context = SSLContext.getInstance("TLS");
                    context.init(null, tm, null);

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setSSLSocketFactory(context.getSocketFactory());

                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                    connection.setUseCaches(false);
                    connection.setRequestProperty("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.1.1; Galaxy X Build/JRO03C");
                    if (cookie != null && cookie.trim() != "") {
                        connection.setRequestProperty("Cookie", cookie);
                    }

                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.write(postData);
                    InputStreamReader instream = new InputStreamReader(connection.getInputStream());

                    int statusCode = connection.getResponseCode();

                    if (statusCode == 200) {

                        if (action == LOGIN) {

                            SharedPreferences pref = ctx.getSharedPreferences(HTTP_PREFS, 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();

                            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
                            String nametmp[] = cookies.get(1).trim().split(";");
                            String name = nametmp[0].trim().split("=")[1];

                            String cookie = cookies.get(0).trim().split(";")[0];

                            editor.putString("session", cookie);
                            editor.putString("name", name);
                            editor.putString("Error", "");

                            editor.commit();

                            Intent intent = new Intent(ctx, WebActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        }
                    }

                } catch (SSLHandshakeException ex){

                    SharedPreferences pref = ctx.getSharedPreferences(HTTP_PREFS, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("Error", ex.toString());
                    editor.commit();

                    Log.d("MOBIACPM", ex.toString());

                    Intent intent = new Intent(ctx, WebActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);

                } catch (Exception ex) {

                    if(action==HttpUtil.LOGIN) {

                    }else if(action==HttpUtil.CHANGE)
                    {

                    }
                }
            }

        }).start();
    }
}

