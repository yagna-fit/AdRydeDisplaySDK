package com.rizzo.mediame;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class RootHelper implements IXposedHookLoadPackage {
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        try {
            Log.e("RedClock", "" + lpparam.packageName);

            //XposedBridge.log("Loaded app: " + lpparam.packageName);
            if (!lpparam.packageName.equals("com.android.systemui"))
                return;

            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;
                    String text = tv.getText().toString();
                    tv.setText(text + " :)");
                    tv.setTextColor(Color.RED);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}