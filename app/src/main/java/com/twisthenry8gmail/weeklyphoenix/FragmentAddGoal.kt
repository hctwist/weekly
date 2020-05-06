package com.twisthenry8gmail.weeklyphoenix

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_add_goal.*
import kotlinx.android.synthetic.main.fragment_add_goal_colors.*
import kotlinx.android.synthetic.main.fragment_add_goal_reset.*
import kotlinx.android.synthetic.main.fragment_add_goal_done.*
import kotlinx.android.synthetic.main.fragment_add_goal_target.*
import kotlinx.android.synthetic.main.fragment_add_goal_title.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class FragmentAddGoal : Fragment(R.layout.fragment_add_goal) {

    private val viewModel by viewModels<Model>({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        add_goal_pager.adapter = PagerAdapter(this)
        add_goal_pager.isUserInputEnabled = false

        viewModel.pageData.observe(viewLifecycleOwner, Observer {

            add_goal_pager.setCurrentItem(it, true)
        })
    }

    class Model : ViewModel() {

        lateinit var goal: Goal
        val pageData = MutableLiveData<Int>()

        fun initialise(goalType: Goal.Type) {

            pageData.value = 0
            goal = Goal(
                goalType,
                "",
                -1,
                7,
                ChronoUnit.DAYS,
                0,
                LocalDate.now().toEpochDay(),
                -1,
                -1,
                Color.RED
            )
        }

        fun nextPage() {

            pageData.value = pageData.value!! + 1
        }

        fun previousPage() {

            pageData.value = pageData.value!! - 1
        }
    }

    class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 5

        override fun createFragment(position: Int): Fragment {

            return when (position) {

                0 -> FragmentAddGoalTitle()
                1 -> FragmentAddGoalTarget()
                2 -> FragmentAddGoalReset()
                else -> FragmentAddGoalDone()
            }
        }
    }

    class FragmentAddGoalTitle : Fragment(R.layout.fragment_add_goal_title) {

        private val viewModel by viewModels<Model>({ requireActivity() })

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

            add_goal_title.addTextChangedListener {

                add_goal_title_continue.isEnabled = !it.isNullOrEmpty()
            }

            add_goal_title.setText(viewModel.goal.name)

            add_goal_title.post {
                add_goal_title.requestFocus()
                requireContext().getSystemService<InputMethodManager>()
                    ?.showSoftInput(add_goal_title, 0)
            }

            add_goal_title_continue.setOnClickListener {

                viewModel.goal.name = add_goal_title.text.toString()
                viewModel.nextPage()
            }

            add_goal_title_back.setOnClickListener {

                viewModel.previousPage()
            }
        }
    }

    class FragmentAddGoalTarget : Fragment(R.layout.fragment_add_goal_target) {

        private val viewModel by viewModels<Model>({ requireActivity() })

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

            when (viewModel.goal.type) {

                Goal.Type.COUNTED -> {

                    add_goal_target.minValue = 1
                    viewModel.goal.target = 1
                }

                Goal.Type.TIMED -> {

                    val increment = 15L * 60
                    add_goal_target.minValue = increment
                    add_goal_target.increment = increment

                    add_goal_target.textFactory = {

                        DateTimeUtil.showGoalTime(requireContext(), it)
                    }

                    viewModel.goal.target = increment
                }
            }

            add_goal_target.valueChangedListener = {

                viewModel.goal.target = it
            }

            add_goal_target_continue.setOnClickListener {

                viewModel.nextPage()
            }

            add_goal_target_back.setOnClickListener {

                viewModel.previousPage()
            }
        }
    }

    class FragmentAddGoalReset : Fragment(R.layout.fragment_add_goal_reset) {

        private val viewModel by viewModels<Model>({ requireActivity() })

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

            Goal.ResetPreset.values().forEach { preset ->

                val chip = Chip(context)
                chip.setText(preset.displayNameRes)

                chip.setOnClickListener {

                    add_goal_frequency_display.animateText(chip.text)
                    viewModel.goal.setResetPreset(preset)
                }

                add_goal_frequency_chips.addView(chip)

                if (viewModel.goal.hasResetPreset(preset)) {

                    add_goal_frequency_display.setText(preset.displayNameRes)
                    chip.isChecked = true
                }
            }

            add_goal_frequency_continue.setOnClickListener {

                viewModel.goal.updateResetDate(LocalDate.ofEpochDay(viewModel.goal.startDate))
                viewModel.nextPage()
            }

            add_goal_frequency_back.setOnClickListener {

                viewModel.previousPage()
            }
        }
    }

    // TODO Advanced options
    class FragmentAddGoalColors : Fragment(R.layout.fragment_add_goal_colors) {

        private val viewModel by viewModels<Model>({ requireActivity() })

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


            add_goal_colors.apply {

                layoutManager = GridLayoutManager(context, 10)

                val ta = resources.obtainTypedArray(R.array.goal_colors)
                adapter = ColorDotAdapter(Array(ta.length()) {

                    ta.getColor(it, 0)
                })
                ta.recycle()

                addItemDecoration(ColorDotDecoration())
            }
        }

        class ColorDotDecoration : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {

                val margin = parent.resources.getDimensionPixelSize(R.dimen.margin)
                val position = parent.getChildAdapterPosition(view)

                val spanCount = (parent.layoutManager as GridLayoutManager).spanCount

                outRect.set(
                    if (position % spanCount != 0) margin else 0,
                    if (position < spanCount) 0 else margin,
                    0,
                    0
                )
            }
        }
    }

    class FragmentAddGoalDone : Fragment(R.layout.fragment_add_goal_done) {

        private val viewModel by viewModels<Model>({ requireActivity() })

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

            add_goal_done_confirm.setOnClickListener {

                fillInGoalBlanks()

                viewModel.viewModelScope.launch {
                    AppDatabase.getInstance(requireContext()).goalsDao().addGoal(viewModel.goal)
                }
                findNavController().navigateUp()
            }
        }

        private fun fillInGoalBlanks() {

            val cTypedArray = resources.obtainTypedArray(R.array.goal_colors)
            val color = cTypedArray.getColor(Random.nextInt(cTypedArray.length()), 0)
            cTypedArray.recycle()

            viewModel.goal.color = color
        }
    }
}