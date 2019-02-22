package com.dashidan;

import com.dashidan.data.Task;

public interface GetTaskCallback {

    void onTaskLoaded(Task task);

    void onDataNotAvailable();
}
