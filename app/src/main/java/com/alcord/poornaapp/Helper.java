package com.alcord.poornaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class Helper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.alcord.poornaapp/databases/";
    private static String DB_NAME = "PoornaApp.sqlite";



    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private String TAG = "Helper";
    Cursor cursorGetData;
    String sigment;

    public Helper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * checking the database Availability based on Availability copying database
     * to the device data
     *
     * @return true (if Available)
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            File file = new File(myPath);
            if (file.exists() && !file.isDirectory())
                checkDB = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error is" + e.toString());
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * copying database from asserts to package location in mobile data
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    /**
     * Opening database for retrieving/inserting information
     *
     * @throws SQLException
     */
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;

        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Closing database after operation done
     */
    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    /**
     * getting information based on SQL Query
     *
     * @param sql
     * @return Output of Query
     */
    public Cursor getData(String sql) {
        try {
            createDataBase();
            openDataBase();
            cursorGetData = getReadableDatabase().rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursorGetData;
    }

    /**
     * Inserting information based on table name and values
     *
     * @param tableName
     * @param values
     * @return
     */
    public long insertData(String tableName, ContentValues values) {
        try {
            openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myDataBase.insert(tableName, null, values);
    }


    /**
     * Updating information based on table name and Condition
     *
     * @param tableName
     * @param values
     * @return
     */
    private int updateData(String tableName, ContentValues values, String condition) {
        openDataBase();
        return myDataBase.update(tableName, values, condition, null);
    }

    public void insertEventsoffers(ArrayList<ContactNumberBean> contactNumberBean) {

        try {
            int mTotalInsertedRows = 0;
            for (int j = 0; j < contactNumberBean.size(); j++) {
                ContentValues initialValues = new ContentValues();
                initialValues.put("number", (contactNumberBean.get(j).getmNumber()));
                initialValues.put("name", (contactNumberBean.get(j).getmName()));

                long rowId = insertData("ContactList", initialValues);
                if (rowId > 0) {
                    mTotalInsertedRows++;
                }
            }
            Log.e("ContactList", "inserted>" + mTotalInsertedRows);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void exportDatabse() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                 String currentDBPath = "//data//com.alcord.poornaapp//databases//PoornaApp.sqlite";
                 //String currentDBPath = DB_PATH + DB_NAME;
                String backupDBPath = "PoornaApp.sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Log.e("db", "copied");
                } else {
                    Log.e("db", "dbnotexist");
                }
            } else {
                Log.e("db", "notcopied");
            }
        } catch (Exception e) {
            Log.e("db", "error");
        }
    }

}



