package com.bringitlist.bringit.Other;

import java.io.Serializable;

public class IdAndChecked implements Serializable {
    public int id;
    public boolean checked;

    public void reverse(){
        checked = !checked;
    }
}