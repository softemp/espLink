package fatima.tech.espnetsmart;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/* JADX INFO: loaded from: classes.dex */
public class TriToggleButton extends AppCompatButton {
    private static final String KEY_BUTTON_STATES = "button_states";
    private static final String KEY_INSTANCE_STATE = "instance_state";
    private int _state;
    private static final int[] STATE_ONE_SET = {R.attr.triToggleBtn_state_one};
    private static final int[] STATE_TWO_SET = {R.attr.triToggleBtn_state_two};
    private static final int[] STATE_THREE_SET = {R.attr.triToggleBtn_state_three};

    public TriToggleButton(Context context) {
        super(context);
        this._state = 0;
        setText("MANU");
    }

    public TriToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._state = 0;
        setText("MANU");
    }

    public TriToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._state = 0;
        setText("MANU");
    }

    @Override // android.view.View
    public boolean performClick() {
        nextState();
        return super.performClick();
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        if (this._state == 0) {
            mergeDrawableStates(drawableState, STATE_ONE_SET);
        } else if (this._state == 1) {
            mergeDrawableStates(drawableState, STATE_TWO_SET);
        } else if (this._state == 2) {
            mergeDrawableStates(drawableState, STATE_THREE_SET);
        }
        return drawableState;
    }

    public void setState(int state) {
        if (state > -1 && state < 3) {
            this._state = state;
            setButtonText();
        }
    }

    public int getState() {
        return this._state;
    }

    public void nextState() {
        this._state++;
        if (this._state > 2) {
            this._state = 0;
        }
        setButtonText();
    }

    public void previousState() {
        this._state--;
        if (this._state < 0) {
            this._state = 2;
        }
        setButtonText();
    }

    private void setButtonText() {
        switch (this._state) {
            case 0:
                setText("MANU");
                break;
            case 1:
                setText("AUTO");
                break;
            case 2:
                setText("TIMER");
                break;
            default:
                setText("Button");
                break;
        }
        refreshDrawableState();
    }

    private void showShortToast(String s) {
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(KEY_BUTTON_STATES, this._state);
        return bundle;
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setState(bundle.getInt(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }
}
