package com.kevinbacon.wikipage;
public class WikiPage {
    private String title;
    private WikiPage parent;
    private boolean seen;

    public WikiPage(String title, WikiPage parent) {
        this.title = title;

        if(parent != null && title.equals(parent.getTitle())) {
            this.parent = null;
        }
        else {
            this.parent = parent;
        }
    }
    public WikiPage getParent() {
        return parent;
    }

    public void setParent(WikiPage parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsSeen() { return this.seen;}

    public void setSeen(boolean seen) {
        this.seen = seen;
    }



}
