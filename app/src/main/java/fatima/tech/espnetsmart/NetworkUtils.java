package fatima.tech.espnetsmart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class NetworkUtils {
    NetworkUtils() {
    }

    static boolean isWifiEnabled(@NonNull Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        boolean test = wifiMgr.isWifiEnabled();
        if (test) {
            Log.d("isWifiEnabled", "Enabled");
        } else {
            Log.d("isWifiEnabled", "DisEnabled");
        }
        return wifiMgr.isWifiEnabled();
    }

    static boolean isWifiConnected(@NonNull Context context) {
        return isConnected(context, 1);
    }

    public static boolean isMobileConnected(@NonNull Context context) {
        return isConnected(context, 0);
    }

    private static boolean isConnected(@NonNull Context context, int type) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService("connectivity");
        Network[] networks = connMgr.getAllNetworks();
        for (Network mNetwork : networks) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(mNetwork);
            if (networkInfo != null && networkInfo.getType() == type && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    static void showScanResult(final Context cntx, boolean manWiFiScan) {
        Log.d("CONNECTIVITY_ACTION", "showScanResult()");
        WifiManager wifiMgr = (WifiManager) cntx.getApplicationContext().getSystemService("wifi");
        if (wifiMgr != null) {
            List<ScanResult> results = wifiMgr.getScanResults();
            Collections.sort(results, new Comparator<ScanResult>() { // from class: fatima.tech.espnetsmart.NetworkUtils.1
                @Override // java.util.Comparator
                public int compare(ScanResult lhs, ScanResult rhs) {
                    if (rhs.level > lhs.level) {
                        return 1;
                    }
                    return rhs.level < lhs.level ? -1 : 0;
                }
            });
            if (manWiFiScan) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(cntx);
                final WifiAdapter arrayAdapter = new WifiAdapter(cntx, android.R.layout.select_dialog_item, results);
                builderSingle.setNegativeButton(cntx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() { // from class: fatima.tech.espnetsmart.NetworkUtils.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() { // from class: fatima.tech.espnetsmart.NetworkUtils.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        String selSSID = arrayAdapter.getItem(which).SSID;
                        arrayAdapter.getItem(which).capabilities.toUpperCase();
                        NetworkUtils.ConnToWiFiNetwork(cntx, selSSID);
                    }
                });
                builderSingle.setTitle("Select device to connect \n");
                AlertDialog dialog = builderSingle.create();
                dialog.show();
                return;
            }
            String prevSSID = Util.loadStringPref(cntx, "wifi_ssid", cntx.getString(R.string.wifiSSID));
            ConnToWiFiNetwork(cntx, prevSSID);
        }
    }

    static class WifiAdapter extends ArrayAdapter<ScanResult> {
        Context cntx;

        public WifiAdapter(Context context, int resource, List<ScanResult> objects) {
            super(context, resource, objects);
            this.cntx = context;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.cntx.getSystemService("layout_inflater");
                convertView = inflater.inflate(R.layout.wifi_item, parent, false);
            }
            ScanResult result = getItem(position);
            TextView txtWiFiSSID = (TextView) convertView.findViewById(R.id.wifi_name);
            ImageView imgWiFi = (ImageView) convertView.findViewById(R.id.wifi_img);
            String capabilities = result.capabilities.toUpperCase();
            if (capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains("WPA2")) {
                imgWiFi.setImageDrawable(ResourcesCompat.getDrawable(this.cntx.getResources(), R.drawable.wifi_level_lock, null));
            } else {
                imgWiFi.setImageDrawable(ResourcesCompat.getDrawable(this.cntx.getResources(), R.drawable.wifi_level, null));
            }
            imgWiFi.setColorFilter(ResourcesCompat.getColor(this.cntx.getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_ATOP);
            imgWiFi.setImageLevel(getNormalizedLevel(result));
            txtWiFiSSID.setText(formatSSDI(result));
            return convertView;
        }

        private int getNormalizedLevel(ScanResult r) {
            int level = WifiManager.calculateSignalLevel(r.level, 5);
            return level;
        }

        private String formatSSDI(ScanResult r) {
            return (r == null || r.SSID == null || "".equalsIgnoreCase(r.SSID.trim())) ? "no data" : r.SSID.replace("\"", "");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void ConnToWiFiNetwork(Context cntx, String selSSID) {
        Log.d("ConnToWiFiNetwork()", selSSID);
        sendMessage(cntx, "Connecting to " + selSSID, R.color.colorBlue);
        WifiManager wifiMgr = (WifiManager) cntx.getApplicationContext().getSystemService("wifi");
        boolean confSSID = false;
        for (WifiConfiguration tmp : wifiMgr.getConfiguredNetworks()) {
            if (tmp.SSID.equals("\"" + selSSID + "\"")) {
                int netId = tmp.networkId;
                boolean conReq = false;
                if (isWifiConnected(cntx)) {
                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                    String Conn_SSID = wifiInfo.getSSID();
                    if (!Conn_SSID.equals("\"" + selSSID + "\"")) {
                        conReq = true;
                    }
                } else {
                    conReq = true;
                }
                if (conReq) {
                    wifiMgr.disconnect();
                    wifiMgr.enableNetwork(netId, true);
                    wifiMgr.reconnect();
                }
                confSSID = true;
            }
        }
        if (!confSSID) {
            showCustomView(cntx, selSSID);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void ConnToNonCofNetwork(Context cntx, String devSSID, String devPass) {
        Log.d("ConnToNonCofNetwork()", devSSID);
        sendMessage(cntx, "Connecting to " + devSSID, R.color.colorBlue);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", devSSID);
        if (devPass == null) {
            wifiConfig.allowedKeyManagement.set(0);
        } else {
            wifiConfig.preSharedKey = String.format("\"%s\"", devPass);
        }
        WifiManager wifiMgr = (WifiManager) cntx.getApplicationContext().getSystemService("wifi");
        int netId = wifiMgr.addNetwork(wifiConfig);
        wifiMgr.disconnect();
        wifiMgr.enableNetwork(netId, true);
        wifiMgr.reconnect();
    }

    private static void showCustomView(final Context cntx, final String ssid) {
        Log.d("showCustomView()", ssid);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(cntx);
        LayoutInflater inflater = (LayoutInflater) cntx.getSystemService("layout_inflater");
        View dialogView = inflater.inflate(R.layout.custom_dialog_wifi_pass, (ViewGroup) null);
        TextInputLayout txtInpLay = (TextInputLayout) dialogView.findViewById(R.id.input_layout_password);
        txtInpLay.setHint("Password for " + ssid);
        final EditText passTextView = (EditText) dialogView.findViewById(R.id.wifi_pass);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("WiFi Password");
        dialogBuilder.setPositiveButton("Connect", new DialogInterface.OnClickListener() { // from class: fatima.tech.espnetsmart.NetworkUtils.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                String pass = passTextView.getText().toString();
                if (!pass.isEmpty() && ssid != null) {
                    Log.d("dialogBuilder", "clicked");
                }
                NetworkUtils.ConnToNonCofNetwork(cntx, ssid, pass);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: fatima.tech.espnetsmart.NetworkUtils.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog inputPassDialog = dialogBuilder.create();
        inputPassDialog.show();
    }

    private static void sendMessage(Context cntx, String message, int color) {
        Intent intent = new Intent();
        intent.setAction("NetUtilSnack");
        intent.putExtra("message", message);
        intent.putExtra("color", color);
        LocalBroadcastManager.getInstance(cntx).sendBroadcast(intent);
    }
}
