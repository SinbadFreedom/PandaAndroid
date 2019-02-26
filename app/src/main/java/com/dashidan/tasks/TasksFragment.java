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

package com.dashidan.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dashidan.ConstValue;
import com.dashidan.R;

import com.dashidan.data.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Task}s. User can choose to view all, active or completed tasks.
 */
public class TasksFragment extends Fragment {

//    private TasksPresenter mPresenter;

//    private TasksAdapter mListAdapter;

//    private View mNoTasksView;

//    private TextView mTextView;

    private WebView mWebView;

//    private ImageView mNoTaskIcon;
//
//    private TextView mNoTaskMainView;
//
//    private TextView mNoTaskAddView;

//    private LinearLayout mTasksView;

//    private TextView mFilteringLabelView;

    public TasksFragment() {
        // Requires empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }



//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        String str_0 = "    <ol start=\"2\">\n" +
//                "        <li><h1 id=\"using-the-python-interpreter\">Using the Python Interpreter</h1>\n" +
//                "        </li>\n" +
//                "    </ol>\n" +
//                "    <p>###2.1. Invoking the <strong>Interpreter</strong></p>\n" +
//                "    <p>The Python interpreter is usually installed as <code>/usr/local/bin/python3.7</code> on those machines where it is available; putting <code>/usr/local/bin</code> in your Unix shell’s search path makes it possible to start it by typing the command:</p>\n" +
//                "    <pre><code>python3.7</code></pre><p>to the shell. <a href=\"\">[1]</a> Since the choice of the directory where the interpreter lives is an installation option, other places are possible; check with your local Python guru or system administrator. (E.g., <code>/usr/local/python</code> is a popular alternative location.)</p>\n" +
//                "    <p>On Windows machines, the <font color=\"#EE6363\">Python installation </font>is usually placed in <code>C:\\Python37</code>, though you can change this when you’re running the installer. To add this directory to your path, you can type the following command into the command prompt in a DOS box:</p>\n" +
//                "    <pre><code>set path=%path%;C:\\python37</code></pre><p>Typing an end-of-file character (<code>Control-D</code> on Unix, <code>Control-Z </code>on Windows) at the primary prompt causes the interpreter to exit with a zero exit status. If that doesn’t work, you can exit the interpreter by typing the following command: <code>quit()</code>.</p>\n" +
//                "    <p>The interpreter’s line-editing features include interactive editing, history substitution and code completion on systems that support readline. Perhaps the quickest check to see whether command line editing is supported is typing <code>Control-P</code> to the first Python prompt you get. If it beeps, you have command line editing; see <a href=\"\">Appendix Interactive Input Editing and History Substitution</a> for an introduction to the keys. If nothing appears to happen, or if <code>^P</code> is echoed, command line editing isn’t available; you’ll only be able to use backspace to remove characters from the current line.</p>\n" +
//                "    <p>The interpreter operates somewhat like the Unix shell: when called with standard input connected to a tty device, it reads and executes commands interactively; when called with a file name argument or with a file as standard input, it reads and executes a script from that file.</p>\n" +
//                "    <p>A second way of starting the interpreter is <code>python -c command [arg] ...</code>, which executes the statement(s) in command, analogous to the shell’s -c option. Since Python statements often contain spaces or other characters that are special to the shell, it is usually advised to quote command in its entirety with single quotes.</p>\n" +
//                "    <p>Some Python modules are also useful as scripts. These can be invoked using <code>python -m module [arg] ...</code>, which executes the source file for module as if you had spelled out its full name on the command line.</p>\n" +
//                "    <p>When a script file is used, it is sometimes useful to be able to run the script and enter interactive mode afterwards. This can be done by passing <a href=\"\">-i</a> before the script.</p>\n" +
//                "    <p>All command line options are described in <a href=\"\">Command line and environment</a>.</p>\n" +
//                "    <p>####2.1.1. Argument Passing####\n" +
//                "        When known to the interpreter, the script name and additional arguments thereafter are turned into a list of strings and assigned to the <code>argv</code> variable in the <code>sys</code> module. You can access this list by executing <code>import sys</code>. The length of the list is at least one; when no script and no arguments are given, <code>sys.argv[0]</code> is an empty string. When the script name is given as &#39;-&#39; (meaning standard input), <code>sys.argv[0]</code> is set to <code>'-'</code>. When <a href=\"\">-c</a> command is used, <code>sys.argv[0]</code> is set to <code>'-c'</code>. When <a href=\"\">-m</a> module is used, <code>sys.argv[0]</code> is set to the full name of the located module. Options found after <a href=\"\">-c</a> command or <a href=\"\">-m</a> module are not consumed by the Python interpreter’s option processing but left in <code>sys.argv</code>for the command or module to handle.</p>\n" +
//                "    <p>####2.1.2. Interactive Mode####\n" +
//                "        When commands are read from a tty, the interpreter is said to be in interactive mode. In this mode it prompts for the next command with the primary prompt, usually three greater-than signs (<code>>>></code>); for continuation lines it prompts with the secondary prompt, by default three dots (<code>...</code>). The interpreter prints a welcome message stating its version number and a copyright notice before printing the first prompt:</p>\n" +
//                "    <pre><code>$ python3.7\n" +
//                "Python 3.7 (default, Sep 16 2015, 09:25:04)\n" +
//                "[GCC 4.8.2] on linux\n" +
//                "Type &quot;help&quot;, &quot;copyright&quot;, &quot;credits&quot; or &quot;license&quot; for more information.\n" +
//                "&gt;&gt;&gt;</code></pre><p>Continuation lines are needed when entering a multi-line construct. As an example, take a look at this if statement:</p>\n" +
//                "    <pre><code>&gt;&gt;&gt;\n" +
//                "&gt;&gt;&gt; the_world_is_flat = True\n" +
//                "&gt;&gt;&gt; if the_world_is_flat:\n" +
//                "...     print(&quot;Be careful not to fall off!&quot;)\n" +
//                "...</code></pre><p>Be careful not to fall off!\n" +
//                "    For more on interactive mode, see <a href=\"\">Interactive Mode</a>.</p>\n" +
//                "    <p>###2.2. The Interpreter and Its Environment###</p>\n" +
//                "    <p>####2.2.1. Source Code Encoding####\n" +
//                "        By default, Python source files are treated as encoded in UTF-8. In that encoding, characters of most languages in the world can be used simultaneously in string literals, identifiers and comments — although the standard library only uses ASCII characters for identifiers, a convention that any portable code should follow. To display all these characters properly, your editor must recognize that the file is UTF-8, and it must use a font that supports all the characters in the file.</p>\n" +
//                "    <p>To declare an encoding other than the default one, a special comment line should be added as the first line of the file. The syntax is as follows:</p>\n" +
//                "    <pre><code># -*- coding: encoding -*-</code></pre><p>where encoding is one of the valid <code>codecs</code> supported by Python.</p>\n" +
//                "    <p>For example, to declare that Windows-1252 encoding is to be used, the first line of your source code file should be:</p>\n" +
//                "    <pre><code># -*- coding: cp1252 -*-</code></pre><p>One exception to the first line rule is when the source code starts with a UNIX “shebang” line. In this case, the encoding declaration should be added as the second line of the file. For example:</p>\n" +
//                "    <pre><code>#!/usr/bin/env python3\n" +
//                "# -*- coding: cp1252 -*-</code></pre><p><strong><em>Footnotes</em></strong></p>\n" +
//                "    <p><a href=\"\">[1]</a>    On Unix, the Python 3.x interpreter is by default not installed with the executable named python, so that it does not conflict with a simultaneously installed Python 2.x executable.</p>";
//
////        ArrayList<String> str = new ArrayList<>();
////        str.add("sssssssss");
////        str.add("kkkkk");
////        str.add("dfsdfsdf");
////        str.add(str_0);
////        mListAdapter = new TasksAdapter(str);
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
////        mPresenter.start();
//    }
//
//    public void setPresenter(@NonNull TasksPresenter presenter) {
////        mPresenter = checkNotNull(presenter);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        mPresenter.result(requestCode, resultCode);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag, container, false);

        // Set up tasks view
//        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
//        listView.setAdapter(mListAdapter);
//        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
//        mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);

        // Set up  no tasks view
//        mNoTasksView = root.findViewById(R.id.noTasks);
//        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
//        listView.setAdapter(mListAdapter);
        mWebView = root.findViewById(R.id.task_web_view);
        mWebView.loadUrl("https://dashidan.com/and_doc/python3/2.html");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        //后退
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


//        mTextView = root.findViewById(R.id.task_textView);


//        mTextView.setText(Html.fromHtml(str));
//        mWebView = root.findViewById(R.id.doc_view);
//        mWebView.loadUrl("https://dashidan.com/and_doc/python3/1.html");

//        mNoTaskIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
//        mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
//        mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
//        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddTask();
//            }
//        });

        // Set up floating action button
//        FloatingActionButton fab =
//                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
//
//        fab.setImageResource(R.drawable.ic_add);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.addNewTask();
//            }
//        });

//        // Set up progress indicator
//        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
//                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
//        swipeRefreshLayout.setColorSchemeColors(
//                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
//                ContextCompat.getColor(getActivity(), R.color.colorAccent),
//                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
//        );
//        // Set the scrolling view in the custom SwipeRefreshLayout.
//        swipeRefreshLayout.setScrollUpChild(listView);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPresenter.loadTasks(false);
//            }
//        });

//        setHasOptionsMenu(true);

        return root;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_clear:
//                mPresenter.clearCompletedTasks();
//                break;
//            case R.id.menu_filter:
//                showFilteringPopUpMenu();
//                break;
//            case R.id.menu_refresh:
//                mPresenter.loadTasks(true);
//                break;
//        }
//        return true;
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

//    public void showFilteringPopUpMenu() {
//        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
//        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
////                switch (item.getItemId()) {
////                    case R.id.active:
////                        mPresenter.setFiltering(ConstValue.ACTIVE_TASKS);
////                        break;
////                    case R.id.completed:
////                        mPresenter.setFiltering(ConstValue.COMPLETED_TASKS);
////                        break;
////                    default:
////                        mPresenter.setFiltering(ConstValue.ALL_TASKS);
////                        break;
////                }
//                mPresenter.loadTasks(false);
//                return true;
//            }
//        });
//
//        popup.show();
//    }

//    /**
//     * Listener for clicks on tasks in the ListView.
//     */
//    TaskItemListener mItemListener = new TaskItemListener() {
//        @Override
//        public void onTaskClick(String clickedTask) {
////            mPresenter.openTaskDetails(clickedTask);
//        }
//
//        @Override
//        public void onCompleteTaskClick(String completedTask) {
//            mPresenter.completeTask(completedTask);
//        }
//
//        @Override
//        public void onActivateTaskClick(String activatedTask) {
//            mPresenter.activateTask(activatedTask);
//        }
//    };

//    public void setLoadingIndicator(final boolean active) {
//
//        if (getView() == null) {
//            return;
//        }
//        final SwipeRefreshLayout srl =
//                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
//
//        // Make sure setRefreshing() is called after the layout is done with everything else.
//        srl.post(new Runnable() {
//            @Override
//            public void run() {
//                srl.setRefreshing(active);
//            }
//        });
//    }

//    public void showTasks(List<String> tasks) {
////        mListAdapter.replaceData(tasks);
//
////        mTasksView.setVisibility(View.VISIBLE);
//        mNoTasksView.setVisibility(View.GONE);
//    }
//
//    public void showNoActiveTasks() {
//        showNoTasksViews(
//                getResources().getString(R.string.no_tasks_active),
//                R.drawable.ic_check_circle_24dp,
//                false
//        );
//    }
//
//    public void showNoTasks() {
//        showNoTasksViews(
//                getResources().getString(R.string.no_tasks_all),
//                R.drawable.ic_assignment_turned_in_24dp,
//                false
//        );
//    }
//
//    public void showNoCompletedTasks() {
//        showNoTasksViews(
//                getResources().getString(R.string.no_tasks_completed),
//                R.drawable.ic_verified_user_24dp,
//                false
//        );
//    }

    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
//        mTasksView.setVisibility(View.GONE);
//        mNoTasksView.setVisibility(View.VISIBLE);

//        mNoTaskMainView.setText(mainText);
//        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
//        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

//    public void showActiveFilterLabel() {
//        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
//    }
//
//    public void showCompletedFilterLabel() {
//        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
//    }
//
//    public void showAllFilterLabel() {
//        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
//    }

//    public void showAddTask() {
//        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
//        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
//    }

//    public void showTaskDetailsUi(String taskId) {
//        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
//        // to show some Intent stubbing.
//        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
//        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
//        startActivity(intent);
//    }

    public void showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete));
    }

    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    public void showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared));
    }

    public void showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    public boolean isActive() {
        return isAdded();
    }

}
