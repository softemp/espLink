package fatima.tech.espnetsmart;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

/* JADX INFO: loaded from: classes.dex */
public class Tab4 extends Fragment {
    Button bt;
    ToggleButton bt2;
    mainActivityListener callbackToMainActivity;
    EditText et;
    NestedScrollView nscr;
    TextView[] tv = new TextView[2];
    String ssid = "";
    String ip = "0.0.0.0";
    String gateway = "0.0.0.0";
    int port = 80;

    public interface mainActivityListener {
        void DataSendDeviceButtonClick(String str, String str2);

        void tab4IsReady();

        void textCopiedToClip();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.callbackToMainActivity = (mainActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement buttonClick");
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);
        setRetainInstance(true);
        this.nscr = (NestedScrollView) view.findViewById(R.id.logs_nested_scroll_view_1);
        this.et = (EditText) view.findViewById(R.id.logs_editText11);
        this.bt = (Button) view.findViewById(R.id.logs_button11);
        this.bt2 = (ToggleButton) view.findViewById(R.id.logs_button12);
        this.tv[1] = (TextView) view.findViewById(R.id.logs_textView13);
        this.bt.getBackground().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorTeal, null), PorterDuff.Mode.SRC_ATOP);
        SpannableString inData = new SpannableString(Util.partlyBoldandColoredText(getContext(), "Incoming and Outgoing Data Logs\n", "", R.color.textColorPrimary));
        inData.setSpan(new RelativeSizeSpan(1.3f), 0, "Incoming and Outgoing Data Logs\n".length(), 33);
        this.tv[1].setText(inData);
        this.tv[1].setOnLongClickListener(new View.OnLongClickListener() { // from class: fatima.tech.espnetsmart.Tab4.1
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) Tab4.this.getActivity().getApplicationContext().getSystemService("clipboard");
                String text = Tab4.this.tv[1].getText().toString();
                ClipData myClip = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(myClip);
                Tab4.this.callbackToMainActivity.textCopiedToClip();
                return false;
            }
        });
        this.bt.setOnClickListener(new View.OnClickListener() { // from class: fatima.tech.espnetsmart.Tab4.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Tab4.this.sendData();
            }
        });
        this.et.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: fatima.tech.espnetsmart.Tab4.3
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 6) {
                    return false;
                }
                InputMethodManager imm = (InputMethodManager) Tab4.this.getActivity().getSystemService("input_method");
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
        return view;
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.callbackToMainActivity.tab4IsReady();
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
    }

    public void dataFromMainActivity(String type, String msg) {
        if (!type.isEmpty()) {
            int color = R.color.md_blue_500;
            if (type.equalsIgnoreCase("IN")) {
                color = R.color.textColorSecondary;
                type = type + " (" + Util.currTime() + "): ";
                msg = msg + "\n";
            } else if (type.equalsIgnoreCase("OUT")) {
                color = R.color.md_teal_500;
                type = type + " (" + Util.currTime() + "): ";
            } else if (type.equalsIgnoreCase("INFO")) {
                color = R.color.md_blue_500;
                type = type + " (" + Util.currTime() + "): ";
            }
            this.tv[1].append(Util.partlyBoldandColoredText(getContext(), "\n" + type, msg, color));
            return;
        }
        this.tv[1].append(msg);
    }

    public void sendData() {
        String editText = this.et.getText().toString();
        if (!editText.isEmpty()) {
            this.et.getText().clear();
            this.et.onEditorAction(6);
            if (!this.bt2.isChecked()) {
                this.callbackToMainActivity.DataSendDeviceButtonClick("logDevice", editText);
            } else {
                this.callbackToMainActivity.DataSendDeviceButtonClick("logClients", editText);
            }
        }
    }
}
