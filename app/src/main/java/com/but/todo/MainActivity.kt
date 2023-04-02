package com.but.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.but.todo.data.Task
import com.but.todo.data.TaskDao
import com.but.todo.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTaskTitle: EditText
    private lateinit var buttonAddTask: ImageButton
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDao: TaskDao
    private lateinit var spinnerTaskStatus: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        editTextTaskTitle = findViewById(R.id.editTextTaskTitle)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)

        buttonAddTask.setOnClickListener {
            val title = editTextTaskTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                insertTask(title)
                editTextTaskTitle.setText("")
            } else {
                Toast.makeText(this, "Merci d'entrer un nom de tache", Toast.LENGTH_SHORT).show()
            }
        }

        val database = TaskDatabase.getDatabase(this)
        taskDao = database.taskDao()

        taskAdapter = TaskAdapter()
        recyclerViewTasks.adapter = taskAdapter

        updateTaskList()

        spinnerTaskStatus = findViewById(R.id.spinnerTaskStatus)

        // Configure the Spinner with the task status options
        val taskStatusOptions = arrayOf("à faire", "en retard", "réalisée")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, taskStatusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTaskStatus.adapter = spinnerAdapter
    }

    private fun insertTask(title: String) {
        val task = Task(title = title, dueDate = null, status = "à faire")
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insertTask(task)
            withContext(Dispatchers.Main) {
                updateTaskList()
            }
        }
    }

    private fun updateTaskList() {
        taskDao.getTasksByStatus("à faire").observe(this, { tasks ->
            taskAdapter.setTasks(tasks)
        })
    }

    private fun getTasksByStatus(status: String) {
        taskDao.getTasksByStatus(status).observe(this, { tasks ->
            taskAdapter.setTasks(tasks)
        })
    }

}