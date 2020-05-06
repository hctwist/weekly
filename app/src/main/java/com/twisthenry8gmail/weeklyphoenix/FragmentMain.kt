package com.twisthenry8gmail.weeklyphoenix

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.random.Random

class FragmentMain : Fragment(R.layout.fragment_main) {

    private val goalAdapter = GoalAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        main_toolbar.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {

                R.id.main_add_goal -> {

                    val popupMenu = PopupMenu(context, view.findViewById(menuItem.itemId))
                    Goal.Type.values().forEach {

                        popupMenu.menu.add(it.displayNameRes)
                    }
                    popupMenu.setOnMenuItemClickListener { popupMenuItem ->

                        ViewModelProvider(requireActivity())[FragmentAddGoal.Model::class.java].initialise(
                            Goal.Type.values()
                                .find { it.getDisplayName(requireContext()) == popupMenuItem.title.toString() }!!
                        )
                        findNavController().navigate(R.id.fragmentAddGoal)
                        true
                    }
                    popupMenu.show()

                    true
                }

                else -> false
            }
        }

        main_cards.apply {

            addItemDecoration(GoalItemDecoration())
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }

        val date = LocalDate.now()
        goalAdapter.heading = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val quotes = resources.getStringArray(R.array.quotes)
        goalAdapter.subheading = quotes[Random(date.toEpochDay()).nextInt(quotes.size)]

        AppDatabase.getInstance(requireContext()).goalsDao().getAll()
            .observe(viewLifecycleOwner, Observer {

                goalAdapter.goals = it
            })
        goalAdapter.actionListener = { goal, position ->

            when (goal.type) {

                Goal.Type.COUNTED -> {

                    goal.current++
                    lifecycleScope.launch {
                        AppDatabase.getInstance(requireContext()).goalsDao()
                            .updateGoalProgress(goal.name, goal.current)
                    }
                    goalAdapter.notifyItemChanged(position, GoalAdapter.Change.PROGRESS)
                }

                Goal.Type.TIMED -> {


                }
            }
        }
    }

    class GoalItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            val adapterPosition = parent.getChildAdapterPosition(view)

            if(adapterPosition == 0) {

                outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.double_margin)
            }
            else if(adapterPosition  != state.itemCount - 1) {

                outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.margin)
            }
        }
    }
}