package de.bastianrinsche.panicsign

import android.view.View
import android.widget.ImageView
import de.bastianrinsche.panicsign.databinding.LayoutControlsBinding

internal class ColorControl(
        binding: LayoutControlsBinding,
        initSelection: String,
        val onColorSelected: (colorString: String) -> Unit
) {
    private val colors: List<ImageView> = listOf<ImageView>(
            binding.red, binding.orange, binding.yellow, binding.green, binding.green2,
            binding.teal, binding.lightBlue, binding.blue, binding.purple, binding.pink
    )
    var selected: String = initSelection
        set(newValue) {
            for (v in colors) {
                val tag = v.tag as String
                if (tag == newValue) {
                    field = newValue
                    v.isSelected = true
                    onColorSelected(tag)
                } else {
                    v.isSelected = false
                }
            }
        }

    init {
        selected = initSelection // L16 doesn't seem to use the setter logic otherwise
        for (v in colors) {
            v.setOnClickListener{ view: View ->
                selected = view.tag as String
            }
        }
    }
}
