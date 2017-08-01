package com.tts.starbar.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tts.starbar.R;

/**
 * Author: Jemy
 **/
public class StarBar extends View {
    private int starNumber;
    private int starPadding;
    private int starSize;
    private Drawable darkDrawable;
    private Drawable lightDrawable;
    private boolean isStarInteger;//is complete star
    private float starPoint;//分数
    private static final int DEFAULT_STAR_PADDING = 20;
    private static final int DEFAULT_PADDING = 30;
    private static final int DEFAULT_STAR_SIZE = 100;
    private Paint paint;
    private static final String TAG = "StarBar";

    public StarBar(Context context) {
        this(context, null);
    }

    public StarBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StarBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StarBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //widget width = star size +star padding+widget padding
        setMeasuredDimension(starSize * starNumber + starPadding * (starNumber - 1) + DEFAULT_PADDING * 2, starSize);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.starBar);
        starNumber = typedArray.getInteger(R.styleable.starBar_starNumber, 5);
        starPadding = typedArray.getDimensionPixelSize(R.styleable.starBar_starPadding, DEFAULT_STAR_PADDING);
        starSize = typedArray.getDimensionPixelSize(R.styleable.starBar_starSize, DEFAULT_STAR_SIZE);
        darkDrawable = typedArray.getDrawable(R.styleable.starBar_starDark);
        lightDrawable = typedArray.getDrawable(R.styleable.starBar_starLight);
        isStarInteger = typedArray.getBoolean(R.styleable.starBar_isStarInteger, false);
        typedArray.recycle();

        setClickable(true);
        initPaint();
    }

    private void initPaint() {
        //change drawable to bitmap
        paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        lightDrawable.setBounds(0,0,starSize,starSize);
        lightDrawable.draw(canvas);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: starPoint" + starPoint);
        if (darkDrawable == null || lightDrawable == null) {
            return;
        }
        //drawable background dark star
        for (int i = 0; i < starNumber; i++) {
            darkDrawable.setBounds((starSize + starPadding) * i + DEFAULT_PADDING, 0,
                    (starSize + starPadding) * i + starSize + DEFAULT_PADDING, starSize);
            darkDrawable.draw(canvas);
        }
        //drawable light star
        if (starPoint <= 0) {
            return;
        }
        if (isStarInteger) {
            //show int star
            for (int i = 0; i < starPoint; i++) {
                lightDrawable.setBounds((starSize + starPadding) * i + DEFAULT_PADDING, 0,
                        (starSize + starPadding) * i + starSize + DEFAULT_PADDING, starSize);
                lightDrawable.draw(canvas);
            }
        } else {
            int completeCount = (int) Math.floor(starPoint);
            Log.d(TAG, "onDraw completeCount: " + completeCount);
            //drawable complete star
            for (int i = 0; i < completeCount; i++) {
                lightDrawable.setBounds((starSize + starPadding) * i + DEFAULT_PADDING, 0,
                        (starSize + starPadding) * i + starSize + DEFAULT_PADDING, starSize);
                lightDrawable.draw(canvas);
            }
            //draw fraction star
            float fractionCount = Math.round((starPoint - completeCount) * 10) * 1.0f / 10;
            Log.d(TAG, "onDraw fractionCount: " + fractionCount);
//            canvas.drawRect(new RectF(DEFAULT_PADDING + (starSize + starPadding) * completeCount, 0,
//                    DEFAULT_PADDING + (starSize + starPadding) * completeCount+starSize * fractionCount, starSize), paint);
            canvas.translate(DEFAULT_PADDING+(starSize + starPadding) * completeCount,0);
            canvas.drawRect(new RectF(0,0,starSize*fractionCount,starSize),paint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getStarPoint(event);
                break;
            case MotionEvent.ACTION_MOVE:
                getStarPoint(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void getStarPoint(MotionEvent event) {
        float downX = event.getX();
        //calculate star point,max starNumber
        starPoint = downX < starPadding ? 0 : Math.min(starNumber * (downX / getWidth()), starNumber);
        invalidate();
    }
}
