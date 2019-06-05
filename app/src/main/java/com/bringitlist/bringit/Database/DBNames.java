package com.bringitlist.bringit.Database;

// esta class serve para ter as cenas ja definidas e nao me enganar ao longo das varias vezes que vou escrever esses valores
public class DBNames {

    private DBNames() {
    }

    public static final String PRODUCTS = "products";
    public static final String CATEGORIES = "categories";
    public static final String HISTORY = "history";
    public static final String USERS = "users";
    public static final String CARTS = "carts";


    static final String CREATE_TABLE_USERS =
            "create table " + USERS + " (" +
                    "id integer primary key AUTOINCREMENT," +
                    "username text not null," +
                    "password_hash text not null," +
                    ")";

    static final String CREATE_TABLE_CATEGORIES = "create table " + CATEGORIES + " (id integer primary key,name text not null);";
    static final String CREATE_TABLE_PRODUCTS =
            "create table " + PRODUCTS + " (" +
                    "id integer primary key," +
                    "cat_id integer not null," +
                    "name text not null," +
                    "price real not null," +
                    "image text not null," +
                    "foreign key(cat_id) references " + CATEGORIES + "(id)" +
                    ")";

    static final String CREATE_TABLE_HISTORY =
            "create table " + HISTORY + " (" +
                    "id integer primary key AUTOINCREMENT," +
                    "user_id integer," +
                    "prod_id integer not null," +
                    "amount integer not null," +
                    "date text DEFAULT CURRENT_TIMESTAMP," +
                    "foreign key(user_id) references " + USERS + "(id)" +
                    "foreign key(prod_id) references " + PRODUCTS + "(id)" +
                    ")";

    static final String CREATE_TABLE_CARTS =
            "create table " + CARTS + " (" +
                    "id integer primary key AUTOINCREMENT" +
                    "user_id integer," +
                    "prod_id text not null," +
                    "amount text not null," +
                    "foreign key(user_id) references " + USERS + "(id)" +
                    "foreign key(prod_id) references " + PRODUCTS + "(id)" +
                    ")";

    public static final String INSERT_CATEGORY = "insert into " + CATEGORIES + " values(?,?);";
    public static final String INSERT_PRODUCT = "insert into " + PRODUCTS + " values(?,?,?,?,?);";
    public static final String INSERT_HISTORY = "insert into " + HISTORY + "(user_id,prod_id,amount) values(?,?,?);";
    public static final String INSERT_USER = "insert into " + USERS + "(prod_id,amount) values(?,?);";
    public static final String INSERT_CART_ITEM = "insert into " + CARTS + "(user_id,prod_id,amount) values(?,?,?);";
}
