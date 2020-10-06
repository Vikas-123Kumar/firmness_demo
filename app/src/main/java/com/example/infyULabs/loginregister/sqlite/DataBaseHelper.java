package com.example.infyULabs.loginregister.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.infyULabs.bean.FruitName;
import com.example.infyULabs.bean.ScanBean;
import com.example.infyULabs.loginregister.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.infyULabs.bean.ScanBean.fruit_id;
import static com.example.infyULabs.bean.ScanBean.scan_fruitName;
import static com.example.infyULabs.bean.ScanBean.scan_data;
import static com.example.infyULabs.bean.ScanBean.scan_time;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User_Manager.db";
    private static final String TABLE_USER = "user";
    private static final String Table_fruit = "fruit";
    private static final String TABLE_SCANDATA = "scan_data";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_MOBILE = "user_mobile";
    private static final String column_fruit_name = "fruit";
    private static final String column_fruit_id = "fruit_id";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    // create table in sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_MOBILE + " TEXT" + ")";

    private String CREATE_SCN_TABLE = "CREATE TABLE " + TABLE_SCANDATA + "("
            + fruit_id + " INTEGER PRIMARY KEY AUTOINCREMENT," + scan_fruitName + " TEXT," + scan_data + " TEXT," + scan_time + " TEXT" + ")";
    private String CREATE_FRUIT_TABLE = "CREATE TABLE " + Table_fruit + "(" + column_fruit_id + " INTEGER PRIMARY KEY AUTOINCREMENT," + column_fruit_name + " TEXT" + ")";
    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SCN_TABLE);
        db.execSQL(CREATE_FRUIT_TABLE);
        Log.e("table created", CREATE_USER_TABLE + "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_NAME, user.getName());
        contentValues.put(COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());
        contentValues.put(COLUMN_USER_MOBILE, user.getMobile_no());
        // insert row
        Log.e("table data", contentValues + "");
        sqLiteDatabase.insert(TABLE_USER, null, contentValues);
        sqLiteDatabase.close();
    }

    public void addScanData(ScanBean scanBean) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues scanValue = new ContentValues();
        scanValue.put(scan_fruitName, scanBean.getFruitName());
        scanValue.put(scan_data, scanBean.getScanData());
        scanValue.put(scan_time, format.format(scanBean.getScanTime()));
        sqLiteDatabase.insert(TABLE_SCANDATA, null, scanValue);

        sqLiteDatabase.close();
    }

    public ArrayList<ScanBean> getScanData() {
        ArrayList<ScanBean> scanList = new ArrayList<>();
        String str_reg = "SELECT * FROM " + Table_fruit;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(str_reg, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ScanBean scanBean = new ScanBean();
                    scanBean.setScanData(cursor.getString(cursor.getColumnIndex(scan_data)));
                    scanBean.setFruitName(cursor.getString(cursor.getColumnIndex(scan_fruitName)));
                    setDate(cursor, scanBean);
                    scanList.add(scanBean);
                } while (cursor.moveToNext());
            }
        }
        return scanList;
    }

    private void setDate(Cursor cursor, ScanBean scanBean) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex(scan_time)));
            Log.e("date", "" + date);

        } catch (ParseException e) {
            Log.e("date conversion error", "Could not string to date during fetch");
            e.printStackTrace();
        }
        scanBean.setScanTime(date);
    }

    public void addFruit(FruitName fruitName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_fruit_name, fruitName.getFruit_name());
        sqLiteDatabase.insert(Table_fruit, null, contentValues);
        sqLiteDatabase.close();
        Log.e("database value", fruitName.getFruit_name() + "");
    }

    public List<FruitName> getFruit() {
        ArrayList<FruitName> registration_list = new ArrayList<>();
        String str_reg = "SELECT * FROM " + Table_fruit;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(str_reg, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    FruitName fruitName = new FruitName();
                    fruitName.setFruit_name(cursor.getString(cursor.getColumnIndex(column_fruit_name)));
                    registration_list.add(fruitName);
                    Log.e("list", registration_list.toString());
                } while (cursor.moveToNext());
            }
        }
        return registration_list;
    }

    public boolean checkFruit(String fruitName) {
        // array of columns to fetch
        String[] columns = {
                column_fruit_id
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = column_fruit_name + " = ?";

        // selection argument
        String[] selectionArgs = {fruitName};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'abc@gmail.com';
         */
        Cursor cursor = db.query(Table_fruit, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_MOBILE
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_MOBILE)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_MOBILE, user.getMobile_no());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    public boolean checkUser(String email) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";

        // selection argument
        String[] selectionArgs = {email};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'abc@gmail.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param name
     * @param password
     * @return true/false
     */
    public boolean checkUser(String name, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {name, password};
        Log.e("name", name + "pass" + password);
        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'abc@gmail.com' AND user_password = '12345';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }
}