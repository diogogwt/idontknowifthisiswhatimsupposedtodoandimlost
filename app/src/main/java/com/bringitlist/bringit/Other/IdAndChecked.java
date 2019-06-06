package com.bringitlist.bringit.Other;

import java.io.Serializable;

public class IdAndChecked implements Serializable {
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