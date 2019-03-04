package com.dashidan.tasks;

public class Title {
    private String titleNum;
    private String titleContent;

    public Title(String titleNum, String titleContent) {
        this.titleNum = titleNum;
        this.titleContent = titleContent;
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
}
