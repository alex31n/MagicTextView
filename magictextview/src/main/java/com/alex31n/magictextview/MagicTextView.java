package com.alex31n.magictextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.WeakHashMap;

public class MagicTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Shadow outerShadows;
    private Shadow innerShadows;

    private Drawable foregroundDrawable;

    private Stroke stroke;

    private int[] lockedCompoundPadding;
    private boolean frozen = false;

    private WeakHashMap<String, Pair<Canvas, Bitmap>> canvasStore =new WeakHashMap<String, Pair<Canvas, Bitmap>>();;
    private Canvas tempCanvas;
    private Bitmap tempBitmap;

    public MagicTextView(@NonNull Context context) {
        super(context);
        initView();
    }

    public MagicTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs);
        initView();
    }

    public MagicTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttributes(context, attrs);
        initView();
    }

    private void initView() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void processAttributes(final Context context, final AttributeSet attrs) {
        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.MagicTextView, 0, 0);
        initAttributes(attrsArray);
        attrsArray.recycle();
    }

    private void initAttributes(TypedArray typedArray) {
        if (typedArray == null) {
            return;
        }
        if (canvasStore == null) {
            canvasStore = new WeakHashMap<String, Pair<Canvas, Bitmap>>();
        }

        if (typedArray.hasValue(R.styleable.MagicTextView_mtv_foreground)) {
            Drawable foreground = typedArray.getDrawable(R.styleable.MagicTextView_mtv_foreground);
            if (foreground != null) {
                this.setForegroundDrawable(foreground);
            } else {
                this.setTextColor(typedArray.getColor(R.styleable.MagicTextView_mtv_foreground, 0xff000000));
            }
        }

        // inner shadow
        if (typedArray.hasValue(R.styleable.MagicTextView_mtv_innerShadowColor)) {
            this.setInnerShadows(typedArray.getDimensionPixelSize(R.styleable.MagicTextView_mtv_innerShadowRadius, 0),
                    typedArray.getDimensionPixelOffset(R.styleable.MagicTextView_mtv_innerShadowDx, 0),
                    typedArray.getDimensionPixelOffset(R.styleable.MagicTextView_mtv_innerShadowDy, 0),
                    typedArray.getColor(R.styleable.MagicTextView_mtv_innerShadowColor, 0xff000000));
        }

        // outer shadow
        if (typedArray.hasValue(R.styleable.MagicTextView_mtv_outerShadowColor)) {
            this.setOuterShadows(typedArray.getDimensionPixelSize(R.styleable.MagicTextView_mtv_outerShadowRadius, 0),
                    typedArray.getDimensionPixelOffset(R.styleable.MagicTextView_mtv_outerShadowDx, 0),
                    typedArray.getDimensionPixelOffset(R.styleable.MagicTextView_mtv_outerShadowDy, 0),
                    typedArray.getColor(R.styleable.MagicTextView_mtv_outerShadowColor, 0xff000000));
        }

        // text stroke
        if (typedArray.hasValue(R.styleable.MagicTextView_mtv_strokeColor)) {
            float strokeWidth = typedArray.getDimensionPixelSize(R.styleable.MagicTextView_mtv_strokeWidth, 1);
            int strokeColor = typedArray.getColor(R.styleable.MagicTextView_mtv_strokeColor, 0xff000000);
            float strokeMiter = typedArray.getDimensionPixelSize(R.styleable.MagicTextView_mtv_strokeMiter, 10);
            Paint.Join strokeJoin = null;
            switch (typedArray.getInt(R.styleable.MagicTextView_mtv_strokeJoinStyle, 0)) {
                case (0):
                    strokeJoin = Paint.Join.MITER;
                    break;
                case (1):
                    strokeJoin = Paint.Join.BEVEL;
                    break;
                case (2):
                    strokeJoin = Paint.Join.ROUND;
                    break;
            }
            this.setStroke(strokeWidth, strokeColor, strokeJoin, strokeMiter);
        }

//        if (innerShadows != null || foregroundDrawable != null) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        freeze();

        Drawable restoreBackground = this.getBackground();
        Drawable[] restoreDrawables = this.getCompoundDrawables();
        int restoreColor = this.getCurrentTextColor();

        this.setCompoundDrawables(null, null, null, null);

        if (outerShadows != null) {
            this.setShadowLayer(outerShadows.getRadius(), outerShadows.getDx(), outerShadows.getDy(), outerShadows.getColor());
            super.onDraw(canvas);
        }

        this.setShadowLayer(0, 0, 0, 0);
        this.setTextColor(restoreColor);

        if (this.foregroundDrawable != null && this.foregroundDrawable instanceof BitmapDrawable) {
            generateTempCanvas();
            super.onDraw(tempCanvas);
            Paint paint = ((BitmapDrawable) this.foregroundDrawable).getPaint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            this.foregroundDrawable.setBounds(canvas.getClipBounds());
            this.foregroundDrawable.draw(tempCanvas);
            canvas.drawBitmap(tempBitmap, 0, 0, null);
            tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        if (stroke != null) {
            TextPaint paint = this.getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(stroke.getStrokeJoin());
            paint.setStrokeMiter(stroke.getStrokeMiter());
            this.setTextColor(stroke.getStrokeColor());
            paint.setStrokeWidth(stroke.getStrokeWidth());
//            canvas.drawPaint(paint);
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
            this.setTextColor(restoreColor);
        }

        if (innerShadows != null) {
            generateTempCanvas();
            TextPaint paint = this.getPaint();
            this.setTextColor(innerShadows.getColor());
            super.onDraw(tempCanvas);
            this.setTextColor(0xFF000000);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            paint.setMaskFilter(new BlurMaskFilter(innerShadows.getRadius(), BlurMaskFilter.Blur.NORMAL));

            tempCanvas.save();
            tempCanvas.translate(innerShadows.getDx(), innerShadows.getDy());

            super.onDraw(tempCanvas);
            tempCanvas.restore();
            canvas.drawBitmap(tempBitmap, 0, 0, null);
            tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            paint.setXfermode(null);
            paint.setMaskFilter(null);
            this.setTextColor(restoreColor);
            this.setShadowLayer(0, 0, 0, 0);
        }


        if (restoreDrawables != null) {
            this.setCompoundDrawablesWithIntrinsicBounds(restoreDrawables[0], restoreDrawables[1], restoreDrawables[2], restoreDrawables[3]);
        }
        this.setBackgroundDrawable(restoreBackground);
        this.setTextColor(restoreColor);

        unfreeze();
    }

    private void generateTempCanvas() {
        String key = String.format("%dx%d", getWidth(), getHeight());
        Pair<Canvas, Bitmap> stored = canvasStore.get(key);
        if (stored != null) {
            tempCanvas = stored.first;
            tempBitmap = stored.second;
        } else {
            tempCanvas = new Canvas();
            tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            tempCanvas.setBitmap(tempBitmap);
            canvasStore.put(key, new Pair<Canvas, Bitmap>(tempCanvas, tempBitmap));
        }
    }

    // Keep these things locked while onDraw in processing
    public void freeze() {
        lockedCompoundPadding = new int[]{
                getCompoundPaddingLeft(),
                getCompoundPaddingRight(),
                getCompoundPaddingTop(),
                getCompoundPaddingBottom()
        };
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    @Override
    public void requestLayout() {
        if (!frozen) super.requestLayout();
    }

    @Override
    public void postInvalidate() {
        if (!frozen) super.postInvalidate();
    }

    @Override
    public void postInvalidate(int left, int top, int right, int bottom) {
        if (!frozen) super.postInvalidate(left, top, right, bottom);
    }

    @Override
    public void invalidate() {
        if (!frozen) super.invalidate();
    }

    @Override
    public void invalidate(Rect rect) {
        if (!frozen) super.invalidate(rect);
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        if (!frozen) super.invalidate(l, t, r, b);
    }

    @Override
    public int getCompoundPaddingLeft() {
        return !frozen ? super.getCompoundPaddingLeft() : lockedCompoundPadding[0];
    }

    @Override
    public int getCompoundPaddingRight() {
        return !frozen ? super.getCompoundPaddingRight() : lockedCompoundPadding[1];
    }

    @Override
    public int getCompoundPaddingTop() {
        return !frozen ? super.getCompoundPaddingTop() : lockedCompoundPadding[2];
    }

    @Override
    public int getCompoundPaddingBottom() {
        return !frozen ? super.getCompoundPaddingBottom() : lockedCompoundPadding[3];
    }


    public void setOuterShadows(Shadow outerShadows) {
        this.outerShadows = outerShadows;
    }

    public void setOuterShadows(float radius, float dx, float dy, int color) {
        if (radius == 0) {
            radius = 0.0001f;
        }
        this.setOuterShadows(new Shadow(radius, dx, dy, color));
    }

    public void setInnerShadows(Shadow innerShadows) {
        this.innerShadows = innerShadows;
    }

    public void setInnerShadows(float radius, float dx, float dy, int color) {
        if (radius == 0) {
            radius = 0.0001f;
        }
        this.setInnerShadows(new Shadow(radius, dx, dy, color));
    }

    public void clearInnerShadows() {
        innerShadows = null;
    }

    public void setForegroundDrawable(Drawable foregroundDrawable) {
        this.foregroundDrawable = foregroundDrawable;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public void setStroke(float width, int color, Paint.Join join, float miter) {
        this.setStroke(new Stroke(width, color, join, miter));
    }
}
