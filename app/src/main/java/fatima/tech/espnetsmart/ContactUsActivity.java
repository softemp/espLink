package fatima.tech.espnetsmart;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class ContactUsActivity extends AppCompatActivity {
    Button buttonSend;
    TextInputLayout layMessage;
    TextInputLayout laySendEmail;
    TextInputLayout laySubject;
    LinearLayout layoutFeed;
    EditText textFrom;
    EditText textMessage;
    EditText textSubject;
    EditText textTo;
    TextView thankYou;

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.buttonSend = (Button) findViewById(R.id.buttonSend);
        this.textTo = (EditText) findViewById(R.id.recipient_email);
        this.textFrom = (EditText) findViewById(R.id.sender_email);
        this.textSubject = (EditText) findViewById(R.id.subject);
        this.textMessage = (EditText) findViewById(R.id.message);
        this.laySendEmail = (TextInputLayout) findViewById(R.id.lay_sender_email);
        this.laySubject = (TextInputLayout) findViewById(R.id.lay_subject);
        this.layMessage = (TextInputLayout) findViewById(R.id.lay_message);
        this.layoutFeed = (LinearLayout) findViewById(R.id.email_login_form);
        this.thankYou = (TextView) findViewById(R.id.thank_you);
        this.textTo.setText(Config.dummyEMAIL);
        this.buttonSend.setOnClickListener(new View.OnClickListener() { // from class: fatima.tech.espnetsmart.ContactUsActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ContactUsActivity.this.sendEmail();
            }
        });
        this.laySendEmail.getEditText().addTextChangedListener(new TextWatcher() { // from class: fatima.tech.espnetsmart.ContactUsActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (!Patterns.EMAIL_ADDRESS.matcher(ContactUsActivity.this.textFrom.getText().toString()).matches()) {
                    ContactUsActivity.this.laySendEmail.setErrorEnabled(true);
                    ContactUsActivity.this.laySendEmail.setError("Provide valid email so later we can contact you if needed");
                } else {
                    ContactUsActivity.this.laySendEmail.setError(null);
                    ContactUsActivity.this.laySendEmail.setErrorEnabled(false);
                }
            }
        });
        this.laySubject.getEditText().addTextChangedListener(new TextWatcher() { // from class: fatima.tech.espnetsmart.ContactUsActivity.3
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    ContactUsActivity.this.laySubject.setErrorEnabled(true);
                    ContactUsActivity.this.laySubject.setError("Subject will help us to assist you faster");
                }
                if (s.length() > 0) {
                    ContactUsActivity.this.laySubject.setError(null);
                    ContactUsActivity.this.laySubject.setErrorEnabled(false);
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        this.layMessage.getEditText().addTextChangedListener(new TextWatcher() { // from class: fatima.tech.espnetsmart.ContactUsActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    ContactUsActivity.this.layMessage.setErrorEnabled(true);
                    ContactUsActivity.this.layMessage.setError("A short description will help us to identify problem faster");
                }
                if (s.length() > 0) {
                    ContactUsActivity.this.layMessage.setError(null);
                    ContactUsActivity.this.layMessage.setErrorEnabled(false);
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEmail() {
        String emailFrom = this.textFrom.getText().toString().trim();
        String subject = this.textSubject.getText().toString().trim();
        String message = this.textMessage.getText().toString().trim();
        if (TextUtils.isEmpty(emailFrom)) {
            this.laySendEmail.setError("Provide valid email so later we can contact you if needed");
        }
        if (TextUtils.isEmpty(subject)) {
            this.laySubject.setError("Subject will help us to assist you faster");
        }
        if (TextUtils.isEmpty(message)) {
            this.layMessage.setError("A short description will help us to identify problem faster");
        }
        if (Patterns.EMAIL_ADDRESS.matcher(this.textFrom.getText().toString()).matches() && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(message)) {
            SendMail sm = new SendMail(this, emailFrom, subject, message);
            sm.execute(new Void[0]);
            this.layoutFeed.setVisibility(8);
            this.thankYou.setVisibility(0);
        }
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

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contactus, menu);
        Util.coloringOptionMenuItem(this, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        Util.optionMenuActions(this, item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}
