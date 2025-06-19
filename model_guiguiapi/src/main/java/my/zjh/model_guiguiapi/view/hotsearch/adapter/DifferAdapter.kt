package my.zjh.model_guiguiapi.view.hotsearch.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter4.BaseDifferAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import my.zjh.model_guiguiapi.R
import my.zjh.model_guiguiapi.util.CloneUtils
import my.zjh.model_guiguiapi.view.hotsearch.adapter.differ.EntityDiffCallback
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchItem

/**
 * DifferAdapter
 *
 * @author AHeng
 * @date 2025/04/13 21:54
 */
class DifferAdapter : BaseDifferAdapter<HotSearchItem, QuickViewHolder>(EntityDiffCallback()) {

    companion object {
        private const val COLOR_FIRST = "#ea444d"
        private const val COLOR_SECOND = "#ed702d"
        private const val COLOR_THIRD = "#eead3f"
        private const val COLOR_DEFAULT_BG = "#efeff5"
        private const val COLOR_DEFAULT_TEXT = "#000000"
        private const val COLOR_WHITE = "#ffffff"
        private const val CORNER_RADIUS = 23f
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): QuickViewHolder {
        return QuickViewHolder(R.layout.gg_item_hotsearch, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: HotSearchItem?) {
        val indexView = holder.getView<AppCompatTextView>(R.id.item_index)

        val gd = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = CORNER_RADIUS

            when (position) {
                0 -> {
                    setColor(Color.parseColor(COLOR_FIRST))
                    indexView.setTextColor(Color.parseColor(COLOR_WHITE))
                }

                1 -> {
                    setColor(Color.parseColor(COLOR_SECOND))
                    indexView.setTextColor(Color.parseColor(COLOR_WHITE))
                }

                2 -> {
                    setColor(Color.parseColor(COLOR_THIRD))
                    indexView.setTextColor(Color.parseColor(COLOR_WHITE))
                }

                else -> {
                    setColor(Color.parseColor(COLOR_DEFAULT_BG))
                    indexView.setTextColor(Color.parseColor(COLOR_DEFAULT_TEXT))
                }
            }
        }

        indexView.background = gd
        indexView.text = item?.index.toString()

        holder.setText(R.id.item_title, item?.title)
    }
}