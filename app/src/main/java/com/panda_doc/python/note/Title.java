package com.panda_doc.python.note;

public class Title {
    private String titleNum;
    private String titleContent;
    private String fullTitle;

    public Title(String titleNum, String titleContent, String fullTitle) {
        this.titleNum = titleNum;
        this.titleContent = titleContent;
        this.fullTitle = fullTitle;
    }

    public String getTitleNum() {
        return titleNum;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public int getTitleRank() {
        return titleNum.split("\\.").length;
    }

    public String getFullTitle() {
        return fullTitle;
    }
}
