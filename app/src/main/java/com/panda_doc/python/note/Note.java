package com.panda_doc.python.note;

public class Note {
    private String userIconUrl;
    private String userName;
    private String noteText;

    public Note(String userIconUrl, String userName, String noteText) {
        this.userIconUrl = userIconUrl;
        this.userName = userName;
        this.noteText = noteText;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getNoteText() {
        return noteText;
    }
}
