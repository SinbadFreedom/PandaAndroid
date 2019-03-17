package com.panda_doc.python.http;

public interface Progress {
    int ERROR = -1;
    int CONNECT_SUCCESS = 0;
    int GET_INPUT_STREAM_SUCCESS = 1;
    int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
    int PROCESS_INPUT_STREAM_SUCCESS = 3;
}
