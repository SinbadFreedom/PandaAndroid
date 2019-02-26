package com.dashidan;

import com.dashidan.data.Task;

public interface GetTaskCallback {

    void onTaskLoaded(String task);

    void onDataNotAvailable();
}
