package com.twisthenry8gmail.weeklyphoenix.view.add

import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.viewmodel.AddGoalViewModel
import kotlinx.android.synthetic.main.color_dot.view.*
import kotlinx.android.synthetic.main.fragment_color_dot.*

class FragmentAddGoalColor : BottomSheetDialogFragment() {

    private val viewModel by navGraphViewModels<AddGoalViewModel>(R.id.nav_add_goal)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(context).inflate(R.layout.fragment_color_dot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val cTypedArray = resources.obtainTypedArray(R.array.goal_colors)
        val colors = Array(cTypedArray.length()) {

            cTypedArray.getColor(it, 0)
        }
        cTypedArray.recycle()
        val colorNames = resources.getStringArray(R.array.goal_color_names)

        val dotAdapter = Adapter(colors)

        viewModel.color.observe(viewLifecycleOwner, Observer { color ->

            val colorIndex = colors.indexOfFirst { it == color }
            dotAdapter.selected = colorIndex
            color_dot_color_name.text = colorNames[colorIndex]
        })

        dotAdapter.onClickListener = {

            viewModel.color.value = it
        }

        color_dot_colors.apply {

            setHasFixedSize(true)
            addItemDecoration(Decoration())
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dotAdapter
        }
    }

    class Adapter(private val colors: Array<Int>) :
        RecyclerView.Adapter<Adapter.VH>() {

        var selected = 0
            set(value) {

                notifyItemChanged(field)
                notifyItemChanged(value)

                field = value
            }

        var onClickListener: ((Int) -> Unit)? = null

        override fun getItemCount(): Int {

            return colors.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

            return VH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.color_dot,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {

            val color = colors[position]
            holder.dot.backgroundTintList = ColorStateList.valueOf(color)

            holder.highlight.visibility = if (position == selected) View.VISIBLE else View.GONE

            holder.dot.setOnClickListener {

                onClickListener?.invoke(color)
            }
        }

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val dot: ImageButton = itemView.color_dot_dot
            val highlight: View = itemView.color_dot_highlight
        }
    }

    class Decoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            val margin = parent.resources.getDimensionPixelSize(R.dimen.margin)
            val position = parent.getChildAdapterPosition(view)

            if (position > 0) {

                outRect.left = margin
            }
        }
    }
}