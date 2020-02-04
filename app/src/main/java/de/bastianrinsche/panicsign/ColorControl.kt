package de.bastianrinsche.panicsign

import android.view.View
import android.widget.ImageView
import de.bastianrinsche.panicsign.databinding.LayoutControlsBinding

internal class ColorControl(binding: LayoutControlsBinding, initSelection: String) {
    private val colors: List<ImageView> = listOf<ImageView>(
            binding.red, binding.orange, binding.yellow, binding.green, binding.green2,
            binding.teal, binding.lightBlue, binding.blue, binding.purple, binding.pink
    )
    private var listener: OnColorSelectedListener? = null
    var selected: String = "MandatoryInitialization"
        set(newValue) {
            for (v in colors) {
                val tag = v.tag as String
                if (tag == newValue) {
                    field = newValue
                    v.isSelected = true
                    listener?.onColorSelected(tag)
                } else {
                    v.isSelected = false
                }
            }
        }

    fun setOnColorSelectedListener(listener: OnColorSelectedListener?) {
        this.listener = listener
    }

    internal interface OnColorSelectedListener {
        fun onColorSelected(colorString: String)
    }

    init {
        selected = initSelection
        val onClickListener = View.OnClickListener { v: View ->
            selected = v.tag as String
        }
        for (v in colors) {
            v.setOnClickListener(onClickListener)
        }
    }
}
