package com.panic.sign;

import android.view.View;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

class ColorControl {
    @BindViews({R.id.red, R.id.orange, R.id.yellow, R.id.green, R.id.green2,
            R.id.teal, R.id.light_blue, R.id.blue, R.id.purple, R.id.pink}) List<ImageView> colors;
    private String selection;
    private OnColorSelectedListener listener;

    ColorControl(View view, String initSelection) {
        ButterKnife.bind(this, view);

        selection = initSelection;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current = (String)view.getTag();
                if (!selection.equals(current)) {
                    selection = current;
                    setSelected(selection);
                }
            }
        };

        for (ImageView v : colors) {
            v.setOnClickListener(onClickListener);
        }
        setSelected(selection);
    }

    String getSelected() {
        for (View v : colors) {
            if (v.isSelected()) {
                return (String)v.getTag();
            }
        }
        throw new IllegalStateException("One color has to be selected at all times");
    }

    void setSelected(String selection) {
        if (selection == null) {
            return;
        }
        for (View v : colors) {
            String tag = (String)v.getTag();
            if (tag.equals(selection)) {
                v.setSelected(true);
                if (listener != null) {
                    listener.onColorSelected(tag);
                }
            } else {
                v.setSelected(false);
            }
        }
    }

    void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    interface OnColorSelectedListener {
        void onColorSelected(String colorString);
    }
}
