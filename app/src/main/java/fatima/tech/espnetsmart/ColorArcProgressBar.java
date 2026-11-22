package fatima.tech.espnetsmart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.InputDeviceCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
public class ColorArcProgressBar extends View {
    private final int DEGREE_PROGRESS_DISTANCE;
    private Paint allArcPaint;
    private int aniSpeed;
    private String bgArcColor;
    private float bgArcWidth;
    private RectF bgRect;
    private float centerX;
    private float centerY;
    private int[] colors;
    private Paint curSpeedPaint;
    private float curSpeedSize;
    private float curValues;
    private float currentAngle;
    private Paint degreePaint;
    private int diameter;
    private int hintColor;
    private Paint hintPaint;
    private float hintSize;
    private String hintString;
    private boolean isFloat;
    private boolean isNeedContent;
    private boolean isNeedDial;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isShowCurrentSpeed;
    private float k;
    private float lastAngle;
    private String longDegreeColor;
    private float longdegree;
    private PaintFlagsDrawFilter mDrawFilter;
    private int mHeight;
    private int mWidth;
    private float maxValues;
    private ValueAnimator progressAnimator;
    private Paint progressPaint;
    private float progressWidth;
    private Matrix rotateMatrix;
    private String shortDegreeColor;
    private float shortdegree;
    private float startAngle;
    private float sweepAngle;
    private SweepGradient sweepGradient;
    private float textSize;
    private int titleColor;
    private String titleString;
    private int txtColor;
    private Paint vTextPaint;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        this.diameter = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.bgRect = new RectF();
        this.startAngle = 135.0f;
        this.sweepAngle = 270.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -16711936, -16711936, -16711936, -16711936, -16711936, -16711936};
        this.maxValues = 100.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(8.0f);
        this.progressWidth = dipToPx(8.0f);
        this.textSize = dipToPx(28.0f);
        this.hintSize = dipToPx(14.0f);
        this.curSpeedSize = dipToPx(20.0f);
        this.aniSpeed = 1000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = ContextCompat.getColor(getContext(), R.color.textColorSecondary);
        this.titleColor = ContextCompat.getColor(getContext(), R.color.textColorPrimary);
        this.txtColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#362C38";
        this.isShowCurrentSpeed = true;
        this.isFloat = false;
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.diameter = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.bgRect = new RectF();
        this.startAngle = 135.0f;
        this.sweepAngle = 270.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -16711936, -16711936, -16711936, -16711936, -16711936, -16711936};
        this.maxValues = 100.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(8.0f);
        this.progressWidth = dipToPx(8.0f);
        this.textSize = dipToPx(28.0f);
        this.hintSize = dipToPx(14.0f);
        this.curSpeedSize = dipToPx(20.0f);
        this.aniSpeed = 1000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = ContextCompat.getColor(getContext(), R.color.textColorSecondary);
        this.titleColor = ContextCompat.getColor(getContext(), R.color.textColorPrimary);
        this.txtColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#362C38";
        this.isShowCurrentSpeed = true;
        this.isFloat = false;
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.diameter = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.bgRect = new RectF();
        this.startAngle = 135.0f;
        this.sweepAngle = 270.0f;
        this.currentAngle = 0.0f;
        this.colors = new int[]{-16711936, -16711936, -16711936, -16711936, -16711936, -16711936, -16711936};
        this.maxValues = 100.0f;
        this.curValues = 0.0f;
        this.bgArcWidth = dipToPx(8.0f);
        this.progressWidth = dipToPx(8.0f);
        this.textSize = dipToPx(28.0f);
        this.hintSize = dipToPx(14.0f);
        this.curSpeedSize = dipToPx(20.0f);
        this.aniSpeed = 1000;
        this.longdegree = dipToPx(13.0f);
        this.shortdegree = dipToPx(5.0f);
        this.DEGREE_PROGRESS_DISTANCE = dipToPx(8.0f);
        this.hintColor = ContextCompat.getColor(getContext(), R.color.textColorSecondary);
        this.titleColor = ContextCompat.getColor(getContext(), R.color.textColorPrimary);
        this.txtColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        this.longDegreeColor = "#111111";
        this.shortDegreeColor = "#111111";
        this.bgArcColor = "#362C38";
        this.isShowCurrentSpeed = true;
        this.isFloat = false;
        initCofig(context, attrs);
        initView();
    }

    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(1, SupportMenu.CATEGORY_MASK);
        int color2 = a.getColor(2, color1);
        int color3 = a.getColor(3, color1);
        int color4 = a.getColor(4, -16711936);
        int color5 = a.getColor(5, color4);
        int color6 = a.getColor(6, InputDeviceCompat.SOURCE_ANY);
        this.colors = new int[]{color1, color2, color3, color4, color5, color6, color6};
        this.bgArcColor = String.format("#%06X", Integer.valueOf(16777215 & a.getColor(0, InputDeviceCompat.SOURCE_ANY)));
        this.sweepAngle = a.getInteger(15, 270);
        this.bgArcWidth = a.getDimension(7, dipToPx(2.0f));
        this.progressWidth = a.getDimension(8, dipToPx(10.0f));
        this.isNeedTitle = a.getBoolean(9, false);
        this.isNeedContent = a.getBoolean(10, false);
        this.isNeedUnit = a.getBoolean(11, false);
        this.isNeedDial = a.getBoolean(12, false);
        this.hintString = a.getString(14);
        this.titleString = a.getString(13);
        this.curValues = a.getFloat(16, 0.0f);
        this.maxValues = a.getFloat(17, 100.0f);
        this.isFloat = a.getBoolean(18, false);
        setCurrentValues(this.curValues);
        setMaxValues(this.maxValues);
        a.recycle();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.bgRect.set(this.bgArcWidth / 2.0f, this.bgArcWidth / 2.0f, this.mWidth - (this.bgArcWidth / 2.0f), View.MeasureSpec.getSize(heightMeasureSpec) - (this.bgArcWidth / 2.0f));
        this.diameter = this.mWidth;
        this.centerX = this.mWidth / 2.0f;
        this.centerY = this.mWidth / 2.0f;
        this.startAngle = 90.0f + ((360.0f - this.sweepAngle) / 2.0f);
    }

    private void initView() {
        this.degreePaint = new Paint();
        this.degreePaint.setColor(Color.parseColor(this.longDegreeColor));
        this.allArcPaint = new Paint();
        this.allArcPaint.setAntiAlias(true);
        this.allArcPaint.setStyle(Paint.Style.STROKE);
        this.allArcPaint.setStrokeWidth(this.bgArcWidth);
        this.allArcPaint.setColor(Color.parseColor(this.bgArcColor));
        this.allArcPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint = new Paint();
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth(this.progressWidth);
        this.progressPaint.setColor(-16711936);
        this.vTextPaint = new Paint();
        this.vTextPaint.setTextSize(this.textSize);
        this.vTextPaint.setColor(this.txtColor);
        this.vTextPaint.setTextAlign(Paint.Align.CENTER);
        this.hintPaint = new Paint();
        this.hintPaint.setTextSize(this.hintSize);
        this.hintPaint.setColor(this.hintColor);
        this.hintPaint.setTextAlign(Paint.Align.CENTER);
        this.curSpeedPaint = new Paint();
        this.curSpeedPaint.setTextSize(this.curSpeedSize);
        this.curSpeedPaint.setColor(this.titleColor);
        this.curSpeedPaint.setTextAlign(Paint.Align.CENTER);
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(this.mDrawFilter);
        if (this.isNeedDial) {
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9.0f, this.centerX, this.centerY);
                } else {
                    if (i % 5 == 0) {
                        this.degreePaint.setStrokeWidth(dipToPx(2.0f));
                        this.degreePaint.setColor(Color.parseColor(this.longDegreeColor));
                        canvas.drawLine(this.centerX, ((this.centerY - (this.diameter / 2)) - (this.progressWidth / 2.0f)) - this.DEGREE_PROGRESS_DISTANCE, this.centerX, (((this.centerY - (this.diameter / 2)) - (this.progressWidth / 2.0f)) - this.DEGREE_PROGRESS_DISTANCE) - this.longdegree, this.degreePaint);
                    } else {
                        this.degreePaint.setStrokeWidth(dipToPx(1.4f));
                        this.degreePaint.setColor(Color.parseColor(this.shortDegreeColor));
                        canvas.drawLine(this.centerX, (((this.centerY - (this.diameter / 2)) - (this.progressWidth / 2.0f)) - this.DEGREE_PROGRESS_DISTANCE) - ((this.longdegree - this.shortdegree) / 2.0f), this.centerX, ((((this.centerY - (this.diameter / 2)) - (this.progressWidth / 2.0f)) - this.DEGREE_PROGRESS_DISTANCE) - ((this.longdegree - this.shortdegree) / 2.0f)) - this.shortdegree, this.degreePaint);
                    }
                    canvas.rotate(9.0f, this.centerX, this.centerY);
                }
            }
        }
        canvas.drawArc(this.bgRect, this.startAngle, this.sweepAngle, false, this.allArcPaint);
        float[] positions = {0.0f, 0.15f, 0.3f, 0.45f, 0.6f, 0.75f, 1.0f};
        this.sweepGradient = new SweepGradient(this.centerX, this.centerY, this.colors, positions);
        this.rotateMatrix = new Matrix();
        this.rotateMatrix.setRotate(130.0f, this.centerX, this.centerY);
        this.sweepGradient.setLocalMatrix(this.rotateMatrix);
        this.progressPaint.setShader(this.sweepGradient);
        canvas.drawArc(this.bgRect, this.startAngle, this.currentAngle, false, this.progressPaint);
        if (this.isNeedContent) {
            if (this.isFloat) {
                canvas.drawText(String.format("%.1f", Float.valueOf(this.curValues)) + this.hintString, this.centerX, this.centerY, this.vTextPaint);
            } else {
                canvas.drawText(String.format("%.0f", Float.valueOf(this.curValues)) + this.hintString, this.centerX, this.centerY, this.vTextPaint);
            }
        }
        if (this.isNeedUnit) {
            canvas.drawText("(0-" + String.format("%.0f", Float.valueOf(this.maxValues)) + ")", this.centerX, this.centerY + ((this.textSize * 2.0f) / 3.0f), this.hintPaint);
        }
        if (this.isNeedTitle) {
            canvas.drawText(this.titleString, this.centerX, getHeight() - (this.textSize / 4.0f), this.curSpeedPaint);
        }
        invalidate();
    }

    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        this.k = this.sweepAngle / maxValues;
    }

    public void setCurrentValues(float currentValues) {
        if (currentValues > this.maxValues) {
            currentValues = this.maxValues;
        }
        if (currentValues < 0.0f) {
            currentValues = 0.0f;
        }
        this.curValues = currentValues;
        this.lastAngle = this.currentAngle;
        setAnimation(this.lastAngle, this.k * currentValues, this.aniSpeed);
    }

    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    private void setTitle(String title) {
        this.titleString = title;
    }

    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    private void setAnimation(float last, float current, int length) {
        this.progressAnimator = ValueAnimator.ofFloat(last, current);
        this.progressAnimator.setDuration(length);
        this.progressAnimator.setTarget(Float.valueOf(this.currentAngle));
        this.progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: fatima.tech.espnetsmart.ColorArcProgressBar.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ColorArcProgressBar.this.currentAngle = ((Float) animation.getAnimatedValue()).floatValue();
                ColorArcProgressBar.this.curValues = ColorArcProgressBar.this.currentAngle / ColorArcProgressBar.this.k;
            }
        });
        this.progressAnimator.start();
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (((dip >= 0.0f ? 1 : -1) * 0.5f) + (dip * density));
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
