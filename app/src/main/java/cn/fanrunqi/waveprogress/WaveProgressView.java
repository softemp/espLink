package cn.fanrunqi.waveprogress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class WaveProgressView extends View {
    private static final int INVALIDATE = 1911;
    private float CurY;
    private int RefreshGap;
    private Bitmap backgroundBitmap;
    private int currentProgress;
    private String currentText;
    private float distance;
    private Handler handler;
    private int height;
    private Path mPath;
    private Paint mPathPaint;
    private String mTextColor;
    private Paint mTextPaint;
    private int mTextSize;
    private String mWaveColor;
    private float mWaveHalfWidth;
    private float mWaveHight;
    private int mWaveSpeed;
    private int maxProgress;
    private int width;

    public WaveProgressView(Context context) {
        this(context, null, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWaveHight = 20.0f;
        this.mWaveHalfWidth = 100.0f;
        this.mWaveColor = "#5be4ef";
        this.mWaveSpeed = 30;
        this.currentText = "";
        this.mTextColor = "#FFFFFF";
        this.mTextSize = 41;
        this.maxProgress = 100;
        this.currentProgress = 0;
        this.distance = 0.0f;
        this.RefreshGap = 10;
        this.handler = new Handler() { // from class: cn.fanrunqi.waveprogress.WaveProgressView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WaveProgressView.INVALIDATE /* 1911 */:
                        WaveProgressView.this.invalidate();
                        sendEmptyMessageDelayed(WaveProgressView.INVALIDATE, WaveProgressView.this.RefreshGap);
                        break;
                }
            }
        };
        Init();
    }

    public void setCurrent(int currentProgress, String currentText) {
        this.currentProgress = currentProgress;
        this.currentText = currentText;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setText(String mTextColor, int mTextSize) {
        this.mTextColor = mTextColor;
        this.mTextSize = mTextSize;
    }

    public void setWave(float mWaveHight, float mWaveWidth) {
        this.mWaveHight = mWaveHight;
        this.mWaveHalfWidth = mWaveWidth / 2.0f;
    }

    public void setWaveColor(String mWaveColor) {
        this.mWaveColor = mWaveColor;
    }

    public void setmWaveSpeed(int mWaveSpeed) {
        this.mWaveSpeed = mWaveSpeed;
    }

    private void Init() {
        if (getBackground() == null) {
            throw new IllegalArgumentException(String.format("background is null.", new Object[0]));
        }
        this.backgroundBitmap = getBitmapFromDrawable(getBackground());
        this.mPath = new Path();
        this.mPathPaint = new Paint();
        this.mPathPaint.setAntiAlias(true);
        this.mPathPaint.setStyle(Paint.Style.FILL);
        this.mTextPaint = new Paint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.handler.sendEmptyMessageDelayed(INVALIDATE, 100L);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = View.MeasureSpec.getSize(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        this.height = size;
        this.CurY = size;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.backgroundBitmap != null) {
            canvas.drawBitmap(createImage(), 0.0f, 0.0f, (Paint) null);
        }
    }

    private Bitmap createImage() {
        this.mPathPaint.setColor(Color.parseColor(this.mWaveColor));
        this.mTextPaint.setColor(Color.parseColor(this.mTextColor));
        this.mTextPaint.setTextSize(this.mTextSize);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBmp);
        float CurMidY = (this.height * (this.maxProgress - this.currentProgress)) / this.maxProgress;
        if (this.CurY > CurMidY) {
            this.CurY -= (this.CurY - CurMidY) / 10.0f;
        }
        this.mPath.reset();
        this.mPath.moveTo(0.0f - this.distance, this.CurY);
        int waveNum = (this.width / (((int) this.mWaveHalfWidth) * 4)) + 1;
        int multiplier = 0;
        for (int i = 0; i < waveNum * 3; i++) {
            this.mPath.quadTo((this.mWaveHalfWidth * (multiplier + 1)) - this.distance, this.CurY - this.mWaveHight, (this.mWaveHalfWidth * (multiplier + 2)) - this.distance, this.CurY);
            this.mPath.quadTo((this.mWaveHalfWidth * (multiplier + 3)) - this.distance, this.CurY + this.mWaveHight, (this.mWaveHalfWidth * (multiplier + 4)) - this.distance, this.CurY);
            multiplier += 4;
        }
        this.distance += this.mWaveHalfWidth / this.mWaveSpeed;
        this.distance %= this.mWaveHalfWidth * 4.0f;
        this.mPath.lineTo(this.width, this.height);
        this.mPath.lineTo(0.0f, this.height);
        this.mPath.close();
        canvas.drawPath(this.mPath, this.mPathPaint);
        int min = Math.min(this.width, this.height);
        this.backgroundBitmap = Bitmap.createScaledBitmap(this.backgroundBitmap, min, min, false);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(this.backgroundBitmap, 0.0f, 0.0f, paint);
        canvas.drawText(this.currentText, this.width / 2, this.height / 2, this.mTextPaint);
        return finalBmp;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
