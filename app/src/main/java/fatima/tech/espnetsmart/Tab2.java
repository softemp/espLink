package fatima.tech.espnetsmart;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class Tab2 extends Fragment {
    mainActivityListener callbackToMainActivity;
    public Context contx;
    long offsetT;
    Handler timerHandler;
    Runnable timerRunnable;
    int[] btnS = {0, 0, 0};
    int[] btnM = {0, 0, 0};
    long[] runT = new long[3];
    long[] onTimeSec = new long[3];
    long[] offTimeSec = new long[3];
    TextView[] tv = new TextView[20];
    ToggleButton[] tb = new ToggleButton[4];
    TriToggleButton[] trb = new TriToggleButton[4];
    int[] desBtn = {R.string.str_switch_2_sum, R.string.str_switch_3_sum, R.string.str_switch_4_sum};
    private View.OnClickListener onClickListenerButton = new View.OnClickListener() { // from class: fatima.tech.espnetsmart.Tab2.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Tab2.this.callbackToMainActivity.buttonClick(view);
        }
    };
    private View.OnClickListener onClickListenerTimerOn = new View.OnClickListener() { // from class: fatima.tech.espnetsmart.Tab2.3
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Tab2.this.callbackToMainActivity.timerTextClick(view, true);
        }
    };
    private View.OnClickListener onClickListenerTimerOff = new View.OnClickListener() { // from class: fatima.tech.espnetsmart.Tab2.4
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Tab2.this.callbackToMainActivity.timerTextClick(view, false);
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
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        this.tb[0] = (ToggleButton) view.findViewById(R.id.light_toggleButton00);
        this.tb[1] = (ToggleButton) view.findViewById(R.id.light_toggleButton10);
        this.tb[2] = (ToggleButton) view.findViewById(R.id.light_toggleButton20);
        this.tb[3] = (ToggleButton) view.findViewById(R.id.light_toggleButton30);
        this.trb[0] = (TriToggleButton) view.findViewById(R.id.light_triToggleButton00);
        this.trb[1] = (TriToggleButton) view.findViewById(R.id.light_triToggleButton10);
        this.trb[2] = (TriToggleButton) view.findViewById(R.id.light_triToggleButton20);
        this.trb[3] = (TriToggleButton) view.findViewById(R.id.light_triToggleButton30);
        this.tv[2] = (TextView) view.findViewById(R.id.light_textView02);
        this.tv[3] = (TextView) view.findViewById(R.id.light_textView03);
        this.tv[5] = (TextView) view.findViewById(R.id.light_textView10);
        this.tv[6] = (TextView) view.findViewById(R.id.light_textView11);
        this.tv[7] = (TextView) view.findViewById(R.id.light_textView12);
        this.tv[8] = (TextView) view.findViewById(R.id.light_textView13);
        this.tv[9] = (TextView) view.findViewById(R.id.light_textView14);
        this.tv[10] = (TextView) view.findViewById(R.id.light_textView20);
        this.tv[11] = (TextView) view.findViewById(R.id.light_textView21);
        this.tv[12] = (TextView) view.findViewById(R.id.light_textView22);
        this.tv[13] = (TextView) view.findViewById(R.id.light_textView23);
        this.tv[14] = (TextView) view.findViewById(R.id.light_textView24);
        this.tv[15] = (TextView) view.findViewById(R.id.light_textView30);
        this.tv[16] = (TextView) view.findViewById(R.id.light_textView31);
        this.tv[17] = (TextView) view.findViewById(R.id.light_textView32);
        this.tv[18] = (TextView) view.findViewById(R.id.light_textView33);
        this.tv[19] = (TextView) view.findViewById(R.id.light_textView34);
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                this.tv[i * 5].setText(Util.loadStringPref(this.contx, "switchName" + (i + 1), getString(this.desBtn[i - 1])));
            }
            this.tb[i].setOnClickListener(this.onClickListenerButton);
            this.trb[i].setOnClickListener(this.onClickListenerButton);
            this.tv[(i * 5) + 2].setOnClickListener(this.onClickListenerTimerOn);
            this.tv[(i * 5) + 3].setOnClickListener(this.onClickListenerTimerOff);
        }
        this.timerHandler = new Handler();
        this.timerRunnable = new Runnable() { // from class: fatima.tech.espnetsmart.Tab2.1
            @Override // java.lang.Runnable
            public void run() {
                String runningTime;
                Tab2.this.timerHandler.postDelayed(this, 1000L);
                for (int i2 = 0; i2 < 3; i2++) {
                    String timerStr = "";
                    if (Tab2.this.btnS[i2] == 1) {
                        runningTime = "ON since " + Util.calculateRunTime(Tab2.this.contx, Tab2.this.runT[i2], false);
                        if (Tab2.this.btnM[i2] == 1) {
                            timerStr = Util.loadStringPref(Tab2.this.contx, "light_cutoff", "0") + " Lux";
                        } else if (Tab2.this.btnM[i2] == 2) {
                            timerStr = Util.calculateRemTime(Tab2.this.contx, Tab2.this.btnS[i2], Tab2.this.offTimeSec[i2], Tab2.this.onTimeSec[i2]);
                        }
                    } else {
                        runningTime = "OFF since " + Util.calculateRunTime(Tab2.this.contx, Tab2.this.runT[i2], false);
                        if (Tab2.this.btnM[i2] == 1) {
                            timerStr = Util.loadStringPref(Tab2.this.contx, "light_cuton", "0") + " Lux";
                        } else if (Tab2.this.btnM[i2] == 2) {
                            timerStr = Util.calculateRemTime(Tab2.this.contx, Tab2.this.btnS[i2], Tab2.this.offTimeSec[i2], Tab2.this.onTimeSec[i2]);
                        }
                    }
                    Tab2.this.tv[(i2 * 5) + 6].setText(runningTime);
                    Tab2.this.tv[(i2 * 5) + 9].setText(timerStr);
                }
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
            String timerOnStr = "";
            String timerOffStr = "";
            for (int i = 0; i < 3; i++) {
                String keyStr = "btnID" + (i + 1);
                JSONObject jsonbtnID0 = jsonRoot.getJSONObject(keyStr);
                this.btnS[i] = jsonbtnID0.getInt("btnS");
                this.btnM[i] = jsonbtnID0.getInt("btnM");
                if (this.btnS[i] == 1) {
                    this.tb[i + 1].setChecked(true);
                } else {
                    this.tb[i + 1].setChecked(false);
                }
                this.trb[i + 1].setState(this.btnM[i]);
                int onH = jsonbtnID0.getInt("onH");
                int onM = jsonbtnID0.getInt("onM");
                int offH = jsonbtnID0.getInt("offH");
                int offM = jsonbtnID0.getInt("offM");
                this.runT[i] = jsonbtnID0.getLong("runT") * 1000;
                this.offsetT = Util.loadLongPref(this.contx, "offsetT", 0L);
                this.onTimeSec[i] = ((long) ((onH * 3600) + (onM * 60))) + (this.offsetT / 1000);
                this.offTimeSec[i] = ((long) ((offH * 3600) + (offM * 60))) + (this.offsetT / 1000);
                this.timerHandler.removeCallbacks(this.timerRunnable);
                this.timerHandler.postDelayed(this.timerRunnable, 0L);
                timerOnStr = String.format(Locale.US, "%02d", Integer.valueOf(onH)) + ":" + String.format(Locale.US, "%02d", Integer.valueOf(onM));
                timerOffStr = String.format(Locale.US, "%02d", Integer.valueOf(offH)) + ":" + String.format(Locale.US, "%02d", Integer.valueOf(offM));
                this.tv[(i * 5) + 7].setText(timerOnStr);
                this.tv[(i * 5) + 8].setText(timerOffStr);
            }
            if (this.btnS[0] == this.btnS[1] && this.btnS[1] == this.btnS[2] && this.btnS[0] == 1) {
                this.tb[0].setChecked(true);
            } else {
                this.tb[0].setChecked(false);
            }
            if (this.btnM[0] == this.btnM[1] && this.btnM[1] == this.btnM[2]) {
                this.trb[0].setState(this.btnM[0]);
            } else {
                this.trb[0].setState(0);
            }
            if (this.onTimeSec[0] == this.onTimeSec[1] && this.onTimeSec[1] == this.onTimeSec[2]) {
                this.tv[2].setText(timerOnStr);
            }
            if (this.offTimeSec[0] == this.offTimeSec[1] && this.offTimeSec[1] == this.offTimeSec[2]) {
                this.tv[3].setText(timerOffStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
