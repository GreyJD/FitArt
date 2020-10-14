package com.example.fitart;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MapRecordingActivity extends AppCompatActivity
{
    private static SeekBar seek_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_recording);
        seekbar();
    }

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
                        Toast.makeText(MapRecordingActivity.this, "size:" + brush_Size, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
