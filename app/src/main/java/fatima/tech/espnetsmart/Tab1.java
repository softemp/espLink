package fatima.tech.espnetsmart;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.fanrunqi.waveprogress.WaveProgressView;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Tab1 extends Fragment {
    mainActivityListener callbackToMainActivity;
    public Context contx;
    long offTimeSec;
    long offsetT;
    long onTimeSec;
    long runT;
    ToggleButton tb;
    Handler timerHandler;
    Runnable timerRunnable;
    TriToggleButton trb;
    WaveProgressView wpv;
    int btnS = 0;
    int btnM = 0;
    TextView[] tv = new TextView[8];
    private View.OnClickListener onClickListener = new View.OnClickListener() { // from class: fatima.tech.espnetsmart.Tab1.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.pump_textView12 /* 2131624112 */:
                    Tab1.this.callbackToMainActivity.timerTextClick(view, true);
                    break;
                case R.id.pump_textView13 /* 2131624114 */:
                    Tab1.this.callbackToMainActivity.timerTextClick(view, false);
                    break;
                case R.id.pump_toggleButton10 /* 2131624116 */:
                case R.id.pump_triToggleButton10 /* 2131624118 */:
                    Tab1.this.callbackToMainActivity.buttonClick(view);
                    break;
            }
        }
    };

    public interface mainActivityListener {
        void buttonClick(View view);

        void timerTextClick(View view, boolean z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.contx = context;
        try {
            this.callbackToMainActivity = (mainActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement buttonClick");
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        this.tb = (ToggleButton) view.findViewById(R.id.pump_toggleButton10);
        this.trb = (TriToggleButton) view.findViewById(R.id.pump_triToggleButton10);
        this.tb.setOnClickListener(this.onClickListener);
        this.trb.setOnClickListener(this.onClickListener);
        this.tv[0] = (TextView) view.findViewById(R.id.pump_textView10);
        this.tv[1] = (TextView) view.findViewById(R.id.pump_textView11);
        this.tv[2] = (TextView) view.findViewById(R.id.pump_textView12);
        this.tv[3] = (TextView) view.findViewById(R.id.pump_textView13);
        this.tv[4] = (TextView) view.findViewById(R.id.pump_textView14);
        this.tv[2].setOnClickListener(this.onClickListener);
        this.tv[3].setOnClickListener(this.onClickListener);
        this.tv[5] = (TextView) view.findViewById(R.id.pump_textView20);
        this.tv[6] = (TextView) view.findViewById(R.id.pump_textView21);
        this.tv[7] = (TextView) view.findViewById(R.id.pump_textView22);
        this.wpv = (WaveProgressView) view.findViewById(R.id.water_level_progress);
        this.wpv.setText(String.format("#%06X", Integer.valueOf(ContextCompat.getColor(getActivity(), R.color.textColorPrimary) & ViewCompat.MEASURED_SIZE_MASK)), 86);
        this.wpv.setWaveColor(String.format("#%06X", Integer.valueOf(ContextCompat.getColor(getActivity(), R.color.colorRed) & ViewCompat.MEASURED_SIZE_MASK)));
        this.wpv.setCurrent(20, "%");
        this.wpv.setMaxProgress(100);
        this.tv[0].setText(Util.loadStringPref(this.contx, "switchName1", getString(R.string.str_switch_1_sum)));
        this.timerHandler = new Handler();
        this.timerRunnable = new Runnable() { // from class: fatima.tech.espnetsmart.Tab1.1
            @Override // java.lang.Runnable
            public void run() {
                String runningTime;
                Tab1.this.timerHandler.postDelayed(this, 1000L);
                String timerStr = "";
                if (Tab1.this.btnS == 1) {
                    runningTime = "ON since " + Util.calculateRunTime(Tab1.this.contx, Tab1.this.runT, false);
                    if (Tab1.this.btnM == 1) {
                        timerStr = "100 %";
                    } else if (Tab1.this.btnM == 2) {
                        timerStr = Util.calculateRemTime(Tab1.this.contx, Tab1.this.btnS, Tab1.this.offTimeSec, Tab1.this.onTimeSec);
                    }
                } else {
                    runningTime = "OFF since " + Util.calculateRunTime(Tab1.this.contx, Tab1.this.runT, false);
                    if (Tab1.this.btnM == 1) {
                        timerStr = Util.loadStringPref(Tab1.this.contx, "pump_auto_start_level", "80") + " %";
                    } else if (Tab1.this.btnM == 2) {
                        timerStr = Util.calculateRemTime(Tab1.this.contx, Tab1.this.btnS, Tab1.this.offTimeSec, Tab1.this.onTimeSec);
                    }
                }
                Tab1.this.tv[1].setText(runningTime);
                Tab1.this.tv[4].setText(timerStr);
            }
        };
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
        try {
            JSONObject jsonRoot = new JSONObject(inMessage);
            JSONObject jsonbtnID0 = jsonRoot.getJSONObject("btnID0");
            this.btnS = jsonbtnID0.getInt("btnS");
            this.btnM = jsonbtnID0.getInt("btnM");
            if (this.btnS == 1) {
                this.tb.setChecked(true);
            } else {
                this.tb.setChecked(false);
            }
            this.trb.setState(this.btnM);
            int onH = jsonbtnID0.getInt("onH");
            int onM = jsonbtnID0.getInt("onM");
            int offH = jsonbtnID0.getInt("offH");
            int offM = jsonbtnID0.getInt("offM");
            this.offsetT = Util.loadLongPref(this.contx, "offsetT", 0L);
            this.runT = jsonbtnID0.getLong("runT") * 1000;
            this.onTimeSec = ((long) ((onH * 3600) + (onM * 60))) + (this.offsetT / 1000);
            this.offTimeSec = ((long) ((offH * 3600) + (offM * 60))) + (this.offsetT / 1000);
            this.timerHandler.removeCallbacks(this.timerRunnable);
            this.timerHandler.postDelayed(this.timerRunnable, 0L);
            String timerOnStr = String.format(Locale.US, "%02d", Integer.valueOf(onH)) + ":" + String.format(Locale.US, "%02d", Integer.valueOf(onM));
            String timerOffStr = String.format(Locale.US, "%02d", Integer.valueOf(offH)) + ":" + String.format(Locale.US, "%02d", Integer.valueOf(offM));
            this.tv[2].setText(timerOnStr);
            this.tv[3].setText(timerOffStr);
            int tnkVol = jsonRoot.getInt("tnkVol");
            int tnkFVol = Integer.parseInt(Util.loadStringPref(this.contx, "tank_capacity", "500"));
            int perc = (tnkVol * 100) / tnkFVol;
            int colorVal = ContextCompat.getColor(getActivity(), R.color.colorRed);
            if (perc > 25 && perc <= 50) {
                colorVal = ContextCompat.getColor(getActivity(), R.color.colorOrange);
            } else if (perc > 50 && perc <= 75) {
                colorVal = ContextCompat.getColor(getActivity(), R.color.colorBlue);
            } else if (perc > 75) {
                colorVal = ContextCompat.getColor(getActivity(), R.color.colorGreen);
            }
            this.wpv.setWaveColor("#" + Integer.toHexString(colorVal));
            this.wpv.setCurrent(perc, perc + "%");
            this.tv[6].setText(String.valueOf("Full tank volume is " + tnkFVol + " Liters"));
            this.tv[7].setText(String.valueOf(tnkVol + " Liters"));
            this.tv[7].setTextColor(colorVal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
