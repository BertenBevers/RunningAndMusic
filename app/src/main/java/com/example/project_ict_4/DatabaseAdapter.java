package com.example.project_ict_4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Berten on 6/05/2015.
 */
public class DatabaseAdapter  {

    DatabaseHandler handler;
    public DatabaseAdapter (Context context){
        handler = new DatabaseHandler(context);
    }
    public void insertData(String title, String artist, double bpm, int songid)
    {
        SQLiteDatabase db = handler.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.TITLE,title );
        contentValues.put(DatabaseHandler.ARTIST, artist);
        contentValues.put(DatabaseHandler.BPM, bpm);
        contentValues.put(DatabaseHandler.UID,songid);
        db.insert(DatabaseHandler.TABLE_NAME, null, contentValues);
    }

    public long getSongID(double bpm1, double bpm2)
    {
       //int i=0;
        //int[] array = new int[50];
        List<Integer> list = new ArrayList<>();
        long songID = 0;
        SQLiteDatabase db = handler.getWritableDatabase();
        String[] columns = {DatabaseHandler.UID};
        Cursor cursor=db.query(DatabaseHandler.TABLE_NAME, columns,"Bpm BETWEEN "+bpm1+" AND "+bpm2,null,null,null,null);
        StringBuffer buffer = new StringBuffer();

        while(cursor.moveToNext()){

            int sid = cursor.getInt(0);
            list.add(sid);
          //  array[i] = sid;
          //  i++;

        }
        Collections.shuffle(list);
        songID = list.get(0);


        /// doe iets
        return  songID;
    }

    public String getAllData()
    {
        SQLiteDatabase db = handler.getWritableDatabase();

        String[] columns = {DatabaseHandler.UID, DatabaseHandler.TITLE, DatabaseHandler.ARTIST, DatabaseHandler.BPM};
        Cursor cursor=db.query(DatabaseHandler.TABLE_NAME, columns, null,null,null,null,null);
        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()){
            int cid = cursor.getInt(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            double bpm = cursor.getDouble(3);
            buffer.append("|"+cid+"#"+title+"#"+ artist +"#"+Double.toString(bpm)+"|");
        }
        return buffer.toString();
    }

    class DatabaseHandler extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "runningandmusic.db";
        private static final String TABLE_NAME="songs";
        private static final int DATABASE_VERSION=1;
        private static final String UID="_id";
        private static final String TITLE="Title";
        private static final String ARTIST="Artist";
        private static final String BPM="Bpm";


        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+UID+" INTEGER, "+TITLE+" VARCHAR(255), "+ARTIST+" VARCHAR(255), "+BPM+" DOUBLE);";
        private static final String DROP_TABLE="DROP TABLE IF EXISTS" +TABLE_NAME;
        private Context context;

        public DatabaseHandler(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
            this.context = context;
            Toast.makeText(this.context , "constructor",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public  void onCreate(SQLiteDatabase db){
            //Create our database here
            db.execSQL(CREATE_TABLE);
            Toast.makeText(this.context , "on create",
                    Toast.LENGTH_LONG).show();

        }
        @Override
        public  void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(DROP_TABLE);

            onCreate(db);

        }
    }
}
