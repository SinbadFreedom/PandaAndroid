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

package com.dashidan.data.source.local;

import com.dashidan.GetTaskCallback;
import com.dashidan.LoadTasksCallback;
import com.dashidan.data.Task;
import com.dashidan.util.AppExecutors;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class TasksLocalDataSource {

    private static volatile TasksLocalDataSource INSTANCE;

    private TasksDao mTasksDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private TasksLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TasksDao tasksDao) {
        mAppExecutors = appExecutors;
        mTasksDao = tasksDao;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TasksDao tasksDao) {
        if (INSTANCE == null) {
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksLocalDataSource(appExecutors, tasksDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: com.dashidan.LoadTasksCallback#onDataNotAvailable() is fired if the database doesn't exist
     * or the table is empty.
     */
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = mTasksDao.getTasks();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    /**
     * Note: GetTaskCallback#onDataNotAvailable() is fired if the {@link Task} isn't
     * found.
     */
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTasksDao.getTaskById(taskId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null) {
                            callback.onTaskLoaded(task);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    public void saveTask(@NonNull final Task task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.insertTask(task);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    public void completeTask(@NonNull final Task task) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.updateCompleted(task.getId(), true);
            }
        };

        mAppExecutors.diskIO().execute(completeRunnable);
    }

    public void completeTask(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    public void activateTask(@NonNull final Task task) {
        Runnable activateRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.updateCompleted(task.getId(), false);
            }
        };
        mAppExecutors.diskIO().execute(activateRunnable);
    }

    public void activateTask(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    public void clearCompletedTasks() {
        Runnable clearTasksRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteCompletedTasks();

            }
        };

        mAppExecutors.diskIO().execute(clearTasksRunnable);
    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    public void deleteAllTasks() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteTasks();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    public void deleteTask(@NonNull final String taskId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.deleteTaskById(taskId);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
