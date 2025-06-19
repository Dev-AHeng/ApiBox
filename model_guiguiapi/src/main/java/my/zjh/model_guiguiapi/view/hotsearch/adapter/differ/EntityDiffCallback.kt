package my.zjh.model_guiguiapi.view.hotsearch.adapter.differ

import androidx.recyclerview.widget.DiffUtil
import my.zjh.model_guiguiapi.view.hotsearch.model.HotSearchItem

class EntityDiffCallback : DiffUtil.ItemCallback<HotSearchItem>() {
    override fun areItemsTheSame(oldItem: HotSearchItem, newItem: HotSearchItem): Boolean {
        // 根据index和title来确定是否是同一项
        return oldItem.index == newItem.index && oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: HotSearchItem, newItem: HotSearchItem): Boolean {
        // 比较两个项目的内容是否完全相同
        return oldItem.title == newItem.title && oldItem.url == newItem.url && oldItem.index == newItem.index
    }

    override fun getChangePayload(oldItem: HotSearchItem, newItem: HotSearchItem): Any? {
        // 用于部分更新，返回变化的内容
        if (oldItem.index == newItem.index && oldItem.title == newItem.title) {
            if (oldItem.url != newItem.url) {
                return newItem.url
            }
        }
        // 如果有其他变化，返回null让RecyclerView执行完整的刷新
        return null
    }
}