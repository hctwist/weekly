package com.twisthenry8gmail.weeklyphoenix.view.add

import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.viewmodel.CurrentGoalViewModel
import com.twisthenry8gmail.weeklyphoenix.R
import kotlinx.android.synthetic.main.fragment_color_dot.*

// TODO Remove to AddGoal.kt?
class FragmentAddGoalColor : BottomSheetDialogFragment() {

    private val viewModel by viewModels<CurrentGoalViewModel>({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(context).inflate(R.layout.fragment_color_dot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val cTypedArray = requireContext().resources.obtainTypedArray(R.array.goal_colors)
        val colors = Array(cTypedArray.length()) {

            cTypedArray.getColor(it, 0)
        }
        cTypedArray.recycle()

        val dotAdapter =
            Adapter(
                colors
            )
        dotAdapter.onClickListener = {

            viewModel.requireCurrentGoal().color = it
            viewModel.postCurrentGoalUpdate()
            dismiss()
        }

        color_dot_colors.apply {

            setHasFixedSize(true)
            addItemDecoration(Decoration())
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = dotAdapter
        }
    }

    // TODO Current selected
    class Adapter(private val colors: Array<Int>) : RecyclerView.Adapter<Adapter.VH>() {

        var onClickListener: (Int) -> Unit = {}

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

            holder.dot.setOnClickListener {

                onClickListener(color)
            }
        }

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val dot = itemView as ImageButton
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