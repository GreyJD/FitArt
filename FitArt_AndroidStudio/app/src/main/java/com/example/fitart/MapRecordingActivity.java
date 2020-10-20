package com.example.fitart;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.IccOpenLogicalChannelResponse;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MapRecordingActivity extends AppCompatActivity
{
    private static SeekBar seek_bar;
    ImageButton colorButton;
    ImageView colorSwatchImage;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);

        //Find Variables
        colorButton = findViewById(R.id.button_color);
        colorSwatchImage = findViewById(R.id.image_colorSwatch);
        seekbar();

        //Assign default color
        defaultColor = ContextCompat.getColor(MapRecordingActivity.this, R.color.colorPrimary);

        //On.click.listener for color palette
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    //Brush Size Controls
    public void seekbar()
    {
        seek_bar = (SeekBar)findViewById(R.id.seekBar_BrushSize);
        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int brush_Size;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brush_Size = progress / 10;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Debug purposes
                        Toast.makeText(MapRecordingActivity.this, "size:" + brush_Size, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    //Color choice popup
    public void openColorPicker()
    {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;

                //Attempt to update Color Swatch...Failed
                PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
                colorSwatchImage.setBackgroundColor(defaultColor);

                //Debug purposes
                Toast.makeText(MapRecordingActivity.this, "color:" + defaultColor, Toast.LENGTH_SHORT).show();

            }
        });
        ambilWarnaDialog.show();
    }
}
