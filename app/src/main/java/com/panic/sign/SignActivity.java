package com.panic.sign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class SignActivity extends AppCompatActivity {
    static class ColorControl {
        @BindViews({R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.green2,
                R.id.teal, R.id.light_blue, R.id.blue, R.id.purple, R.id.pink})
        List<ImageView> colors;
    }
    @BindView(R.id.control_top) View topColors;
    @BindView(R.id.control_bottom) View bottomColors;
    @BindView(R.id.button_change) Button change;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);

        ColorControl top = new ColorControl();
        ColorControl bottom = new ColorControl();

        ButterKnife.bind(top, topColors);
        ButterKnife.bind(bottom, bottomColors);

        View.OnClickListener test = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(true);
                Log.d("test", "test");
                Log.d("test", view.isSelected() + "");
            }
        };

        for (ImageView i : top.colors) {
            i.setOnClickListener(test);
        }

        for (ImageView i : bottom.colors) {
            i.setOnClickListener(test);
        }

    }


}
