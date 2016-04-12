package mobi.acpm.xposedlabs;


import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by acpm on 11/11/15.
 */
public class Module implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public static final String PREFS = "XLabSettings";
    private static XSharedPreferences sPrefs;
    public static String TAG = "XposedLabsLog: ";

    public static void loadPrefs() {
        sPrefs = new XSharedPreferences(Module.class.getPackage().getName(), PREFS);
        sPrefs.makeWorldReadable();
    }


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {



        if (loadPackageParam.packageName.equals("mobi.acpm.xposedlabs")) {
            findAndHookMethod("mobi.acpm.xposedlabs.MainActivity", loadPackageParam.classLoader, "isModuleEnabled", XC_MethodReplacement.returnConstant(true));
        }

        loadPrefs();

        if (loadPackageParam.packageName.contains(sPrefs.getString("package","xxxxxx"))) {

            findAndHookMethod(Log.class, "d", String.class, String.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String id = (String) param.args[0];
                    String value = (String) param.args[1];

                    XposedBridge.log(TAG+"ID="+id+" Value="+value);

                }
            });
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }
}
