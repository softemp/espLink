package fatima.tech.espnetsmart;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class Util {
    public static boolean optionMenuActions(Context cntx, int id) {
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask((Activity) cntx);
                return true;
            case R.id.action_appsettings /* 2131624268 */:
                Intent i = new Intent(cntx, (Class<?>) SettingsActivity.class);
                cntx.startActivity(i);
                return true;
            case R.id.action_rateus /* 2131624269 */:
                cntx.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + cntx.getPackageName())));
                return true;
            case R.id.action_share /* 2131624270 */:
                int applicationNameId = cntx.getApplicationInfo().labelRes;
                String appPackageName = cntx.getPackageName();
                Intent intentShare = new Intent("android.intent.action.SEND");
                intentShare.setType("text/plain");
                intentShare.putExtra("android.intent.extra.SUBJECT", cntx.getString(applicationNameId));
                String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                intentShare.putExtra("android.intent.extra.TEXT", "Install ESPLink App from Play Store and start controlling your home appliances using your smartphone\n" + link);
                try {
                    cntx.startActivity(Intent.createChooser(intentShare, "Share ESPLink App"));
                } catch (ActivityNotFoundException e) {
                }
                return true;
            case R.id.action_feedback /* 2131624271 */:
                Intent intentFeedback = new Intent(cntx, (Class<?>) ContactUsActivity.class);
                cntx.startActivity(intentFeedback);
                return true;
            case R.id.action_help /* 2131624272 */:
                Intent intentHelp = new Intent(cntx, (Class<?>) HelpActivity.class);
                cntx.startActivity(intentHelp);
                return true;
            case R.id.action_about /* 2131624273 */:
                Intent intentAbout = new Intent(cntx, (Class<?>) AboutActivity.class);
                cntx.startActivity(intentAbout);
                return true;
            default:
                return true;
        }
    }

    public static void coloringOptionMenuItem(Context cntx, Menu menu) {
        int itmNo;
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
            }
        }
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) cntx).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        float logicalDensity = metrics.density;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int dp = (int) (((double) (width / logicalDensity)) + 0.5d);
        if (dp < 360) {
            itmNo = 1;
        } else if (dp < 360 || dp >= 500) {
            itmNo = (dp < 500 || dp >= 600) ? 4 : 3;
        } else {
            itmNo = 2;
        }
        for (int i = 0; i < itmNo; i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ResourcesCompat.getColor(cntx.getResources(), R.color.colorWhite, null), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static Snackbar colorSnackBar(Context cntx, Snackbar snackbar, int bkgColor) {
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        snackbarView.setBackgroundColor(ResourcesCompat.getColor(cntx.getResources(), bkgColor, null));
        snackbarView.setElevation(30.0f);
        textView.setTextColor(ResourcesCompat.getColor(cntx.getResources(), R.color.md_white_1000, null));
        return snackbar;
    }

    public static String calculateRemTime(Context cntx, int btnS, long offTimeSec, long onTimeSec) {
        long remTime;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(11);
        int minute = calendar.get(12);
        int second = calendar.get(13);
        int c = (hour * 3600) + (minute * 60) + second;
        if (btnS == 1) {
            remTime = ((long) c) < offTimeSec ? offTimeSec - ((long) c) : (offTimeSec - ((long) c)) + ((long) 86400);
        } else if (c >= onTimeSec) {
            remTime = (onTimeSec - ((long) c)) + ((long) 86400);
        } else {
            remTime = onTimeSec - ((long) c);
        }
        return DateUtils.formatElapsedTime(remTime);
    }

    public static String calculateRunTime(Context cntx, long runT, boolean longFormat) {
        TimeZone timezone = TimeZone.getDefault();
        int tzOffset = timezone.getRawOffset();
        long offsetT = loadLongPref(cntx, "offsetT", 0L);
        long currentMilliSeconds = System.currentTimeMillis();
        long millis = ((((long) tzOffset) + currentMilliSeconds) - offsetT) - runT;
        int s = (int) (millis / 1000);
        int m = s / 60;
        int h = m / 60;
        int d = h / 24;
        int seconds = s % 60;
        int minutes = m % 60;
        int hours = h % 24;
        int days = d % 365;
        String[] units = new String[4];
        units[0] = " day";
        units[1] = " hour";
        units[2] = longFormat ? " minute" : " min";
        units[3] = longFormat ? " second" : " sec";
        ArrayList<String> timeArray = new ArrayList<>();
        if (days > 0) {
            timeArray.add(String.valueOf(days) + units[0]);
        }
        if (hours > 0) {
            timeArray.add(String.valueOf(hours) + units[1]);
        }
        if (minutes > 0) {
            timeArray.add(String.valueOf(minutes) + units[2]);
        }
        if (seconds > 0) {
            timeArray.add(String.valueOf(seconds) + units[3]);
        }
        String time = "";
        for (int i = 0; i < timeArray.size(); i++) {
            time = time + timeArray.get(i);
            if (i != timeArray.size() - 1) {
                time = time + ", ";
            }
        }
        return time.isEmpty() ? "0 sec" : time;
    }

    public static SpannableString partlyBoldandColoredText(Context cntx, String boldText, String normalText, int color) {
        SpannableString totalText = new SpannableString(boldText + normalText);
        totalText.setSpan(new StyleSpan(1), 0, boldText.length(), 33);
        totalText.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(cntx.getResources(), color, null)), 0, boldText.length(), 33);
        return totalText;
    }

    public static SpannableString partlyColoredText(Context cntx, String colorText, String normalText, int color) {
        SpannableString totalText = new SpannableString(colorText + normalText);
        totalText.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(cntx.getResources(), color, null)), 0, colorText.length(), 33);
        return totalText;
    }

    public static String currTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        return date.format(currentLocalTime);
    }

    public static void saveStringPref(Context cntx, String key, String val) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, val);
        prefEditor.apply();
    }

    public static String loadStringPref(Context cntx, String key, String defVal) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        return pref != null ? pref.getString(key, defVal) : defVal;
    }

    public static void saveLongPref(Context cntx, String key, long val) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putLong(key, val);
        prefEditor.apply();
    }

    public static long loadLongPref(Context cntx, String key, long defVal) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        return pref != null ? pref.getLong(key, defVal) : defVal;
    }

    public static void saveIntPref(Context cntx, String key, int val) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, val);
        prefEditor.apply();
    }

    public static int loadIntPref(Context cntx, String key, int defVal) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        return pref != null ? pref.getInt(key, defVal) : defVal;
    }

    public static void saveBooleanPref(Context cntx, String key, Boolean val) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean(key, val.booleanValue());
        prefEditor.apply();
    }

    public static Boolean loadBooleanPref(Context cntx, String key, Boolean defVal) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cntx);
        return pref != null ? Boolean.valueOf(pref.getBoolean(key, defVal.booleanValue())) : defVal;
    }

    public static String convertFromUnix(long unix_time) throws IllegalArgumentException, NullPointerException {
        TimeZone timezone = TimeZone.getDefault();
        int tzOffset = timezone.getRawOffset();
        long inTime = unix_time - ((long) tzOffset);
        Date date = new Date(inTime);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss aa", Locale.US);
        String result = format.format(date);
        return result;
    }
}
