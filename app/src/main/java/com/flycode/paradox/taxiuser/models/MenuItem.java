package com.flycode.paradox.taxiuser.models;

/**
 * Created by victor on 12/21/15.
 */
public class MenuItem {

    private int icon;
    private int title;

    public MenuItem(int icon, int title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public int getTitle() {
        return title;
    }

}
