package com.bringitlist.bringit.Other;

public class IdAndChecked{
    public int id = -1;
    public boolean checked = false;

    public void reverse() {
        checked = !checked;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", checked=" + checked +
                '}';
    }
}