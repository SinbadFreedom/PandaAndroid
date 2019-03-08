package com.dashidan.jsbridge;

import android.webkit.JavascriptInterface;

import com.dashidan.tasks.TaskAdapter;

public class JavaScriptinterface {
    //    Context context;
    TaskAdapter taskAdapter;

    public JavaScriptinterface(TaskAdapter taskAdapter) {
        this.taskAdapter = taskAdapter;
    }

    /**
     * 与js交互时用到的方法，在js里直接调用的
     */
    @JavascriptInterface
    public void navTitleId(String id) {
        taskAdapter.setAnchorTitleId(id);
    }
}
