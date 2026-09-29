
package com.eternal.hexTocolor;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
 
/** Rounded hue slider with a draggable thumb. */
public class ColorPickerView extends View {
 
    public interface HueListner {
        void onHueChanged(float hue);
    }
 
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bar = new RectF();
    private final float density;
    private final float thumbR, trackH, pad;
 
    private float hue = 220f;
    private HueListner listner;
 
    public ColorPickerView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        density = ctx.getResources().getDisplayMetrics().density;
        thumbR = 13 * density;
        trackH = 16 * density;
        pad = thumbR + 3 * density;
        ringPaint.setColor(Color.WHITE);
        haloPaint.setColor(0x55000000);
    }
 
    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int h = (int) (44 * density);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), wSpec), h);
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float cy = h / 2f;
        bar.set(pad, cy - trackH / 2, w - pad, cy + trackH / 2);
        int[] colors = {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000};
        barPaint.setShader(new LinearGradient(bar.left, 0, bar.right, 0, colors, null, Shader.TileMode.CLAMP));
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        float r = trackH / 2;
        canvas.drawRoundRect(bar, r, r, barPaint);
 
        float cx = bar.left + (hue / 360f) * bar.width();
        float cy = getHeight() / 2f;
        fillPaint.setColor(Color.HSVToColor(new float[]{hue, 1f, 1f}));
        canvas.drawCircle(cx, cy + density, thumbR + 2 * density, haloPaint);
        canvas.drawCircle(cx, cy, thumbR, ringPaint);
        canvas.drawCircle(cx, cy, thumbR - 3.5f * density, fillPaint);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int a = e.getAction();
        if (a == MotionEvent.ACTION_DOWN || a == MotionEvent.ACTION_MOVE) {
            getParent().requestDisallowInterceptTouchEvent(true);
            float t = (e.getX() - bar.left) / bar.width();
            t = Math.max(0f, Math.min(1f, t));
            hue = t * 360f;
            invalidate();
            if (listner != null) listner.onHueChanged(hue);
        }
        return true;
    }
 
    public void setHue(float hue) {
        this.hue = hue;
        invalidate();
    }
 
    public float getHue() { return hue; }
 
    public void setHueListner(HueListner l) { this.listner = l; }
}
 
