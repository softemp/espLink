package fatima.tech.espnetsmart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/* JADX INFO: loaded from: classes.dex */
public class SendMail extends AsyncTask<Void, Void, Void> {
    private Context context;
    private String email;
    private String message;
    private ProgressDialog progressDialog;
    private Session session;
    private String subject;

    public SendMail(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = ProgressDialog.show(this.context, "Sending message", "Please wait...", false, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.progressDialog.dismiss();
        Toast.makeText(this.context, "Message Sent", 1).show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        this.session = Session.getDefaultInstance(props, new Authenticator() { // from class: fatima.tech.espnetsmart.SendMail.1
            @Override // javax.mail.Authenticator
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
            }
        });
        try {
            MimeMessage mm = new MimeMessage(this.session);
            mm.setFrom(new InternetAddress(this.email));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Config.recpEMAIL));
            mm.setSubject(this.subject + "-" + getApplicationName(this.context) + "-" + getAppVersion(this.context));
            mm.setText(this.message + "\n\n" + getHandsetInformation(this.context));
            Transport.send(mm);
            return null;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getApplicationName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }

    private static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "vX.XX";
        }
    }

    private String getHandsetInformation(Context context) {
        return "Sender Email: " + this.email + "\nBrand: " + Build.BRAND + "\nDevice: " + Build.DEVICE + "\nManufacturer: " + Build.MANUFACTURER + "\nModel: " + Build.MODEL + "\nScreen Density: " + getDeviceDensity(context) + "\nVersion SDK int: " + Build.VERSION.SDK_INT + "\nVersion codename: " + Build.VERSION.CODENAME + "\nVersion incremental: " + Build.VERSION.INCREMENTAL + "\n";
    }

    private static float getDeviceDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
