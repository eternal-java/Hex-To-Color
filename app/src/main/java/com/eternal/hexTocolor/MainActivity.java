
package com.eternal.hexTocolor;
 
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity implements ColorPickerView.HueListner, ColorState.FinalInterface {
 
    private ColorState colorState;
    private ColorPickerView huePicker;
    private View preview;
    private TextView hexText, rgbText, copyBtn;
    private GradientDrawable previewBg;
    private String currentHex = "#000000";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(0xFF14161B);
 
        huePicker = findViewById(R.id.colorSqr);
        colorState = findViewById(R.id.state);
        preview = findViewById(R.id.prevColor);
        hexText = findViewById(R.id.hexText);
        rgbText = findViewById(R.id.rgbText);
        copyBtn = findViewById(R.id.copyBtn);
 
        previewBg = new GradientDrawable();
        previewBg.setCornerRadius(28 * getResources().getDisplayMetrics().density);
        preview.setBackground(previewBg);
 
        huePicker.setHueListner(this);
        colorState.setFinalInterface(this);
        huePicker.setHue(colorState.getHue());
 
        copyBtn.setOnClickListener(v -> copyHex());
        preview.setOnClickListener(v -> copyHex());
 
        onFinalColorChange(colorState.getColor()); // initial paint
    }
 
    @Override
    public void onHueChanged(float hue) {
        colorState.setHue(hue); // triggers onFinalColorChange
    }
 
    @Override
    public void onFinalColorChange(int color) {
        currentHex = String.format("#%06X", color & 0xFFFFFF);
        previewBg.setColor(color);
 
        boolean light = (0.299 * Color.red(color) + 0.587 * Color.green(color)
                + 0.114 * Color.blue(color)) / 255.0 > 0.6;
        int fg = light ? 0xFF14161B : Color.WHITE;
 
        hexText.setText(currentHex);
        hexText.setTextColor(fg);
        rgbText.setText("RGB  " + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color));
        rgbText.setTextColor(fg);
        rgbText.setAlpha(0.72f);
    }
 
    private void copyHex() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("hex", currentHex));
        Toast.makeText(this, "Copied " + currentHex, Toast.LENGTH_SHORT).show();
    }
}
 
