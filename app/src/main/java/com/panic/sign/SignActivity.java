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
    @BindView(R.id.button_change) Button change;
    @BindView(R.id.control_top) View topColors;
    @BindView(R.id.control_bottom) View bottomColors;
    // this is necessary to disambiguate the layout IDs
    // may be fixed in the future https://github.com/JakeWharton/butterknife/issues/394
    static class ColorControl {
        @BindViews({R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.green2,
                R.id.teal, R.id.light_blue, R.id.blue, R.id.purple, R.id.pink})
        List<ImageView> colors;
    }

    ColorControl topControl = new ColorControl();
    ColorControl bottomControl = new ColorControl();

    boolean top = true;
    boolean bottom = false;
    String topSelection = "NONE";
    String bottomSelection = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        ButterKnife.bind(this);
        ButterKnife.bind(topControl, topColors);
        ButterKnife.bind(bottomControl, bottomColors);

        View.OnClickListener topListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = view.getTag().toString();
                if (topSelection.equals(current)) {
                    view.setSelected(false);
                    topSelection = "NONE";
                } else {
                    deselectViews(top);
                    view.setSelected(true);
                    topSelection = current;
                }
                Log.d("topSelection", topSelection);
            }
        };

        View.OnClickListener bottomListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = view.getTag().toString();
                if (bottomSelection.equals(current)) {
                    view.setSelected(false);
                    bottomSelection = "NONE";
                } else {
                    deselectViews(bottom);
                    view.setSelected(true);
                    bottomSelection = current;
                }
                Log.d("bottomSelection", bottomSelection);
            }
        };

        for (ImageView i : topControl.colors) {
            i.setOnClickListener(topListener);
        }

        for (ImageView i : bottomControl.colors) {
            i.setOnClickListener(bottomListener);
        }

    }

    private void deselectViews(boolean top) {
        if (top) {
            for (View v : topControl.colors) {
                v.setSelected(false);
            }
        } else {
            for (View v : bottomControl.colors) {
                v.setSelected(false);
            }
        }
    }

}
