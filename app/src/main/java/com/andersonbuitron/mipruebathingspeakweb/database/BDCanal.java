package com.andersonbuitron.mipruebathingspeakweb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

import com.andersonbuitron.mipruebathingspeakweb.modelos.Canal;

import java.util.ArrayList;

/**
 * para el crud de Canal
 */

public class BDCanal {
    public static final int BOX_OFFICE = 0;
    private CanalHelper mHelper;
    private SQLiteDatabase mDatabase;
    Context context;

    public BDCanal(Context context) {
        mHelper = new CanalHelper(context);
        mDatabase = mHelper.getWritableDatabase();
        this.context = context;
    }

    public void insertCanal(Canal canal) {

        //compile the statement and start a transaction
        ContentValues valores = new ContentValues();
        mDatabase.beginTransaction();

        if (!canal.getId().equals("")) {
            valores.put(CanalHelper.DatosTabla.COLUMNA_ID, canal.getId());
            valores.put(CanalHelper.DatosTabla.COLUMNA_NOMBRE,canal.getNombre());
            valores.put(CanalHelper.DatosTabla.COLUMNA_API_KEY, canal.getApi_key_write());
            valores.put(CanalHelper.DatosTabla.COLUMNA_ICONO,canal.getIcono());

            Long newRow = mDatabase.insert(CanalHelper.DatosTabla.NOMBRE_TABLA, null, valores);
            Toast.makeText(context, "Se guardo el dato" + newRow, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, "el Id es obligatorio.", Toast.LENGTH_SHORT).show();
        }
        //set the transaction as successful and end the transaction
        //L.m("inserting entries " + listCanal.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Canal> leerCanales() {
        ArrayList<Canal> listCanal = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {
                CanalHelper.DatosTabla.COLUMNA_ID,
                CanalHelper.DatosTabla.COLUMNA_NOMBRE,
                CanalHelper.DatosTabla.COLUMNA_API_KEY,
                CanalHelper.DatosTabla.COLUMNA_ICONO,
        };
        Cursor cursor = mDatabase.query(CanalHelper.DatosTabla.NOMBRE_TABLA, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                //create a new Canal object and retrieve the data from the cursor to be stored in this Canal object
                Canal Canal = new Canal();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Canal object to contain our data
                Canal.setId(cursor.getString(cursor.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_ID)));
                Canal.setNombre(cursor.getString(cursor.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_NOMBRE)));
                Canal.setApi_key_write(cursor.getString(cursor.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_API_KEY)));
                Canal.setIcono(cursor.getString(cursor.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_ICONO)));
                //add the Canal to the list of Canal objects which we plan to return
                listCanal.add(Canal);
            }
            while (cursor.moveToNext());
        }
        return listCanal;
    }

    public Canal buscarCanal(String id){

        String[] projection = {
                CanalHelper.DatosTabla.COLUMNA_ID,
                CanalHelper.DatosTabla.COLUMNA_NOMBRE,
                CanalHelper.DatosTabla.COLUMNA_API_KEY,
                CanalHelper.DatosTabla.COLUMNA_ICONO,};
        // Filter results WHERE "title" = 'My Title'
        String selection = CanalHelper.DatosTabla.COLUMNA_ID + " = ?";
        String[] selectionArgs = {id};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = CanalHelper.DatosTabla.COLUMNA_NOMBRE + " DESC";
        Canal canal = null;
        try {
            Cursor c = mDatabase.query(CanalHelper.DatosTabla.NOMBRE_TABLA, projection, selection, selectionArgs, null, null, sortOrder);

            canal =  new Canal();
            c.moveToFirst();
            canal.setId(c.getString(c.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_ID)));
            canal.setIcono(c.getString(c.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_NOMBRE)));
            canal.setApi_key_write(c.getString(c.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_API_KEY)));
            canal.setIcono(c.getString(c.getColumnIndex(CanalHelper.DatosTabla.COLUMNA_ICONO)));

        } catch (android.database.CursorIndexOutOfBoundsException e) {
            Toast.makeText(context, "No se encontro registro alguno.", Toast.LENGTH_LONG).show();
        }

        return canal;
    }

    public void actualizarCanal(Canal canal){
        ContentValues valores = new ContentValues();
        valores.put(CanalHelper.DatosTabla.COLUMNA_NOMBRE,canal.getNombre());
        valores.put(CanalHelper.DatosTabla.COLUMNA_ICONO,canal.getIcono());

        String selection = CanalHelper.DatosTabla.COLUMNA_ID + "=?";
        String[] selectionArgs = {canal.getId()};

        int res = mDatabase.update(CanalHelper.DatosTabla.NOMBRE_TABLA,valores,selection,selectionArgs);

        if(res > 0){
            Toast.makeText(context, "Se actualizo el canal con id["+canal.getId()+"]", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"No se actualizo ningun registro: ",Toast.LENGTH_LONG).show();
        }
    }

    public void deleteCanal(String idCanal) {
        String selection3 = CanalHelper.DatosTabla.COLUMNA_ID + "=?";
        String[] selectionArgs3 = {idCanal};

        int res = mDatabase.delete(CanalHelper.DatosTabla.NOMBRE_TABLA, selection3, selectionArgs3);
        if(res > 0){
            Toast.makeText(context, "Se elimino el canal con id["+idCanal+"]", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"No se elimino ningun registro: ",Toast.LENGTH_LONG).show();
        }
        mDatabase.delete(CanalHelper.DatosTabla.NOMBRE_TABLA, null, null);
    }
    
    public class CanalHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "MiBaseDB.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String CREAR_TABLA_1 =
                "CREATE TABLE " + DatosTabla.NOMBRE_TABLA + " (" +
                        DatosTabla.COLUMNA_ID + " INTEGER PRIMARY KEY," +
                        DatosTabla.COLUMNA_NOMBRE + TEXT_TYPE + COMMA_SEP +
                        DatosTabla.COLUMNA_API_KEY + TEXT_TYPE +
                        DatosTabla.COLUMNA_ICONO + TEXT_TYPE + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DatosTabla.NOMBRE_TABLA;

        /* Inner class that defines the table contents */
        public final class DatosTabla implements BaseColumns {
            public static final String NOMBRE_TABLA = "Canal";
            public static final String COLUMNA_ID = "id";
            public static final String COLUMNA_NOMBRE = "nombre";
            public static final String COLUMNA_API_KEY = "api_key";
            public static final String COLUMNA_ICONO = "api_key";
        }

        public CanalHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREAR_TABLA_1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {

            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);

        }
    }
}
