package jac.infosyst.proyectogas.modelo;

import android.media.Image;

public class NavDrawerItem
{
    private boolean showNotify;
    private String title;
    private int imgMenu;

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int imgMenu) {
        this.showNotify = showNotify;
        this.title = title;
        this.imgMenu = imgMenu;

    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getimgMenu() {
        return imgMenu;
    }

    public void setimgMenu(int imgMenu) {
        this.imgMenu = imgMenu;
    }


}


