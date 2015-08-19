package com.example.kskhom.weather;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by kskhom on 13.08.2015.
 */
public class WeatherView extends LinearLayout {
    private TextView date;

    private ImageView mImage;
    private TextView temp;
    private TextView hum;

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_component, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
        mImage = (ImageView) this
                .findViewById(R.id.weather_pic);
        mImage.setImageResource(R.drawable.im1);

        temp = (TextView) this
                .findViewById(R.id.tvTemp);
        date = (TextView) this
                .findViewById(R.id.tvDate);
        hum = (TextView) this
                .findViewById(R.id.tvRelwet);

    }

    public WeatherView(Context context) {
        this(context, null);
    }

    public void setParams(Entry e)
    {
        temp.setText(e.temperature);
        hum.setText(e.relwet);
        switch(e.cloudiness) {
            case "1":
                mImage.setImageResource(R.drawable.im1);
                break;
            case "2":
                mImage.setImageResource(R.drawable.im2);
                break;
            case "3":
                mImage.setImageResource(R.drawable.im3);
                break;
            case "4":
                mImage.setImageResource(R.drawable.im4);
                break;
        }
        date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(e.day).append("-").append(e.month + 1).append("-")
                .append(e.year).append(" "));
    }
}
