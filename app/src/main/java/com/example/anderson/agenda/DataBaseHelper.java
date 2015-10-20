package com.example.anderson.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    private static final String BANCO_DADOS = "Contatos";
    private static int VERSAO = 1;
    public DataBaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE AGENDA (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome VARCHAR(100)," +
                "idade INTEGER," +
                "cidade VARCHAR(80)," +
                "uf INTEGER," +
                "email VARCHAR(100)," +
                "foto BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long addAgenda(Agenda a){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("nome",a.getNome());
        values.put("idade",a.getIdade());
        values.put("cidade",a.getCidade ());
        values.put("uf",a.getUf());
        values.put("email",a.getEmail());
        values.put("foto",a.getFoto());

        long id = db.insert("AGENDA",null,values);

        db.close();
        return id;
    }

    public Agenda getAgenda(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM AGENDA WHERE id = ?",new String[]{String.valueOf(id)});
        Agenda agenda = new Agenda();

        if (cursor != null) {
            cursor.moveToFirst();

            agenda.setId(cursor.getInt(0));
            agenda.setNome(cursor.getString(1));
            agenda.setIdade(cursor.getInt(2));
            agenda.setCidade(cursor.getString(3));
            agenda.setUf(cursor.getInt(4));
            agenda.setEmail(cursor.getString(5));
            agenda.setFoto(cursor.getBlob(6));
        }
        return agenda;
    }
}
