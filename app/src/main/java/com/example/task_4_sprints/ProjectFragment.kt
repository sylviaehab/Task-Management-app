package com.example.task_4_sprints

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.task_4_sprints.data.db.AppDatabase
import com.example.task_4_sprints.data.db.viewModel.ProjectViewModel
import com.example.task_4_sprints.data.db.viewModel.ProjectViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProjectFragment : Fragment() {

    private val TAG_UI = "UI_TEST"
    private lateinit var vm: ProjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        val factory = ProjectViewModelFactory(db.projectDao(), db.taskDao())
        vm = ViewModelProvider(this, factory)[ProjectViewModel::class.java]

        vm.allProjectsLiveData.observe(viewLifecycleOwner) { projects ->
            Log.d(TAG_UI, "Observed LiveData: $projects")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.tasksFlow(1).collectLatest { tasks ->
                Log.d(TAG_UI, "Collected Flow: $tasks")
            }
        }
    }
}
