package fatima.tech.espnetsmart;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/* JADX INFO: loaded from: classes.dex */
public class RateItDialogFragment extends DialogFragment {
    private static final int DAYS_UNTIL_PROMPT = 1;
    private static final String DISABLED = "DISABLED";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final int LAUNCHES_UNTIL_PROMPT = 2;
    private static final int MILLIS_UNTIL_PROMPT = 60000;
    private static final String PREF_NAME = "APP_RATER";

    public static void show(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = sharedPreferences.getLong(LAST_PROMPT, 0L);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
            editor.putLong(LAST_PROMPT, lastPromptTime);
        }
        if (!sharedPreferences.getBoolean(DISABLED, false)) {
            int launches = sharedPreferences.getInt(LAUNCHES, 0) + 1;
            if (launches > 2 && currentTime > 60000 + lastPromptTime) {
                shouldShow = true;
            }
            editor.putInt(LAUNCHES, launches);
        }
        if (shouldShow) {
            editor.putInt(LAUNCHES, 0).putLong(LAST_PROMPT, System.currentTimeMillis()).apply();
            new RateItDialogFragment().show(fragmentManager, (String) null);
        } else {
            editor.apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }

    @Override // android.support.v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int tabColor = ResourcesCompat.getColor(getResources(), R.color.md_blue_500, null);
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(R.string.rate_title).titleColor(tabColor).iconRes(R.drawable.ic_stars_black_48dp).content(R.string.rate_message).positiveText(R.string.rate_positive).onPositive(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.RateItDialogFragment.3
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                RateItDialogFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + RateItDialogFragment.this.getActivity().getPackageName())));
                RateItDialogFragment.getSharedPreferences(RateItDialogFragment.this.getActivity()).edit().putBoolean(RateItDialogFragment.DISABLED, true).apply();
                RateItDialogFragment.this.dismiss();
            }
        }).neutralText(R.string.rate_remind_later).onNeutral(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.RateItDialogFragment.2
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                RateItDialogFragment.this.dismiss();
            }
        }).negativeText(R.string.rate_never).onNegative(new MaterialDialog.SingleButtonCallback() { // from class: fatima.tech.espnetsmart.RateItDialogFragment.1
            @Override // com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
            public void onClick(@NonNull MaterialDialog dialog2, @NonNull DialogAction which) {
                RateItDialogFragment.getSharedPreferences(RateItDialogFragment.this.getActivity()).edit().putBoolean(RateItDialogFragment.DISABLED, true).apply();
                RateItDialogFragment.this.dismiss();
            }
        }).build();
        dialog.getIconView().setColorFilter(tabColor);
        dialog.create();
        return dialog;
    }
}
