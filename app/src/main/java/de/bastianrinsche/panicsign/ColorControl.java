package de.bastianrinsche.panicsign;

import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import de.bastianrinsche.panicsign.databinding.LayoutControlsBinding;

class ColorControl {
    private List<ImageView> colors;
    private String selection;
    private OnColorSelectedListener listener;

    ColorControl(LayoutControlsBinding binding, String initSelection) {
        colors = Arrays.asList(
                binding.red, binding.orange, binding.yellow, binding.green, binding.green2,
                binding.teal, binding.lightBlue, binding.blue, binding.purple, binding.pink
        );
        setSelected(initSelection);

        View.OnClickListener onClickListener = v -> {
            String current = (String)v.getTag();
            if (!selection.equals(current)) {
                selection = current;
                setSelected(selection);
            }
        };

        for (ImageView v : colors) {
            v.setOnClickListener(onClickListener);
        }
    }

    String getSelected() {
        if (selection == null) {
            throw new IllegalStateException("One color has to be selected at all times");
        }
        return selection;
    }

    void setSelected(String selection) {
        if (selection == null) {
            throw new IllegalStateException("One color has to be selected at all times");
        }
        for (View v : colors) {
            String tag = (String)v.getTag();
            if (tag.equals(selection)) {
                v.setSelected(true);
                this.selection = selection;
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
