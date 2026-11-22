package fatima.tech.espnetsmart;

import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends AppCompatActivity implements Tab1.mainActivityListener, Tab2.mainActivityListener, Tab4.mainActivityListener {
    private static final int DAYS_UNTIL_PROMPT = 2;
    private static final String DISABLED = "DISABLED";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final int LAUNCHES_UNTIL_PROMPT = 10;
    private static final int MILLIS_UNTIL_PROMPT = 172800000;
    private static final String PREF_NAME = "APP_RATER";
    private static Menu menuAction;
    WifiReceiver WifiReceiver;
    CollapsingToolbarLayout collapsingToolbar;
    private CoordinatorLayout coordinatorLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView headerImage;
    private DrawerLayout mDrawer;
    private WebSocketClient mWebSocketClient;
    AnimationDrawable myAnimationDrawable;
    private View navHeader;
    private NavigationView navigationView;
    ImageView realTimeUpdImageView;
    TextView realTimeUpdTextView;
    SwipeRefreshLayout swipeContainer;
    private TabLayout tabLayout;
    Handler timerTask;
    private Toolbar toolbar;
    private ViewPager viewPager;
    IntentFilter wifiIntentFilter;
    private int lastTab = 0;
    private Integer[] images = {Integer.valueOf(R.drawable.tab_bkg_1), Integer.valueOf(R.drawable.tab_bkg_2), Integer.valueOf(R.drawable.tab_bkg_3), Integer.valueOf(R.drawable.tab_bkg_4)};
    private int[] primaryColor = {R.color.md_blue_500, R.color.md_green_500, R.color.md_red_500, R.color.md_teal_500};
    private int[] primaryColorDark = {R.color.md_blue_700, R.color.md_green_700, R.color.md_red_700, R.color.md_teal_700};
    boolean isWiFiScan = false;
    boolean manWiFiScan = false;
    boolean manCloseSock = false;
    boolean isWebsocketConn = false;
    boolean isDevSettDownl = false;
    boolean istab4IsReady = false;
    boolean isFirstLoad = true;
    int heartBeat = 0;
    int refreshInt = 30;
    int refCounter = 0;
    int tabDataDownCnt = 1;
    boolean gotUpdated = true;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() { // from class: fatima.tech.espnetsmart.MainActivity.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("NetUtilSnack")) {
                String message = intent.getStringExtra("message");
                int color = intent.getIntExtra("color", R.color.colorBlue);
                MainActivity.this.MySnackBar(message, 0, color, new Object[0]);
            }
        }
    };
    public Runnable wifiConnTimerTaskFun = new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.12
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.timerTask.postDelayed(this, 100L);
            if (MainActivity.this.istab4IsReady) {
                MainActivity.this.timerTask.removeCallbacks(MainActivity.this.wifiConnTimerTaskFun);
                MainActivity.this.startWebSocketClient();
            }
        }
    };
    private Runnable socketConnTimerTaskFun = new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.13
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.timerTask.postDelayed(this, 1000L);
            if (MainActivity.this.refCounter == 0) {
                if (MainActivity.this.isFirstLoad) {
                    MainActivity.this.outDataLogging("Establishing Websocket Connection\n", "..............");
                } else {
                    MainActivity.this.outDataLogging("Establishing Websocket ", "(IP:" + Util.loadStringPref(MainActivity.this.getBaseContext(), "ServIP", MainActivity.this.getString(R.string.ServIP)) + ", PORT:" + Util.loadStringPref(MainActivity.this.getBaseContext(), "sockPort", MainActivity.this.getString(R.string.sockPort)) + ")");
                }
                MainActivity.this.connectWebSocket();
            }
            MainActivity.this.outDataLogging("", ".");
            if (MainActivity.this.refCounter % 15 == 0 && MainActivity.this.refCounter > 0) {
                MainActivity.this.outDataLogging("", "timeout");
                MainActivity.this.outDataLogging("Retrying", ".");
                MainActivity.this.connectWebSocket();
            }
            MainActivity.this.refCounter++;
        }
    };
    private Runnable settTimerTaskFun = new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.14
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.timerTask.postDelayed(this, 1000L);
            if (MainActivity.this.refCounter == 0) {
                MainActivity.this.outDataLogging("\nObtaining Remote Device Settings", ".");
                MainActivity.this.sendInitReq("getSett", false);
            }
            MainActivity.this.outDataLogging("", ".");
            if (MainActivity.this.refCounter >= 15) {
                MainActivity.this.outDataLogging("", "timeout");
                MainActivity.this.refCounter = 0;
                MainActivity.this.runnableDisEnab(MainActivity.this.timerTaskFun);
            }
            MainActivity.this.refCounter++;
        }
    };
    private Runnable timerTaskFun = new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.15
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.isFirstLoad = false;
            MainActivity.this.refCounter++;
            boolean doRef = Util.loadBooleanPref(MainActivity.this.getBaseContext(), "sync_freq_switch", false).booleanValue();
            if (doRef) {
                MainActivity.this.refreshInt = Integer.parseInt(Util.loadStringPref(MainActivity.this.getBaseContext(), "pref_sync_freq", String.valueOf(MainActivity.this.refreshInt)));
            }
            if (MainActivity.this.refCounter >= MainActivity.this.refreshInt) {
                MainActivity.this.refCounter = 0;
                if (doRef) {
                    MainActivity.this.refreshButtonClick();
                } else {
                    MainActivity.this.sendInitReq("Ping", true);
                }
                MainActivity.this.timerTask.removeCallbacks(MainActivity.this.isPongReceived);
                MainActivity.this.timerTask.postDelayed(MainActivity.this.isPongReceived, 3000L);
            }
            MainActivity.this.timerTask.postDelayed(this, 1000L);
        }
    };
    private Runnable isPongReceived = new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.16
        @Override // java.lang.Runnable
        public void run() {
            MainActivity.this.connectWebSocket();
        }
    };

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupCollapsingToolbarLayout();
        setupToolbar();
        setupTablayout();
        setupNavigationView();
        setupSwipeRefreshLayout();
        setupFab();
        viewPagerListener();
        if (savedInstanceState != null) {
            this.viewPager.setCurrentItem(savedInstanceState.getInt("lastTab"), true);
        }
        wifiIntentFilterInit();
        this.timerTask = new Handler();
        rateMeDialog();
        realTimeUpdateFeed();
    }

    private void setTheme() {
        int selTheme = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_themes", "2")).intValue();
        switch (selTheme) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(1);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(2);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(0);
                break;
        }
        setTheme(R.style.MyMaterialTheme);
    }

    private int getCurrTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & 48;
        switch (currentNightMode) {
            case 0:
            case 16:
                return 0;
            case 32:
                return 1;
            default:
                return 2;
        }
    }

    private void setupNavigationView() {
        this.mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerToggle = setupDrawerToggle();
        this.mDrawer.addDrawerListener(this.drawerToggle);
        this.navigationView = (NavigationView) findViewById(R.id.nvView);
        this.navHeader = this.navigationView.getHeaderView(0);
        this.navigationView.setItemIconTintList(null);
        int themeSel = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_themes", "2")).intValue();
        for (int i = 0; i < 9; i++) {
            if (i > 2 && i < 7) {
                this.navigationView.getMenu().getItem(i).getIcon().setColorFilter(ResourcesCompat.getColor(getResources(), this.primaryColor[i - 3], null), PorterDuff.Mode.SRC_ATOP);
            } else {
                this.navigationView.getMenu().getItem(i).getIcon().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.textColorPrimary, null), PorterDuff.Mode.SRC_ATOP);
            }
        }
        changeNavItemTextColor(0);
        Menu menu = this.navigationView.getMenu();
        MenuItem navConWiFi = menu.findItem(R.id.nav_switch_theme);
        SwitchCompat switchcompat = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_switch_theme)).findViewById(R.id.drawer_theme_switch);
        if (themeSel == 1) {
            navConWiFi.setTitle("Night Theme");
        } else if (themeSel == 2) {
            navConWiFi.setTitle("DayNight Theme");
            switchcompat.setChecked(true);
        }
        setupDrawerContent(this.navigationView);
    }

    private void setupToolbar() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
    }

    private void setupTablayout() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.viewPager.setOffscreenPageLimit(3);
        setupViewPager(this.viewPager);
        this.tabLayout = (TabLayout) findViewById(R.id.tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
    }

    private void setupCollapsingToolbarLayout() {
        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        this.collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        this.headerImage = (ImageView) findViewById(R.id.header_image);
        this.headerImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), this.images[this.lastTab].intValue(), null));
    }

    private void setupSwipeRefreshLayout() {
        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        this.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: fatima.tech.espnetsmart.MainActivity.1
            @Override // android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                MainActivity.this.refreshButtonClick();
            }
        });
        this.swipeContainer.setColorSchemeResources(this.primaryColor[0], this.primaryColor[1], this.primaryColor[2], this.primaryColor[3]);
    }

    private void setupFab() {
    }

    private void wifiIntentFilterInit() {
        this.WifiReceiver = new WifiReceiver();
        this.wifiIntentFilter = new IntentFilter();
        this.wifiIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.wifiIntentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.wifiIntentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.wifiIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    private void realTimeUpdateFeed() {
        this.realTimeUpdTextView = (TextView) findViewById(R.id.realtime_update__feed_text);
        this.realTimeUpdImageView = (ImageView) findViewById(R.id.real_time_update_icon);
        this.myAnimationDrawable = (AnimationDrawable) this.realTimeUpdImageView.getDrawable();
        realTimeUpdateText(false);
    }

    private void realTimeUpdateText(boolean updated) {
        String normalText = "updates are not available";
        int tintColor = R.color.md_red_500;
        if (updated) {
            tintColor = R.color.md_green_500;
            normalText = "updated just now";
        }
        this.realTimeUpdImageView.setColorFilter(ContextCompat.getColor(this, tintColor));
        this.realTimeUpdTextView.setText(Util.partlyColoredText(this, "Real-time ", normalText, tintColor));
    }

    private void realTimeUpdateAnimation() {
        this.myAnimationDrawable.stop();
        this.myAnimationDrawable.start();
    }

    private void viewPagerListener() {
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: fatima.tech.espnetsmart.MainActivity.2
            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                MainActivity.this.changeNavItemTextColor(position);
                Drawable[] layers = {ResourcesCompat.getDrawable(MainActivity.this.getResources(), MainActivity.this.images[MainActivity.this.lastTab].intValue(), null), ResourcesCompat.getDrawable(MainActivity.this.getResources(), MainActivity.this.images[position].intValue(), null)};
                TransitionDrawable transition = new TransitionDrawable(layers);
                MainActivity.this.headerImage.setImageDrawable(transition);
                transition.startTransition(1500);
                MainActivity.this.lastTab = position;
                MainActivity.this.collapsingToolbar.setContentScrimColor(ContextCompat.getColor(MainActivity.this.getApplicationContext(), MainActivity.this.primaryColor[position]));
                MainActivity.this.collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(MainActivity.this.getApplicationContext(), MainActivity.this.primaryColorDark[position]));
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                MainActivity.this.enableDisableSwipeRefresh(state == 0);
            }
        });
    }

    public void navigationDrawerButtonClick(View view) {
        SwitchCompat switchButt = (SwitchCompat) view;
        if (switchButt.isChecked()) {
            Util.saveStringPref(this, "pref_themes", "2");
        } else {
            Util.saveStringPref(this, "pref_themes", "0");
        }
        restartActivity();
    }

    public void restartActivity() {
        recreate();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        registerReceiver(this.WifiReceiver, this.wifiIntentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mMessageReceiver, new IntentFilter("NetUtilSnack"));
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        this.manCloseSock = false;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.manCloseSock = true;
        if (this.mWebSocketClient != null) {
            if (this.mWebSocketClient.isOpen()) {
                this.mWebSocketClient.close();
            }
            this.mWebSocketClient = null;
        }
        if (this.timerTask != null) {
            runnableDisEnab(null);
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        unregisterReceiver(this.WifiReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mMessageReceiver);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.mDrawer.isDrawerOpen(GravityCompat.START)) {
            this.mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastTab", this.lastTab);
    }

    public class WifiReceiver extends BroadcastReceiver {
        public WifiReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "android.net.wifi.WIFI_STATE_CHANGED":
                    int extraWifiState = intent.getIntExtra("wifi_state", 4);
                    switch (extraWifiState) {
                        case 0:
                            MainActivity.this.MySnackBar("Turning WiFi OFF", -2, R.color.colorBlue, new Object[0]);
                            break;
                        case 1:
                            if (!MainActivity.this.isFirstLoad) {
                                MainActivity.this.outDataLogging("WiFi Turned OFF", "");
                            }
                            MainActivity.this.MySnackBar("WiFi Turned OFF", -2, R.color.colorRed, Integer.valueOf(R.color.colorYellow), "ENABLE");
                            break;
                        case 2:
                            MainActivity.this.MySnackBar("Turning WiFi ON", -2, R.color.colorBlue, new Object[0]);
                            break;
                        case 3:
                            if (!MainActivity.this.isFirstLoad) {
                                MainActivity.this.outDataLogging("WiFi Turned ON", "");
                            }
                            MainActivity.this.MySnackBar("WiFi Turned ON", -2, R.color.colorBlue, new Object[0]);
                            break;
                        case 4:
                            MainActivity.this.MySnackBar("WiFi network state unknown", -2, R.color.colorRed, Integer.valueOf(R.color.colorYellow), "ENABLE");
                            break;
                    }
                    break;
                case "android.net.wifi.SCAN_RESULTS":
                    if (MainActivity.this.isWiFiScan) {
                        MainActivity.this.scanResultsAvailable();
                        break;
                    }
                    break;
                case "android.net.wifi.RSSI_CHANGED":
                    MainActivity.this.UpdateNetworkStrength();
                    break;
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService("wifi");
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    String Conn_SSID = wifiInfo.getSSID().replace("\"", "");
                    TextView deviceName = (TextView) MainActivity.this.navHeader.findViewById(R.id.device_name);
                    ImageView imgDevIcon = (ImageView) MainActivity.this.navHeader.findViewById(R.id.img_device_icon);
                    Menu menu = MainActivity.this.navigationView.getMenu();
                    MenuItem navConWiFi = menu.findItem(R.id.nav_item_connwifi);
                    if (NetworkUtils.isWifiConnected(context)) {
                        if (!MainActivity.this.isFirstLoad) {
                            MainActivity.this.outDataLogging("WiFi Connected to ", Conn_SSID);
                        }
                        MainActivity.this.MySnackBar("WiFi Connected to " + Conn_SSID, 0, R.color.colorGreen, new Object[0]);
                        navConWiFi.setTitle("Disconnect Device");
                        deviceName.setText(Util.partlyBoldandColoredText(context, "Device: ", Conn_SSID.replace("\"", ""), R.color.colorLightGreen));
                        imgDevIcon.setColorFilter(ResourcesCompat.getColor(MainActivity.this.getResources(), R.color.colorLightGreen, null));
                        MainActivity.this.refCounter = 0;
                        if (MainActivity.this.isFirstLoad) {
                            MainActivity.this.runnableDisEnab(MainActivity.this.wifiConnTimerTaskFun);
                        } else {
                            MainActivity.this.runnableDisEnab(MainActivity.this.socketConnTimerTaskFun);
                        }
                    } else {
                        if (!MainActivity.this.isFirstLoad) {
                            MainActivity.this.outDataLogging("WiFi disconnected", "");
                        }
                        MainActivity.this.MySnackBar("WiFi disconnected", -2, R.color.colorRed, Integer.valueOf(R.color.colorYellow), "CONNECT");
                        navConWiFi.setTitle("Connect Device");
                        deviceName.setText(Util.partlyBoldandColoredText(context, "Status: ", "Disconnected", R.color.colorRed));
                        imgDevIcon.setColorFilter(ResourcesCompat.getColor(MainActivity.this.getResources(), R.color.colorRed, null));
                        MainActivity.this.timerTask.removeCallbacks(MainActivity.this.timerTaskFun);
                        new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.WifiReceiver.1
                            @Override // java.lang.Runnable
                            public void run() {
                                MainActivity.this.CheckAndStartWifi();
                            }
                        }, 3000L);
                    }
                    MainActivity.this.UpdateNetworkStrength();
                    break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void CheckAndStartWifi() {
        final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
        if (!NetworkUtils.isWifiEnabled(this)) {
            if (!Util.loadBooleanPref(this, "wifi_permission_switch", false).booleanValue()) {
                int tabColor = ResourcesCompat.getColor(getResources(), this.primaryColor[this.lastTab], null);
                MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.str_turning_wifi_title).titleColor(tabColor).iconRes(R.drawable.ic_signal_wifi_off_black_48dp).content(R.string.str_turning_wifi_content).positiveText(R.string.str_btn_turn_on).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.5
                    @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                        wifiMgr.setWifiEnabled(true);
                    }
                }).negativeText(R.string.str_btn_cancle).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.4
                    @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                        MainActivity.this.MySnackBar("Wifi is disabled (OFF)", -2, R.color.colorRed, Integer.valueOf(R.color.colorYellow), "ENABLE");
                    }
                }).build();
                dialog.getIconView().setColorFilter(tabColor);
                dialog.show();
                return;
            }
            wifiMgr.setWifiEnabled(true);
            return;
        }
        SearchAvaDevices();
    }

    public void SearchAvaDevices() {
        if (NetworkUtils.isWifiEnabled(this)) {
            if (!this.isWiFiScan) {
                this.isWiFiScan = true;
                circleToolbarProgress(true);
                MySnackBar("Searching available devices", 0, R.color.colorBlue, new Object[0]);
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
                wifiMgr.startScan();
                return;
            }
            MySnackBar("Another scan is in progress", 0, R.color.colorBlue, new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanResultsAvailable() {
        this.isWiFiScan = false;
        circleToolbarProgress(false);
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
        List<ScanResult> wifiList = wifiMgr.getScanResults();
        Comparator<ScanResult> comparator = new Comparator<ScanResult>() { // from class: fatima.tech.espnetsmart.MainActivity.6
            @Override // java.util.Comparator
            public int compare(ScanResult lhs, ScanResult rhs) {
                if (rhs.level < lhs.level) {
                    return -1;
                }
                return lhs.level == rhs.level ? 0 : 1;
            }
        };
        Collections.sort(wifiList, comparator);
        int size = wifiList.size();
        if (size > 0) {
            NetworkUtils.showScanResult(this, this.manWiFiScan);
        } else {
            MySnackBar("No WiFi network found", -2, R.color.colorOrange, Integer.valueOf(R.color.colorYellow), "SCAN");
        }
        this.manWiFiScan = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void UpdateNetworkStrength() {
        TextView deviceStrength = (TextView) this.navHeader.findViewById(R.id.device_strength);
        ImageView imgDevStr = (ImageView) this.navHeader.findViewById(R.id.img_device_strength);
        if (NetworkUtils.isWifiConnected(this)) {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int level = wifiInfo.getRssi();
            String SSID = wifiInfo.getSSID();
            int levelperc = WifiManager.calculateSignalLevel(level, 100);
            int levelperc2 = WifiManager.calculateSignalLevel(level, 5);
            if (this.mDrawer.isDrawerOpen(GravityCompat.START)) {
                List<ScanResult> networkList = wifiMgr.getScanResults();
                Boolean secType = false;
                if (networkList != null) {
                    for (ScanResult network : networkList) {
                        if (network.SSID.equalsIgnoreCase(SSID)) {
                            String capabilities = network.capabilities.toUpperCase();
                            if (capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains("WPA2")) {
                                secType = true;
                            }
                        }
                    }
                }
                if (secType.booleanValue()) {
                    imgDevStr.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wifi_level_lock, null));
                } else {
                    imgDevStr.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wifi_level, null));
                }
                int colorVal = R.color.colorGrey;
                if (levelperc <= 20) {
                    colorVal = R.color.colorRed;
                } else if (levelperc > 20 && levelperc <= 50) {
                    colorVal = R.color.colorOrange;
                } else if (levelperc > 50 && levelperc <= 80) {
                    colorVal = R.color.colorBlue;
                } else if (levelperc > 80) {
                    colorVal = R.color.colorGreen;
                }
                imgDevStr.setColorFilter(ContextCompat.getColor(this, colorVal));
                imgDevStr.setImageLevel(levelperc2);
                deviceStrength.setText(Util.partlyBoldandColoredText(this, "Signal: ", levelperc + "% (" + level + " dBm)", colorVal));
                return;
            }
            return;
        }
        imgDevStr.setColorFilter(ContextCompat.getColor(this, R.color.colorRed));
        imgDevStr.setImageLevel(0);
        deviceStrength.setText(Util.partlyBoldandColoredText(this, "Signal: ", "0 dBm", R.color.colorRed));
    }

    private void connDiscFunction(String action) {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
        if (action.equalsIgnoreCase("Connect Device")) {
            MySnackBar("Connecting to last connected device", -2, R.color.colorBlue, new Object[0]);
            wifiMgr.reconnect();
            new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.7
                @Override // java.lang.Runnable
                public void run() {
                    if (!NetworkUtils.isWifiConnected(MainActivity.this.getBaseContext())) {
                        MainActivity.this.CheckAndStartWifi();
                    }
                }
            }, 3000L);
        } else {
            MySnackBar("Disconnecting", -2, R.color.colorBlue, new Object[0]);
            wifiMgr.disconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectWebSocket() {
        realTimeUpdateText(false);
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
            this.mWebSocketClient = new WebSocketClient(uri) { // from class: fatima.tech.espnetsmart.MainActivity.8
                @Override // org.java_websocket.client.WebSocketClient
                public void onOpen(ServerHandshake serverHandshake) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.8.1
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.processData("<{\"action\":\"webSockConn\"}>");
                        }
                    });
                }

                @Override // org.java_websocket.client.WebSocketClient
                public void onClose(int i, String s, boolean b) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.8.2
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.processData("<{\"action\":\"webSockDisConn\"}>");
                        }
                    });
                }

                @Override // org.java_websocket.client.WebSocketClient
                public void onMessage(final String s) {
                    MainActivity.this.runOnUiThread(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.8.3
                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.this.processData(s);
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
    public void sendData(JSONObject jsonRoot, boolean logData) {
        realTimeUpdateAnimation();
        String outData = jsonRoot.toString();
        if (this.mWebSocketClient != null && this.mWebSocketClient.isOpen()) {
            this.mWebSocketClient.send("<" + outData + ">");
        }
        if (logData) {
            outDataLogging("OUT", outData);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processData(String inMessage) {
        Log.i("inMessage", inMessage);
        circleToolbarProgress(false);
        int startChar = inMessage.indexOf("<");
        int stopChar = inMessage.lastIndexOf(">");
        if (startChar != -1 && stopChar != -1) {
            String inMessage2 = inMessage.substring(startChar + 1, stopChar);
            try {
                JSONObject jsonRoot = new JSONObject(inMessage2);
                if (jsonRoot.has("action")) {
                    String action = jsonRoot.getString("action");
                    if (!action.equals("webSockDisConn")) {
                        this.refCounter = 0;
                    }
                    switch (action) {
                        case "webSockConn":
                            realTimeUpdateText(true);
                            outDataLogging("Websocket Connected\n", "");
                            this.isWebsocketConn = true;
                            Log.i("isWebsocketConn1", String.valueOf(this.isWebsocketConn));
                            if (this.isFirstLoad) {
                                runnableDisEnab(this.settTimerTaskFun);
                            } else {
                                runnableDisEnab(this.timerTaskFun);
                            }
                            break;
                        case "webSockDisConn":
                            Log.i("isWebsocketConn2", String.valueOf(this.isWebsocketConn));
                            if (this.isWebsocketConn) {
                                outDataLogging("\nWebsocket Disconnected\nReconnecting", "");
                            }
                            this.isWebsocketConn = false;
                            Log.i("isWebsocketConn3", String.valueOf(this.isWebsocketConn));
                            if (!this.manCloseSock) {
                                runnableDisEnab(this.socketConnTimerTaskFun);
                                break;
                            }
                            break;
                        case "btnState":
                        case "btnMode":
                        case "btnTimerOn":
                        case "btnTimerOff":
                        case "refresh":
                        case "servResp":
                            outDataLogging("IN", inMessage2);
                            this.timerTask.removeCallbacks(this.isPongReceived);
                            int tabID = jsonRoot.getInt("tabID");
                            switch (tabID) {
                                case 1:
                                    Tab1 fragmentTab1 = (Tab1) getSupportFragmentManager().getFragments().get(0);
                                    if (fragmentTab1 != null) {
                                        fragmentTab1.dataFromMainActivity(inMessage2);
                                    }
                                    break;
                                case 2:
                                    Tab2 fragmentTab2 = (Tab2) getSupportFragmentManager().getFragments().get(1);
                                    if (fragmentTab2 != null) {
                                        fragmentTab2.dataFromMainActivity(inMessage2);
                                    }
                                    break;
                                case 3:
                                    Tab3 fragmentTab3 = (Tab3) getSupportFragmentManager().getFragments().get(2);
                                    if (fragmentTab3 != null) {
                                        fragmentTab3.dataFromMainActivity(inMessage2);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "getSett":
                            String wifiSSID = jsonRoot.getString("wifiSSID");
                            String wifiPass = jsonRoot.getString("wifiPass");
                            String ServIP = jsonRoot.getString("ServIP");
                            String httpPort = String.valueOf(jsonRoot.getInt("httpPort"));
                            String sockPort = String.valueOf(jsonRoot.getInt("sockPort"));
                            String mdnsName = jsonRoot.getString("mdnsName");
                            String serMode = jsonRoot.getString("serMode");
                            String heap = String.valueOf(jsonRoot.getInt("heap"));
                            String tnkFVol = String.valueOf(jsonRoot.getInt("tnkFVol"));
                            String tnkECal = String.valueOf(jsonRoot.getInt("tnkECal"));
                            String tnkFCal = String.valueOf(jsonRoot.getInt("tnkFCal"));
                            String pumASta = String.valueOf(jsonRoot.getInt("pumASta"));
                            String battR = String.valueOf(jsonRoot.getInt("battR"));
                            String mainR = String.valueOf(jsonRoot.getInt("mainR"));
                            String luxOffCal = String.valueOf(jsonRoot.getInt("luxOffCal"));
                            String luxOnCal = String.valueOf(jsonRoot.getInt("luxOnCal"));
                            Util.saveStringPref(getBaseContext(), "wifiSSID", wifiSSID);
                            Util.saveStringPref(getBaseContext(), "wifiPass", wifiPass);
                            Util.saveStringPref(getBaseContext(), "ServIP", ServIP);
                            Util.saveStringPref(getBaseContext(), "httpPort", httpPort);
                            Util.saveStringPref(getBaseContext(), "sockPort", sockPort);
                            Util.saveStringPref(getBaseContext(), "mdnsName", mdnsName);
                            Util.saveStringPref(getBaseContext(), "serMode", serMode);
                            Util.saveStringPref(getBaseContext(), "heap", heap);
                            Util.saveStringPref(getBaseContext(), "tank_capacity", tnkFVol);
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_low", tnkECal);
                            Util.saveStringPref(getBaseContext(), "pressure_sensor_high", tnkFCal);
                            Util.saveStringPref(getBaseContext(), "pump_auto_start_level", pumASta);
                            Util.saveStringPref(getBaseContext(), "battery_rating_list", battR);
                            Util.saveStringPref(getBaseContext(), "ac_power_rating_list", mainR);
                            Util.saveStringPref(getBaseContext(), "light_cutoff", luxOffCal);
                            Util.saveStringPref(getBaseContext(), "light_cuton", luxOnCal);
                            long inTime = jsonRoot.getInt("currTime");
                            long currentMilliSeconds = System.currentTimeMillis();
                            TimeZone timezone = TimeZone.getDefault();
                            int tzOffset = timezone.getRawOffset();
                            long offsetT = (((long) tzOffset) + currentMilliSeconds) - (1000 * inTime);
                            Util.saveLongPref(getBaseContext(), "offsetT", offsetT);
                            this.isDevSettDownl = true;
                            outDataLogging("WiFi SSID: ", wifiSSID);
                            outDataLogging("WiFi Pass: ", wifiPass);
                            outDataLogging("Server IP: ", ServIP);
                            outDataLogging("HTTP Port: ", httpPort);
                            outDataLogging("Socket Port: ", sockPort);
                            outDataLogging("MDNS: ", mdnsName);
                            outDataLogging("Server Mode: ", serMode);
                            outDataLogging("Heap (RAM): ", heap + " kB");
                            outDataLogging("Tank Full Volume: ", tnkFVol + " Liter");
                            outDataLogging("Tank Empty Calibration: ", tnkECal);
                            outDataLogging("Tank Full Calibration: ", tnkFCal);
                            outDataLogging("Pump Auto Start: ", pumASta + " %");
                            outDataLogging("Battery Rating: ", battR + " Volts");
                            outDataLogging("AC Mains Rating: ", mainR + " Volts");
                            outDataLogging("Auto Switch OFF Lights at: ", luxOffCal + " Lux");
                            outDataLogging("Auto Switch ON Lights at: ", luxOnCal + " Lux");
                            outDataLogging("Device Current Time: ", Util.convertFromUnix(1000 * inTime));
                            outDataLogging("Time Offset: ", String.format(Locale.US, "%.1f", Double.valueOf(offsetT / 1000.0d)) + " s");
                            outDataLogging("", "\n´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``\n");
                            runnableDisEnab(this.timerTaskFun);
                            refreshButtonClick2(1);
                            new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.9
                                @Override // java.lang.Runnable
                                public void run() {
                                    MainActivity.this.refreshButtonClick2(2);
                                    new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.9.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            MainActivity.this.refreshButtonClick2(3);
                                        }
                                    }, 2000L);
                                }
                            }, 2000L);
                            if (Math.abs(offsetT) > 600000) {
                                int tabColor = ResourcesCompat.getColor(getResources(), this.primaryColor[this.lastTab], null);
                                MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.str_device_clock_update_title).titleColor(tabColor).iconRes(R.drawable.ic_watch_black_48dp).content(R.string.str_update_device_clock_content).positiveText(R.string.str_clock_update).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.11
                                    @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                                        Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                        int year = calendar.get(1);
                                        int month = calendar.get(2);
                                        int date = calendar.get(5);
                                        int hour = calendar.get(11);
                                        int minute = calendar.get(12);
                                        int second = calendar.get(13);
                                        int dayOfWeek = calendar.get(7);
                                        TimeZone timezone2 = TimeZone.getDefault();
                                        int tzOffset2 = timezone2.getRawOffset();
                                        long unixTime = (System.currentTimeMillis() + ((long) tzOffset2)) / 1000;
                                        JSONObject jsonRoot2 = new JSONObject();
                                        try {
                                            jsonRoot2.put("action", "setTime");
                                            jsonRoot2.put("year", year % 100);
                                            jsonRoot2.put("month", month + 1);
                                            jsonRoot2.put("date", date);
                                            jsonRoot2.put("hour", hour);
                                            jsonRoot2.put("minute", minute);
                                            jsonRoot2.put("second", second);
                                            jsonRoot2.put("dayOfWeek", dayOfWeek == 1 ? 7 : dayOfWeek - 1);
                                            jsonRoot2.put("unixTime", unixTime);
                                            if (MainActivity.this.mWebSocketClient != null) {
                                                MainActivity.this.sendData(jsonRoot2, true);
                                                MainActivity.this.gotUpdated = false;
                                                new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.11.1
                                                    @Override // java.lang.Runnable
                                                    public void run() {
                                                        if (!MainActivity.this.gotUpdated) {
                                                            MainActivity.this.MySnackBar("Device time update failed", 0, R.color.colorRed, new Object[0]);
                                                        }
                                                    }
                                                }, 5000L);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).negativeText(R.string.str_btn_cancle).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.10
                                    @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                                    public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                                    }
                                }).build();
                                dialog.getIconView().setColorFilter(tabColor);
                                dialog.show();
                                break;
                            }
                            break;
                        case "setSett":
                            long inTime2 = jsonRoot.getInt("currTime");
                            long currentMilliSeconds2 = System.currentTimeMillis();
                            TimeZone timezone2 = TimeZone.getDefault();
                            int tzOffset2 = timezone2.getRawOffset();
                            long offsetT2 = (currentMilliSeconds2 - (1000 * inTime2)) + ((long) tzOffset2);
                            Util.saveLongPref(getBaseContext(), "offsetT", offsetT2);
                            outDataLogging("INFO", "Device settings got updated\n");
                            MySnackBar("Device settings got updated\nRestarting system with new configuration", 0, R.color.colorBlue, new Object[0]);
                            break;
                        case "setTime":
                            this.gotUpdated = true;
                            outDataLogging("INFO", "Device time got updated\n");
                            long inTime3 = jsonRoot.getInt("currTime");
                            long currentMilliSeconds3 = System.currentTimeMillis();
                            TimeZone timezone3 = TimeZone.getDefault();
                            int tzOffset3 = timezone3.getRawOffset();
                            long offsetT3 = (currentMilliSeconds3 - (1000 * inTime3)) + ((long) tzOffset3);
                            Util.saveLongPref(getBaseContext(), "offsetT", offsetT3);
                            MySnackBar("Device time updated successfully", 0, R.color.colorBlue, new Object[0]);
                            break;
                        case "logClients":
                            String inData = jsonRoot.getString("data");
                            outDataLogging("IN", inMessage2);
                            MySnackBar(inData, 0, R.color.colorGrey, new Object[0]);
                            break;
                        case "Pong":
                            outDataLogging("IN", inMessage2);
                            this.timerTask.removeCallbacks(this.isPongReceived);
                            break;
                    }
                    return;
                }
                return;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        outDataLogging("IN", inMessage);
    }

    @Override // fatima.tech.espnetsmart.Tab4.mainActivityListener
    public void tab4IsReady() {
        this.istab4IsReady = true;
    }

    public void startWebSocketClient() {
        outDataLogging("", "\n.................................................................");
        if (NetworkUtils.isWifiEnabled(this)) {
            outDataLogging("WiFi Turned ON", "");
            outDataLogging("Connecting to WiFi", ".......");
            if (NetworkUtils.isWifiConnected(this)) {
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService("wifi");
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                String connSSID = wifiInfo.getSSID();
                DhcpInfo dhcp = wifiMgr.getDhcpInfo();
                String ip = Formatter.formatIpAddress(dhcp.gateway);
                String macAddr = wifiInfo.getMacAddress();
                String bssid = wifiInfo.getBSSID();
                outDataLogging("WiFi Connected to: ", connSSID.replace("\"", ""));
                outDataLogging("Gateway: ", ip);
                outDataLogging("MAC Address: ", macAddr);
                outDataLogging("BSSID: ", bssid);
                outDataLogging("\nWebsocket Parameters", ":");
                outDataLogging("IP: ", Util.loadStringPref(getBaseContext(), "ServIP", getString(R.string.ServIP)));
                outDataLogging("Port: ", Util.loadStringPref(getBaseContext(), "sockPort", getString(R.string.sockPort)));
                this.refCounter = 0;
                this.timerTask.removeCallbacks(this.socketConnTimerTaskFun);
                this.timerTask.postDelayed(this.socketConnTimerTaskFun, 0L);
                return;
            }
            outDataLogging("WiFi Disconnected", "");
            outDataLogging("", "\n´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``\n");
            return;
        }
        outDataLogging("WiFi Turned OFF", "");
        outDataLogging("", "\n´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``´´``\n");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendInitReq(String key, boolean log) {
        JSONObject jsonRoot = new JSONObject();
        try {
            jsonRoot.put("action", key);
            sendData(jsonRoot, log);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void outDataLogging(String msg, String type) {
        try {
            Tab4 fragmentTab4 = (Tab4) getSupportFragmentManager().getFragments().get(3);
            if (fragmentTab4 != null) {
                fragmentTab4.dataFromMainActivity(msg, type);
            }
        } catch (Exception e) {
        }
    }

    public void circleToolbarProgress(Boolean showHide) {
        if (menuAction != null) {
            final MenuItem menuItem = menuAction.findItem(R.id.action_refresh);
            if (showHide.booleanValue()) {
                menuItem.setActionView(R.layout.circle_progress);
                this.swipeContainer.setRefreshing(false);
                new Handler().postDelayed(new Runnable() { // from class: fatima.tech.espnetsmart.MainActivity.17
                    @Override // java.lang.Runnable
                    public void run() {
                        menuItem.setActionView((View) null);
                    }
                }, 10000L);
                return;
            }
            menuItem.setActionView((View) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableDisableSwipeRefresh(boolean enable) {
        if (this.swipeContainer != null) {
            this.swipeContainer.setEnabled(enable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runnableDisEnab(Runnable runB) {
        this.timerTask.removeCallbacks(this.wifiConnTimerTaskFun);
        this.timerTask.removeCallbacks(this.socketConnTimerTaskFun);
        this.timerTask.removeCallbacks(this.settTimerTaskFun);
        this.timerTask.removeCallbacks(this.timerTaskFun);
        if (runB != null) {
            this.timerTask.postDelayed(runB, 0L);
        }
    }

    @Override // fatima.tech.espnetsmart.Tab1.mainActivityListener, fatima.tech.espnetsmart.Tab2.mainActivityListener
    public void buttonClick(View view) {
        int btnID;
        int btnSM;
        JSONObject jsonRoot = new JSONObject();
        if ((view instanceof ToggleButton) || (view instanceof TriToggleButton)) {
            boolean btnType = false;
            if (view instanceof ToggleButton) {
                ToggleButton btn = (ToggleButton) view;
                btnID = Integer.parseInt(btn.getTag().toString());
                btnSM = btn.isChecked() ? 1 : 0;
                btnType = true;
            } else {
                TriToggleButton btn2 = (TriToggleButton) findViewById(view.getId());
                btnID = Integer.parseInt(btn2.getTag().toString());
                btnSM = btn2.getState();
            }
            try {
                jsonRoot.put("action", btnType ? "btnState" : "btnMode");
                jsonRoot.put("tabID", this.viewPager.getCurrentItem() + 1);
                jsonRoot.put("btnID", btnID);
                jsonRoot.put(btnType ? "btnS" : "btnM", btnSM);
                sendData(jsonRoot, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // fatima.tech.espnetsmart.Tab1.mainActivityListener, fatima.tech.espnetsmart.Tab2.mainActivityListener
    public void timerTextClick(View view, final boolean onOff) {
        if (view instanceof TextView) {
            TextView textV = (TextView) view;
            final int btnID = Integer.parseInt(textV.getTag().toString());
            String currTime = textV.getText().toString();
            int hour = Integer.parseInt(currTime.substring(0, 2));
            int minute = Integer.parseInt(currTime.substring(3));
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, onOff ? R.style.TimerOnDialogTheme : R.style.TimerOffDialogTheme, new TimePickerDialog.OnTimeSetListener() { // from class: fatima.tech.espnetsmart.MainActivity.18
                @Override // android.app.TimePickerDialog.OnTimeSetListener
                public void onTimeSet(TimePicker view2, int hourOfDay, int minute2) {
                    JSONObject jsonRoot = new JSONObject();
                    try {
                        jsonRoot.put("action", onOff ? "btnTimerOn" : "btnTimerOff");
                        jsonRoot.put("tabID", MainActivity.this.viewPager.getCurrentItem() + 1);
                        jsonRoot.put("btnID", btnID);
                        jsonRoot.put(onOff ? "onH" : "offH", hourOfDay);
                        jsonRoot.put(onOff ? "onM" : "offM", minute2);
                        MainActivity.this.sendData(jsonRoot, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, hour, minute, true);
            timePickerDialog.show();
        }
    }

    @Override // fatima.tech.espnetsmart.Tab4.mainActivityListener
    public void DataSendDeviceButtonClick(String key, String inData) {
        JSONObject jsonRoot = new JSONObject();
        try {
            jsonRoot.put("action", key);
            jsonRoot.put("data", inData);
            sendData(jsonRoot, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override // fatima.tech.espnetsmart.Tab4.mainActivityListener
    public void textCopiedToClip() {
        MySnackBar("Log text copied to clipboard", 0, R.color.md_teal_500, new Object[0]);
    }

    public void refreshButtonClick() {
        refreshButtonClick2(this.viewPager.getCurrentItem() + 1);
    }

    public void refreshButtonClick2(int tabID) {
        circleToolbarProgress(true);
        JSONObject jsonRoot = new JSONObject();
        try {
            jsonRoot.put("action", "refresh");
            jsonRoot.put("tabID", tabID);
            sendData(jsonRoot, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, this.mDrawer, this.toolbar, R.string.drawer_open, R.string.drawer_close) { // from class: fatima.tech.espnetsmart.MainActivity.19
            @Override // android.support.v7.app.ActionBarDrawerToggle, android.support.v4.widget.DrawerLayout.DrawerListener
            public void onDrawerOpened(View drawerView) {
                MainActivity.this.UpdateNetworkStrength();
            }
        };
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.drawerToggle.syncState();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() { // from class: fatima.tech.espnetsmart.MainActivity.20
            @Override // android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                MainActivity.this.displayView(menuItem);
                return true;
            }
        });
    }

    void displayView(MenuItem menuItem) {
        int navItemIndex = 4;
        switch (menuItem.getItemId()) {
            case R.id.nav_switch_theme /* 2131624256 */:
                showThemeChangeDialog();
                break;
            case R.id.gp_two /* 2131624257 */:
            case R.id.gp_three /* 2131624260 */:
            case R.id.gp_four /* 2131624265 */:
            default:
                navItemIndex = 4;
                break;
            case R.id.nav_item_connwifi /* 2131624258 */:
                String ison = menuItem.getTitle().toString();
                connDiscFunction(ison);
                break;
            case R.id.nav_item_scanwifi /* 2131624259 */:
                this.manWiFiScan = true;
                CheckAndStartWifi();
                break;
            case R.id.nav_item_pump /* 2131624261 */:
                navItemIndex = 0;
                break;
            case R.id.nav_item_lights /* 2131624262 */:
                navItemIndex = 1;
                break;
            case R.id.nav_item_sensors /* 2131624263 */:
                navItemIndex = 2;
                break;
            case R.id.nav_item_logs /* 2131624264 */:
                navItemIndex = 3;
                break;
            case R.id.nav_item_appsetting /* 2131624266 */:
                Intent intentSetting = new Intent(this, (Class<?>) SettingsActivity.class);
                startActivity(intentSetting);
                break;
            case R.id.nav_item_help /* 2131624267 */:
                Intent intentHelp = new Intent(this, (Class<?>) HelpActivity.class);
                startActivity(intentHelp);
                break;
        }
        this.mDrawer.closeDrawers();
        if (navItemIndex <= 3) {
            this.viewPager.setCurrentItem(navItemIndex);
            this.tabLayout.setupWithViewPager(this.viewPager);
            changeNavItemTextColor(navItemIndex);
        }
    }

    public void changeNavItemTextColor(int menuPos) {
        if (this.navigationView != null) {
            MenuItem menuItem = this.navigationView.getMenu().getItem(this.lastTab + 3);
            menuItem.setTitle(Util.partlyColoredText(this, menuItem.getTitle().toString(), "", R.color.textColorPrimary));
            MenuItem menuItem2 = this.navigationView.getMenu().getItem(menuPos + 3);
            menuItem2.setTitle(Util.partlyColoredText(this, menuItem2.getTitle().toString(), "", this.primaryColor[menuPos]));
            menuItem2.setChecked(true);
        }
    }

    public void showThemeChangeDialog() {
        int tabColor = ResourcesCompat.getColor(getResources(), this.primaryColor[this.lastTab], null);
        int themeSel = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_themes", "2")).intValue();
        MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.str_theme_select_dialog_title).titleColor(tabColor).widgetColor(tabColor).iconRes(R.drawable.ic_theme_change_black_48dp).items(R.array.pref_themes_list_titles).itemsCallbackSingleChoice(themeSel, new MaterialDialog.ListCallbackSingleChoice() { // from class: fatima.tech.espnetsmart.MainActivity.21
            @Override // com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice
            public boolean onSelection(MaterialDialog dialog2, View view, int which, CharSequence text) {
                Menu menu = MainActivity.this.navigationView.getMenu();
                MenuItem navConWiFi = menu.findItem(R.id.nav_switch_theme);
                SwitchCompat switchcompat = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_switch_theme)).findViewById(R.id.drawer_theme_switch);
                switchcompat.setChecked(false);
                navConWiFi.setTitle(MainActivity.this.getResources().getStringArray(R.array.pref_themes_list_titles)[which]);
                String themeSel2 = "2";
                switch (which) {
                    case 0:
                        themeSel2 = "0";
                        break;
                    case 1:
                        themeSel2 = "1";
                        break;
                    case 2:
                        switchcompat.setChecked(true);
                        themeSel2 = "2";
                        break;
                }
                Util.saveStringPref(MainActivity.this.getBaseContext(), "pref_themes", themeSel2);
                MainActivity.this.restartActivity();
                return true;
            }
        }).build();
        dialog.getIconView().setColorFilter(tabColor);
        dialog.show();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuAction = menu;
        return true;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override // android.support.v4.app.FragmentActivity
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        Util.coloringOptionMenuItem(this, menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Util.optionMenuActions(this, item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                this.mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_refresh /* 2131624274 */:
                refreshButtonClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1(), " PUMP ");
        adapter.addFragment(new Tab2(), "LIGHTS");
        adapter.addFragment(new Tab3(), "SENSORS");
        adapter.addFragment(new Tab4(), "LOGS");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList;
        private final List<String> mFragmentTitleList;

        public Adapter(FragmentManager manager) {
            super(manager);
            this.mFragmentList = new ArrayList();
            this.mFragmentTitleList = new ArrayList();
        }

        @Override // android.support.v4.app.FragmentPagerAdapter
        public Fragment getItem(int position) {
            return this.mFragmentList.get(position);
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return this.mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            this.mFragmentList.add(fragment);
            this.mFragmentTitleList.add(title);
        }

        @Override // android.support.v4.view.PagerAdapter
        public CharSequence getPageTitle(int position) {
            return this.mFragmentTitleList.get(position);
        }
    }

    void MySnackBar(String txt, int duration, int bkgColor, Object... optParam) {
        Snackbar snackbar = Snackbar.make(this.coordinatorLayout, txt, duration);
        Util.colorSnackBar(this, snackbar, bkgColor);
        if (optParam.length > 1) {
            int actionBtnColor = ((Integer) optParam[0]).intValue();
            final String actLabel = (String) optParam[1];
            snackbar.setActionTextColor(ResourcesCompat.getColor(getResources(), actionBtnColor, null));
            snackbar.setAction(actLabel, new View.OnClickListener() { // from class: fatima.tech.espnetsmart.MainActivity.22
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    switch (actLabel) {
                        case "ENABLE":
                            WifiManager wifiMgr = (WifiManager) MainActivity.this.getApplicationContext().getSystemService("wifi");
                            wifiMgr.setWifiEnabled(true);
                            break;
                        case "CONNECT":
                            MainActivity.this.CheckAndStartWifi();
                            break;
                        case "SCAN":
                            MainActivity.this.manWiFiScan = true;
                            MainActivity.this.CheckAndStartWifi();
                            break;
                    }
                }
            });
        }
        snackbar.show();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    void rateMeDialog() {
        boolean shouldShow = false;
        int launches = 0;
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = Util.loadLongPref(this, LAST_PROMPT, 0L);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
        }
        if (!Util.loadBooleanPref(this, DISABLED, false).booleanValue() && (launches = Util.loadIntPref(this, LAUNCHES, 0) + 1) > 10 && currentTime > 172800000 + lastPromptTime) {
            shouldShow = true;
        }
        if (shouldShow) {
            Util.saveLongPref(this, LAST_PROMPT, System.currentTimeMillis());
            Util.saveIntPref(this, LAUNCHES, 0);
            int tabColor = ResourcesCompat.getColor(getResources(), this.primaryColor[this.lastTab], null);
            MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.rate_title).titleColor(tabColor).iconRes(R.drawable.ic_stars_black_48dp).content(R.string.rate_message).positiveText(R.string.rate_positive).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.25
                @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                    Util.saveBooleanPref(MainActivity.this.getBaseContext(), MainActivity.DISABLED, true);
                }
            }).neutralText(R.string.rate_remind_later).onNeutral(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.24
                @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                }
            }).negativeText(R.string.rate_never).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.MainActivity.23
                @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
                public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                    Util.saveBooleanPref(MainActivity.this.getBaseContext(), MainActivity.DISABLED, true);
                }
            }).build();
            dialog.getIconView().setColorFilter(tabColor);
            dialog.show();
            return;
        }
        Util.saveLongPref(this, LAST_PROMPT, lastPromptTime);
        Util.saveIntPref(this, LAUNCHES, launches);
    }
}
