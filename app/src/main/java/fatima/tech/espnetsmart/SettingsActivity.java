package fatima.tech.espnetsmart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static Context mContext;
    private static Menu menuAction;
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() { // from class: fatima.tech.espnetsmart.SettingsActivity.1
        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                return true;
            }
            if (preference instanceof EditTextPreference) {
                String key = preference.getKey();
                if (key.equalsIgnoreCase("tank_capacity")) {
                    preference.setSummary("Total volume of tank: " + stringValue + " Liter");
                    return true;
                }
                if (key.equalsIgnoreCase("pressure_sensor_low") || preference.getKey().equalsIgnoreCase("pressure_sensor_high")) {
                    preference.setSummary("Pressure sensor reading: " + stringValue);
                    return true;
                }
                if (key.equalsIgnoreCase("pump_auto_start_level") || preference.getKey().equalsIgnoreCase("pressure_sensor_high")) {
                    preference.setSummary("Pump auto start water level: " + stringValue + " %");
                    return true;
                }
                if (key.equalsIgnoreCase("light_cutoff") || preference.getKey().equalsIgnoreCase("light_cuton")) {
                    preference.setSummary("at sunlight sensor reading of " + stringValue + " Lux");
                    return true;
                }
                if (key.equalsIgnoreCase("wifiPass")) {
                    preference.setSummary(SettingsActivity.passHide(stringValue.length()));
                    return true;
                }
                preference.setSummary(stringValue);
                return true;
            }
            preference.setSummary(stringValue);
            return true;
        }
    };
    Button bt1;
    Button bt2;
    private WebSocketClient mWebSocketClient;
    Handler timerTask;
    View view;
    int refCounter = 0;
    boolean manCloseSock = false;
    boolean isRefreshing = false;
    private Runnable socketConnTimerTaskFun = new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.4
        @Override // java.lang.Runnable
        public void run() {
            SettingsActivity.this.timerTask.postDelayed(this, 1000L);
            if (SettingsActivity.this.refCounter == 0) {
                SettingsActivity.this.MySnackBar("Establishing Websocket (IP:" + Util.loadStringPref(SettingsActivity.this.getBaseContext(), "ServIP", SettingsActivity.this.getString(R.string.ServIP)) + ", PORT:" + Util.loadStringPref(SettingsActivity.this.getBaseContext(), "sockPort", SettingsActivity.this.getString(R.string.sockPort)) + ")", -2, R.color.colorBlue, new Object[0]);
                SettingsActivity.this.connectWebSocket();
            }
            if (SettingsActivity.this.refCounter % 15 == 0 && SettingsActivity.this.refCounter > 0) {
                SettingsActivity.this.connectWebSocket();
            }
            SettingsActivity.this.refCounter++;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static String passHide(int len) {
        String pass = "";
        for (int i = 0; i < len; i++) {
            pass = pass + "*";
        }
        return pass;
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override // fatima.tech.espnetsmart.AppCompatPreferenceActivity, android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.timerTask = new Handler();
        LayoutInflater inflater = LayoutInflater.from(this);
        this.view = inflater.inflate(R.layout.setting_activity_footer, (ViewGroup) null);
        setListFooter(this.view);
        setupActionBar();
        this.bt1 = (Button) this.view.findViewById(R.id.button_update_device_clock);
        this.bt1.setOnClickListener(new View.OnClickListener() { // from class: fatima.tech.espnetsmart.SettingsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int year = calendar.get(1);
                int month = calendar.get(2);
                int date = calendar.get(5);
                int hour = calendar.get(11);
                int minute = calendar.get(12);
                int second = calendar.get(13);
                int dayOfWeek = calendar.get(7);
                TimeZone timezone = TimeZone.getDefault();
                int tzOffset = timezone.getRawOffset();
                long unixTime = (System.currentTimeMillis() + ((long) tzOffset)) / 1000;
                JSONObject jsonRoot = new JSONObject();
                try {
                    jsonRoot.put("action", "setTime");
                    jsonRoot.put("year", year % 100);
                    jsonRoot.put("month", month + 1);
                    jsonRoot.put("date", date);
                    jsonRoot.put("hour", hour);
                    jsonRoot.put("minute", minute);
                    jsonRoot.put("second", second);
                    jsonRoot.put("dayOfWeek", dayOfWeek == 1 ? 7 : dayOfWeek - 1);
                    jsonRoot.put("unixTime", unixTime);
                    if (SettingsActivity.this.mWebSocketClient != null) {
                        SettingsActivity.this.sendData(jsonRoot);
                        SettingsActivity.this.bt1.postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (SettingsActivity.this.isRefreshing) {
                                    SettingsActivity.this.bt1.getBackground().setColorFilter(ResourcesCompat.getColor(SettingsActivity.this.getResources(), R.color.md_red_500, null), PorterDuff.Mode.SRC_ATOP);
                                    SettingsActivity.this.MySnackBar("Device time update failed", 0, R.color.colorRed, new Object[0]);
                                }
                            }
                        }, 5000L);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.bt1.setEnabled(false);
        this.bt1.getBackground().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.md_grey_500, null), PorterDuff.Mode.SRC_ATOP);
        this.bt2 = (Button) this.view.findViewById(R.id.button_sync_device_settings);
        this.bt2.setOnClickListener(new View.OnClickListener() { // from class: fatima.tech.espnetsmart.SettingsActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                JSONObject jsonRoot = new JSONObject();
                try {
                    jsonRoot.put("action", "setSett");
                    jsonRoot.put("wifiSSID", Util.loadStringPref(SettingsActivity.mContext, "wifiSSID", SettingsActivity.this.getString(R.string.wifiSSID)).trim());
                    jsonRoot.put("wifiPass", Util.loadStringPref(SettingsActivity.mContext, "wifiPass", SettingsActivity.this.getString(R.string.wifiPass)).trim());
                    jsonRoot.put("ServIP", Util.loadStringPref(SettingsActivity.mContext, "ServIP", SettingsActivity.this.getString(R.string.ServIP)).trim());
                    jsonRoot.put("httpPort", Util.loadStringPref(SettingsActivity.mContext, "httpPort", SettingsActivity.this.getString(R.string.httpPort)).trim());
                    jsonRoot.put("sockPort", Util.loadStringPref(SettingsActivity.mContext, "sockPort", SettingsActivity.this.getString(R.string.sockPort)).trim());
                    jsonRoot.put("mdnsName", Util.loadStringPref(SettingsActivity.mContext, "mdnsName", SettingsActivity.this.getString(R.string.mdnsName)).trim());
                    jsonRoot.put("tnkFVol", Util.loadStringPref(SettingsActivity.mContext, "tank_capacity", "500").trim());
                    jsonRoot.put("tnkECal", Util.loadStringPref(SettingsActivity.mContext, "pressure_sensor_low", "10").trim());
                    jsonRoot.put("tnkFCal", Util.loadStringPref(SettingsActivity.mContext, "pressure_sensor_high", "1000").trim());
                    jsonRoot.put("pumASta", Util.loadStringPref(SettingsActivity.mContext, "pump_auto_start_level", "80").trim());
                    jsonRoot.put("battR", Util.loadStringPref(SettingsActivity.mContext, "battery_rating_list", "12").trim());
                    jsonRoot.put("mainR", Util.loadStringPref(SettingsActivity.mContext, "ac_power_rating_list", "220").trim());
                    jsonRoot.put("luxOffCal", Util.loadStringPref(SettingsActivity.mContext, "light_cutoff", "10").trim());
                    jsonRoot.put("luxOnCal", Util.loadStringPref(SettingsActivity.mContext, "light_cuton", "20").trim());
                    if (SettingsActivity.this.mWebSocketClient != null) {
                        SettingsActivity.this.sendData(jsonRoot);
                        Util.saveBooleanPref(SettingsActivity.mContext, "unSaveSett", true);
                        SettingsActivity.this.bt2.postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (SettingsActivity.this.isRefreshing) {
                                    SettingsActivity.this.bt2.getBackground().setColorFilter(ResourcesCompat.getColor(SettingsActivity.this.getResources(), R.color.md_red_500, null), PorterDuff.Mode.SRC_ATOP);
                                    SettingsActivity.this.MySnackBar("Device setting upload failed", 0, R.color.colorRed, new Object[0]);
                                }
                            }
                        }, 5000L);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        this.bt2.setEnabled(false);
        this.bt2.getBackground().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.md_grey_500, null), PorterDuff.Mode.SRC_ATOP);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectWebSocket() {
        if (this.mWebSocketClient != null) {
            if (this.mWebSocketClient.isOpen()) {
                this.mWebSocketClient.close();
            }
            this.mWebSocketClient = null;
        }
        try {
            String SERVER_IP = getString(R.string.ServIP);
            String SERVER_PORT = getString(R.string.sockPort);
            String SERVER_URL = "ws://" + Util.loadStringPref(this, "ServIP", SERVER_IP) + ":" + Util.loadStringPref(this, "sockPort", SERVER_PORT);
            URI uri = new URI(SERVER_URL);
            this.mWebSocketClient = new WebSocketClient(uri) { // from class: fatima.tech.espnetsmart.SettingsActivity.5
                @Override // org.java_websocket.client.WebSocketClient
                public void onOpen(ServerHandshake serverHandshake) {
                    SettingsActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SettingsActivity.this.bt1.setEnabled(true);
                            SettingsActivity.this.bt1.getBackground().setColorFilter(ResourcesCompat.getColor(SettingsActivity.this.getResources(), R.color.md_blue_500, null), PorterDuff.Mode.SRC_ATOP);
                            SettingsActivity.this.bt2.setEnabled(true);
                            SettingsActivity.this.bt2.getBackground().setColorFilter(ResourcesCompat.getColor(SettingsActivity.this.getResources(), Util.loadBooleanPref(SettingsActivity.mContext, "unSaveSett", false).booleanValue() ? R.color.md_red_500 : R.color.md_green_500, null), PorterDuff.Mode.SRC_ATOP);
                            SettingsActivity.this.timerTask.removeCallbacks(SettingsActivity.this.socketConnTimerTaskFun);
                            SettingsActivity.this.refCounter = 0;
                            SettingsActivity.this.MySnackBar("Websocket connected", 0, R.color.colorGreen, new Object[0]);
                        }
                    });
                }

                @Override // org.java_websocket.client.WebSocketClient
                public void onClose(int i, String s, boolean b) {
                    SettingsActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.5.2
                        @Override // java.lang.Runnable
                        public void run() {
                            SettingsActivity.this.bt1.setEnabled(false);
                            SettingsActivity.this.bt2.setEnabled(false);
                            if (!SettingsActivity.this.manCloseSock) {
                                SettingsActivity.this.timerTask.removeCallbacks(SettingsActivity.this.socketConnTimerTaskFun);
                                SettingsActivity.this.timerTask.postDelayed(SettingsActivity.this.socketConnTimerTaskFun, 0L);
                            }
                            SettingsActivity.this.MySnackBar("Websocket Disconnected", -2, R.color.colorRed, new Object[0]);
                        }
                    });
                }

                @Override // org.java_websocket.client.WebSocketClient
                public void onMessage(final String s) {
                    SettingsActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.5.3
                        @Override // java.lang.Runnable
                        public void run() {
                            SettingsActivity.this.processData(s);
                        }
                    });
                }

                @Override // org.java_websocket.client.WebSocketClient
                public void onError(Exception e) {
                }
            };
            this.mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processData(String inMessage) {
        int startChar = inMessage.indexOf("<");
        int stopChar = inMessage.lastIndexOf(">");
        if (startChar != -1 && stopChar != -1) {
            try {
                JSONObject jsonRoot = new JSONObject(inMessage.substring(startChar + 1, stopChar));
                if (jsonRoot.has("action")) {
                    this.isRefreshing = false;
                    circleToolbarProgress(false);
                    String action = jsonRoot.getString("action");
                    switch (action) {
                        case "setTime":
                            long inTime = jsonRoot.getInt("currTime");
                            long currentMilliSeconds = System.currentTimeMillis();
                            TimeZone timezone = TimeZone.getDefault();
                            int tzOffset = timezone.getRawOffset();
                            long offsetT = (currentMilliSeconds - (1000 * inTime)) + ((long) tzOffset);
                            Util.saveLongPref(getBaseContext(), "offsetT", offsetT);
                            this.bt1.getBackground().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.md_blue_500, null), PorterDuff.Mode.SRC_ATOP);
                            MySnackBar("Device time updated successfully", 0, R.color.colorGreen, new Object[0]);
                            break;
                        case "resetSett":
                            MySnackBar("Device default settings restored!!\nRestarting device with default settings", 0, R.color.colorGreen, new Object[0]);
                            break;
                        case "setSett":
                            Util.saveStringPref(getBaseContext(), "wifiSSID", jsonRoot.getString("wifiSSID"));
                            Util.saveStringPref(getBaseContext(), "wifiPass", jsonRoot.getString("wifiPass"));
                            Util.saveStringPref(getBaseContext(), "ServIP", jsonRoot.getString("ServIP"));
                            Util.saveStringPref(getBaseContext(), "httpPort", String.valueOf(jsonRoot.getInt("httpPort")));
                            Util.saveStringPref(getBaseContext(), "sockPort", String.valueOf(jsonRoot.getInt("sockPort")));
                            Util.saveStringPref(getBaseContext(), "mdnsName", jsonRoot.getString("mdnsName"));
                            Util.saveStringPref(getBaseContext(), "tank_capacity", String.valueOf(jsonRoot.getInt("tnkFVol")));
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_low", String.valueOf(jsonRoot.getInt("tnkECal")));
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_high", String.valueOf(jsonRoot.getInt("tnkFCal")));
                            Util.saveStringPref(getBaseContext(), "pump_auto_start_level", String.valueOf(jsonRoot.getInt("pumASta")));
                            Util.saveStringPref(getBaseContext(), "battery_rating_list", String.valueOf(jsonRoot.getInt("battR")));
                            Util.saveStringPref(getBaseContext(), "ac_power_rating_list", String.valueOf(jsonRoot.getInt("mainR")));
                            Util.saveStringPref(getBaseContext(), "light_cutoff", String.valueOf(jsonRoot.getInt("luxOffCal")));
                            Util.saveStringPref(getBaseContext(), "light_cuton", String.valueOf(jsonRoot.getInt("luxOnCal")));
                            Util.saveBooleanPref(mContext, "unSaveSett", false);
                            this.bt2.getBackground().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.md_green_500, null), PorterDuff.Mode.SRC_ATOP);
                            MySnackBar("Device settings uploaded successfully\nRestarting system with new configuration", 0, R.color.colorGreen, new Object[0]);
                            break;
                        case "getSett":
                            Util.saveStringPref(getBaseContext(), "wifiSSID", jsonRoot.getString("wifiSSID"));
                            Util.saveStringPref(getBaseContext(), "wifiPass", jsonRoot.getString("wifiPass"));
                            Util.saveStringPref(getBaseContext(), "ServIP", jsonRoot.getString("ServIP"));
                            Util.saveStringPref(getBaseContext(), "httpPort", String.valueOf(jsonRoot.getInt("httpPort")));
                            Util.saveStringPref(getBaseContext(), "sockPort", String.valueOf(jsonRoot.getInt("sockPort")));
                            Util.saveStringPref(getBaseContext(), "mdnsName", jsonRoot.getString("mdnsName"));
                            Util.saveStringPref(getBaseContext(), "serMode", jsonRoot.getString("serMode"));
                            Util.saveStringPref(getBaseContext(), "heap", String.valueOf(jsonRoot.getInt("heap")));
                            Util.saveStringPref(getBaseContext(), "tank_capacity", String.valueOf(jsonRoot.getInt("tnkFVol")));
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_low", String.valueOf(jsonRoot.getInt("tnkECal")));
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_high", String.valueOf(jsonRoot.getInt("tnkFCal")));
                            Util.saveStringPref(getBaseContext(), "pump_auto_start_level", String.valueOf(jsonRoot.getInt("pumASta")));
                            Util.saveStringPref(getBaseContext(), "battery_rating_list", String.valueOf(jsonRoot.getInt("battR")));
                            Util.saveStringPref(getBaseContext(), "ac_power_rating_list", String.valueOf(jsonRoot.getInt("mainR")));
                            Util.saveStringPref(getBaseContext(), "light_cutoff", String.valueOf(jsonRoot.getInt("luxOffCal")));
                            Util.saveStringPref(getBaseContext(), "light_cuton", String.valueOf(jsonRoot.getInt("luxOnCal")));
                            long inTime2 = jsonRoot.getInt("currTime");
                            long currentMilliSeconds2 = System.currentTimeMillis();
                            TimeZone timezone2 = TimeZone.getDefault();
                            int tzOffset2 = timezone2.getRawOffset();
                            long offsetT2 = (currentMilliSeconds2 - (1000 * inTime2)) + ((long) tzOffset2);
                            Util.saveLongPref(getBaseContext(), "offsetT", offsetT2);
                            MySnackBar("Device settings downloaded successfully", 0, R.color.colorGreen, new Object[0]);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        this.manCloseSock = false;
        this.refCounter = 0;
        this.timerTask.removeCallbacks(this.socketConnTimerTaskFun);
        this.timerTask.postDelayed(this.socketConnTimerTaskFun, 0L);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        this.manCloseSock = true;
        if (this.mWebSocketClient != null) {
            if (this.mWebSocketClient.isOpen()) {
                this.mWebSocketClient.close();
            }
            this.mWebSocketClient = null;
        }
    }

    private void setupActionBar() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_activity_toolbar, root, false);
        root.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        menuAction = menu;
        Util.coloringOptionMenuItem(this, menu);
        return true;
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.optionMenuActions(this, item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_refresh /* 2131624274 */:
                refreshButtonClick();
                return true;
            case R.id.action_format /* 2131624275 */:
                restoreSettingsButtonClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void restoreSettingsButtonClick() {
        JSONObject jsonRoot = new JSONObject();
        try {
            jsonRoot.put("action", "resetSett");
            if (this.mWebSocketClient != null && this.mWebSocketClient.isOpen()) {
                circleToolbarProgress(true);
                this.mWebSocketClient.send("<" + jsonRoot.toString() + ">");
            }
        } catch (JSONException e) {
        }
        new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.6
            @Override // java.lang.Runnable
            public void run() {
                if (SettingsActivity.this.isRefreshing) {
                    SettingsActivity.this.MySnackBar("Restoring device default setting failed", 0, R.color.colorRed, new Object[0]);
                }
            }
        }, 5000L);
    }

    public void refreshButtonClick() {
        JSONObject jsonRoot = new JSONObject();
        try {
            jsonRoot.put("action", "getSett");
            if (this.mWebSocketClient != null && this.mWebSocketClient.isOpen()) {
                circleToolbarProgress(true);
                this.mWebSocketClient.send("<" + jsonRoot.toString() + ">");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.7
            @Override // java.lang.Runnable
            public void run() {
                if (SettingsActivity.this.isRefreshing) {
                    SettingsActivity.this.MySnackBar("Device Setting download failed", 0, R.color.colorRed, new Object[0]);
                }
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendData(JSONObject jsonRoot) {
        circleToolbarProgress(true);
        this.isRefreshing = true;
        String outData = jsonRoot.toString();
        if (this.mWebSocketClient != null && this.mWebSocketClient.isOpen()) {
            this.mWebSocketClient.send("<" + outData + ">");
        }
    }

    public void circleToolbarProgress(Boolean showHide) {
        if (menuAction != null) {
            final MenuItem menuItem = menuAction.findItem(R.id.action_refresh);
            if (showHide.booleanValue()) {
                menuItem.setActionView(R.layout.circle_progress);
                this.isRefreshing = true;
                new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.SettingsActivity.8
                    @Override // java.lang.Runnable
                    public void run() {
                        SettingsActivity.this.isRefreshing = false;
                        menuItem.setActionView((View) null);
                    }
                }, 7000L);
                return;
            }
            menuItem.setActionView((View) null);
        }
    }

    @Override // android.preference.PreferenceActivity
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override // android.preference.PreferenceActivity
    @TargetApi(11)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName) || AppPreferenceFragment.class.getName().equals(fragmentName) || DevicePreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(11)
    public static class AppPreferenceFragment extends PreferenceFragment {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_app_settings);
            setHasOptionsMenu(true);
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pref_themes"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pref_sync_freq"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("switchName1"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("switchName2"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("switchName3"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("switchName4"));
        }

        @Override // android.app.Fragment
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id != 16908332) {
                return super.onOptionsItemSelected(item);
            }
            Util.saveBooleanPref(SettingsActivity.mContext, "unSaveSett", false);
            startActivity(new Intent(getActivity(), (Class<?>) SettingsActivity.class));
            return true;
        }
    }

    @TargetApi(11)
    public static class DevicePreferenceFragment extends PreferenceFragment {
        private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_device_settings);
            setHasOptionsMenu(true);
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("wifiSSID"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("wifiPass"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("ServIP"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("httpPort"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("sockPort"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("mdnsName"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("tank_capacity"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pressure_sensor_low"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pressure_sensor_high"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("pump_auto_start_level"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("battery_rating_list"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("ac_power_rating_list"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("light_cutoff"));
            SettingsActivity.bindPreferenceSummaryToValue(findPreference("light_cuton"));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            this.prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: fatima.tech.espnetsmart.SettingsActivity.DevicePreferenceFragment.1
                @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
                public void onSharedPreferenceChanged(SharedPreferences prefs2, String key) {
                    if (!key.equalsIgnoreCase("unSaveSett")) {
                        Util.saveBooleanPref(SettingsActivity.mContext, "unSaveSett", true);
                    }
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(this.prefListener);
        }

        @Override // android.app.Fragment
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id != 16908332) {
                return super.onOptionsItemSelected(item);
            }
            startActivity(new Intent(getActivity(), (Class<?>) SettingsActivity.class));
            return true;
        }
    }

    void MySnackBar(String txt, int duration, int bkgColor, Object... optParam) {
        Snackbar snackbar = Snackbar.make(this.view, txt, duration);
        Util.colorSnackBar(this, snackbar, bkgColor);
        if (optParam.length > 1) {
            int actionBtnColor = ((Integer) optParam[0]).intValue();
            final String actLabel = (String) optParam[1];
            snackbar.setActionTextColor(ResourcesCompat.getColor(getResources(), actionBtnColor, null));
            snackbar.setAction(actLabel, new View.OnClickListener() { // from class: fatima.tech.espnetsmart.SettingsActivity.9
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    // No APK original os dois casos existem e estão vazios (conferido no bytecode).
                    switch (actLabel) {
                        case "RETRY":
                        case "CONNECT":
                        default:
                            break;
                    }
                }
            });
        }
        snackbar.show();
    }
}
