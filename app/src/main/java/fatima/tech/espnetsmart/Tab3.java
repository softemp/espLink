package fatima.tech.espnetsmart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Tab3 extends Fragment {
    ColorArcProgressBar arcProgBar1;
    ColorArcProgressBar arcProgBar2;
    TextView[] tv = new TextView[6];
    int batteryRating = 12;
    int acRating = 220;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        this.tv[0] = (TextView) view.findViewById(R.id.sensors_textView12);
        this.tv[1] = (TextView) view.findViewById(R.id.sensors_textView13);
        this.tv[2] = (TextView) view.findViewById(R.id.sensors_textView22);
        this.tv[3] = (TextView) view.findViewById(R.id.sensors_textView23);
        this.tv[4] = (TextView) view.findViewById(R.id.sensors_textView32);
        this.tv[5] = (TextView) view.findViewById(R.id.sensors_textView33);
        this.arcProgBar1 = (ColorArcProgressBar) view.findViewById(R.id.sensors_arcProgess21);
        this.arcProgBar2 = (ColorArcProgressBar) view.findViewById(R.id.sensors_arcProgess22);
        this.batteryRating = Integer.parseInt(Util.loadStringPref(getContext(), "battery_rating_list", String.valueOf(this.batteryRating)));
        this.arcProgBar1.setMaxValues(this.batteryRating + (this.batteryRating / 2));
        this.arcProgBar1.setCurrentValues(0.0f);
        this.acRating = Integer.parseInt(Util.loadStringPref(getContext(), "ac_power_rating_list", String.valueOf(this.acRating)));
        if (this.acRating == 110) {
            this.arcProgBar2.setMaxValues(200.0f);
        } else {
            this.arcProgBar2.setMaxValues(300.0f);
        }
        this.arcProgBar2.setCurrentValues(0.0f);
        return view;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
    }

    public void dataFromMainActivity(String inMessage) {
        int[] myColor = {R.color.md_red_500, R.color.md_green_500, R.color.md_blue_500, R.color.md_orange_500};
        try {
            JSONObject jsonRoot = new JSONObject(inMessage);
            float prvBattV = (float) (((double) jsonRoot.getInt("battV")) / 10.0d);
            int prvSupV = jsonRoot.getInt("supV");
            this.arcProgBar1.setCurrentValues(prvBattV);
            this.arcProgBar2.setCurrentValues(prvSupV);
            int luxV = jsonRoot.getInt("lux");
            int HumV = jsonRoot.getInt("hum");
            int tempV = jsonRoot.getInt("temp");
            int colInd = 2;
            String strValue = "Failed to update";
            if (luxV <= 5) {
                colInd = 0;
                strValue = "Dark";
            } else if (luxV > 5 && luxV <= 25) {
                colInd = 2;
                strValue = "Dim light";
            } else if (luxV > 25 && luxV <= 500) {
                colInd = 1;
                strValue = "Bright";
            } else if (luxV > 500) {
                colInd = 3;
                strValue = "Sunny (very bright)";
            }
            this.tv[0].setText(strValue);
            this.tv[1].setText(String.format(Locale.US, "%d Lux", Integer.valueOf(luxV)));
            this.tv[1].setTextColor(ContextCompat.getColor(getActivity(), myColor[colInd]));
            int colInd2 = 2;
            String strValue2 = "Failed to update";
            if (HumV < 30) {
                colInd2 = 0;
                strValue2 = "Very dry";
            } else if (HumV > 30 && HumV < 45) {
                colInd2 = 2;
                strValue2 = "Dry";
            } else if (HumV >= 45 && HumV <= 60) {
                colInd2 = 1;
                strValue2 = "Normal";
            } else if (HumV > 60 && HumV <= 90) {
                colInd2 = 3;
                strValue2 = "Humid";
            } else if (HumV > 90) {
                colInd2 = 0;
                strValue2 = "Saturated";
            }
            this.tv[2].setText(strValue2);
            this.tv[3].setText(String.format(Locale.US, "%d %%RH", Integer.valueOf(HumV)));
            this.tv[3].setTextColor(ContextCompat.getColor(getActivity(), myColor[colInd2]));
            int colInd3 = 2;
            String strValue3 = "Failed to update";
            if (tempV <= 50) {
                colInd3 = 0;
                strValue3 = "Very cold";
            } else if (tempV > 50 && tempV <= 180) {
                colInd3 = 3;
                strValue3 = "Cold";
            } else if (tempV > 180 && tempV <= 300) {
                colInd3 = 1;
                strValue3 = "Normal";
            } else if (tempV > 300 && tempV <= 400) {
                colInd3 = 2;
                strValue3 = "Warm";
            } else if (tempV > 400) {
                colInd3 = 0;
                strValue3 = "Very Hot";
            }
            this.tv[4].setText(strValue3);
            this.tv[5].setText(String.format("%s °C", String.format(Locale.US, "%.1f", Double.valueOf(((double) tempV) / 10.0d))));
            this.tv[5].setTextColor(ContextCompat.getColor(getActivity(), myColor[colInd3]));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
