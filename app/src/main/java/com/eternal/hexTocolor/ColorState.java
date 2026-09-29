

package com.eternal.hexTocolor;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
 
/** Rounded saturation / brightness square with a draggable thumb. */
public class ColorState extends View {
 
    public interface FinalInterface {
        void onFinalColorChange(int color);
    }
 
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final float density;
    private final float radius, thumbR;
 
    private float hue = 220f, sat = 0.75f, val = 0.95f;
    private FinalInterface final1;
 
    public ColorState(Context ctx, AttributeSet atr) {
        super(ctx, atr);
        density = ctx.getResources().getDisplayMetrics().density;
        radius = 20 * density;
        thumbR = 13 * density;
        ringPaint.setColor(Color.WHITE);
        haloPaint.setColor(0x55000000);
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rect.set(0, 0, w, h);
        buildShader();
    }
 
    private void buildShader() {
        if (rect.width() <= 0) return;
        Shader whiteToHue = new LinearGradient(0, 0, rect.width(), 0,
                Color.WHITE, Color.HSVToColor(new float[]{hue, 1f, 1f}), Shader.TileMode.CLAMP);
        Shader clearToBlack = new LinearGradient(0, 0, 0, rect.height(),
                Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        paint.setShader(new ComposeShader(whiteToHue, clearToBlack, PorterDuff.Mode.SRC_OVER));
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rect, radius, radius, paint);
 
        float cx = sat * rect.width();
        float cy = (1f - val) * rect.height();
        // keep the thumb fully visible at the edges
        cx = Math.max(thumbR, Math.min(rect.width() - thumbR, cx));
        cy = Math.max(thumbR, Math.min(rect.height() - thumbR, cy));
 
        fillPaint.setColor(getColor());
        canvas.drawCircle(cx, cy + density, thumbR + 2 * density, haloPaint);
        canvas.drawCircle(cx, cy, thumbR, ringPaint);
        canvas.drawCircle(cx, cy, thumbR - 3.5f * density, fillPaint);
    }
 
    public void setHue(float hue) {
        this.hue = hue;
        buildShader();
        invalidate();
        notifyColor();
    }
 
    public float getHue() { return hue; }
 
    public int getColor() {
        return Color.HSVToColor(new float[]{hue, sat, val});
    }
 
    private void notifyColor() {
        if (final1 != null) final1.onFinalColorChange(getColor());
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int a = e.getAction();
        if ((a == MotionEvent.ACTION_DOWN || a == MotionEvent.ACTION_MOVE) && rect.width() > 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            sat = Math.max(0f, Math.min(1f, e.getX() / rect.width()));
            val = 1f - Math.max(0f, Math.min(1f, e.getY() / rect.height()));
            invalidate();
            notifyColor();
        }
        return true;
    }
 
    public void setFinalInterface(FinalInterface inter) { this.final1 = inter; }
}
 
