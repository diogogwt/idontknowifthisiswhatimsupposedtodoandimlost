package com.bringitlist.bringit.Database;

// esta class serve para ter as cenas ja definidas e nao me enganar ao longo das varias vezes que vou escrever esses valores
public class DatabaseNames {

    private DatabaseNames() {
    }

    public static final String COLUMN_ID = "id";

    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_CAT_ID = "id_cat";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE = "image";

    public static final String CREATE_TABLES =
            "create table " + TABLE_CATEGORIES + "(" +
                    COLUMN_ID + " integer primary key" +
                    COLUMN_NAME + " text" +
                    ");" +
                    "create table " + TABLE_PRODUCTS + "(" +
                    COLUMN_ID + " integer primary key," +
                    COLUMN_CAT_ID + " integer" +
                    COLUMN_NAME + " text" +
                    COLUMN_IMAGE + " text" +
                    "foreign key(" + COLUMN_CAT_ID + ") references " + TABLE_CATEGORIES + "(" + COLUMN_ID + ")" +
                    ")";

    public static final String INSERT_CATEGORY = "insert into " + TABLE_CATEGORIES + "values(?,?)";
    public static final String INSERT_PRODUCT = "insert into " + TABLE_PRODUCTS + "values(?,?,?,?)";
}
