package com.but.todo.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE status = :status")
    fun getTasksByStatus(status: String): LiveData<List<Task>>

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}

