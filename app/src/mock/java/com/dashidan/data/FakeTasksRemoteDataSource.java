/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dashidan.data;

import com.dashidan.GetTaskCallback;
import com.dashidan.LoadTasksCallback;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeTasksRemoteDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, String> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {
    }

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    public void getTasks(@NonNull LoadTasksCallback callback) {
        callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        String task = TASKS_SERVICE_DATA.get(taskId);
        callback.onTaskLoaded(task);
    }

    public void saveTask(@NonNull String task) {
        TASKS_SERVICE_DATA.put(task, task);
    }

//    public void completeTask(@NonNull String task) {
////        String completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
//        TASKS_SERVICE_DATA.put(task, task);
//    }

    public void completeTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

//    public void activateTask(@NonNull String task) {
////        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
//        TASKS_SERVICE_DATA.put(task, task);
//    }

    public void activateTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, String>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry != null) {
                it.remove();
            }
        }
    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(String... tasks) {
        for (String task : tasks) {
            TASKS_SERVICE_DATA.put(task, task);
        }
    }
}
