package com.bringitlist.bringit.Database;

// esta class serve para ter as cenas ja definidas e nao me enganar ao longo das varias vezes que vou escrever esses valores
public class DatabaseNames {

    private DatabaseNames() {
    }

    public static final String COLUMN_ID = "id";

    public static final String PRODUCTS = "products";
    public static final String CATEGORIES = "categories";

    public static final String CREATE_TABLE_CATEGORIES = "create table " + CATEGORIES + " (id integer primary key,name text);";
    public static final String CREATE_TABLE_PRODUCTS =
            "create table " + PRODUCTS + " (" +
                    "id integer primary key," +
                    "cat_id integer," +
                    "name text," +
                    "image text," +
                    "foreign key(cat_id) references " + CATEGORIES + "(id)" +
                    ")";

    public static final String INSERT_CATEGORY = "insert into " + CATEGORIES + " values(?,?);";
    public static final String INSERT_PRODUCT = "insert into " + PRODUCTS + " values(?,?,?,?);";
}
