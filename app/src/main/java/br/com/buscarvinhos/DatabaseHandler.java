package br.com.buscarvinhos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@SuppressLint("DefaultLocale")
public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "Motor.db";
    // Agenda table name
    private static final String TABLE_VINHO = "vinho";
    // ContAgenda Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_DESCRICAO = "descricao";
    private static final String KEY_VINICULA = "vinicula";
    private static final String KEY_FORNECEDOR = "fornecedor";
    private static final String KEY_ANO = "ano";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_VALOR = "valor";
    private static final String KEY_CAMINHO_FOTO = "caminho_foto";
    private static final String KEY_FOTO_TEXTO = "foto_texto";
    private static final String KEY_FOTO = "foto";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar Tabela agenda
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_VINHO
                + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_DESCRICAO + " VARCHAR(256), " + KEY_VINICULA
                + " VARCHAR(256), " + KEY_FORNECEDOR + " VARCHAR(256), "
                + KEY_ANO + " INTEGER, " + KEY_VOLUME + " NUMBER(15,2), "
                + KEY_VALOR + " NUMBER(15,2), " + KEY_CAMINHO_FOTO
                + " VARCHAR(256), " + KEY_FOTO_TEXTO + " VARCHAR(256), "
                + KEY_FOTO + " BLOB);";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VINHO);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     *
     * @throws IOException
     */

    @SuppressLint("DefaultLocale")
    // Adding new contact
    void addVine(String descricao, String vinicula, String fornecedor, int ANO,
                 double VOLUME, double VALOR, String caminhoFoto, String fotoTexto,
                 byte[] foto) throws IOException {
        SQLiteDatabase db = this.getWritableDatabase();

        // Bitmap bitmap = DbBitmapUtility.getImage(foto);
        // byte[] foto1 = DbBitmapUtility.getBytes(bitmap);
        // Blob fotoBlob = (Blob) new java.io.ByteArrayInputStream(foto);
        byte[] descompressada = null;
        try {
            descompressada = CompressionUtils.decompress(foto);
        } catch (DataFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream in = new ByteArrayInputStream(descompressada);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRICAO, descricao.toUpperCase());
        values.put(KEY_VINICULA, vinicula.toUpperCase());
        values.put(KEY_FORNECEDOR, fornecedor.toUpperCase());
        values.put(KEY_ANO, ANO);
        values.put(KEY_VOLUME, VOLUME);
        values.put(KEY_VALOR, VALOR);
        values.put(KEY_CAMINHO_FOTO, caminhoFoto);
        values.put(KEY_FOTO_TEXTO, fotoTexto);

        if (bmp != null) {
            bmp.compress(CompressFormat.PNG, 100, blob);
            byte[] bitmapdata = blob.toByteArray();
            values.put(KEY_FOTO, bitmapdata);
        } else {
            values.put(KEY_FOTO, foto);
        }

        // Inserting Row
        db.insert(TABLE_VINHO, null, values);
        db.close(); // Closing database connecting
    }

    // Updating single contact
    public int updateVine(int id, String nome, String vinicula,
                          String fornecedor, int ano, double volume, double valor,
                          String caminhoFoto, String fotoTexto, byte[] foto1) {
        SQLiteDatabase db = this.getWritableDatabase();

        /*
         * byte[] fotoBytes = DbBitmapUtility.getBytes(foto1);
         *
         * String fotoTexto[] = null;
         *
         * for(int cont=0; cont<fotoBytes.length; cont++){ fotoTexto[cont] =
         * String.valueOf(fotoBytes[cont]); }
         */

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRICAO, nome.toUpperCase());
        values.put(KEY_VINICULA, vinicula.toUpperCase());
        values.put(KEY_FORNECEDOR, fornecedor.toUpperCase());
        values.put(KEY_ANO, ano);
        values.put(KEY_VOLUME, volume);
        values.put(KEY_VALOR, valor);
        values.put(KEY_CAMINHO_FOTO, caminhoFoto);
        values.put(KEY_FOTO_TEXTO, fotoTexto);
        values.put(KEY_FOTO, foto1);

        // updating row
        return db.update(TABLE_VINHO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Deleting single contact
    public void deleteVine(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VINHO, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Deleting all contact
    public void deleteAllVines() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_VINHO);
        db.close();
    }

    // Getting contacts Count
    public int getVinesCount() {

        String countQuery = "SELECT * FROM " + TABLE_VINHO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        try {
            if (cursor.moveToFirst()) {
                count = cursor.getCount();
            }
            return count;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Getting All Vines
    public List<Vinho> getAllVines() {
        List<Vinho> vineList = new ArrayList<Vinho>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VINHO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Vinho vinho = new Vinho();
                vinho.setID(cursor.getInt(0));
                vinho.setDescricao(cursor.getString(1));
                vinho.setVinicula(cursor.getString(2));
                vinho.setFornecedor(cursor.getString(3));
                vinho.setAno(Integer.parseInt(cursor.getString(4)));

                vinho.set_caminho_foto(cursor.getString(5));
                vinho.set_foto_texto(cursor.getString(6));
                vinho.set_foto(cursor.getBlob(7));
                // Adding contact to list
                vineList.add(vinho);
            } while (cursor.moveToNext());
        }

        // return contact list
        return vineList;
    }

}
