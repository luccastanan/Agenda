package com.example.anderson.agenda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener{

    private List<HashMap<String, Object>> contatos;
    ListView listView;
    TextView lblVazio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblVazio = (TextView) findViewById(R.id.lbl_vazio);
    }

    @Override
    public void onResume(){
        super.onResume();
        atualizarLista();
    }

    public void atualizarLista(){
        if(prepararLista().isEmpty()){
            lblVazio.setVisibility(View.VISIBLE);
        }else{
            lblVazio.setVisibility(View.INVISIBLE);
        }
        listarContatos();
    }

    private void listarContatos() {
        String[] de = {"foto","nome"};
        int[] para = {R.id.foto_lista,R.id.nome_lista};

        SimpleAdapter adapter = new SimpleAdapter(this,prepararLista(),R.layout.item_contato,de,para);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.lista_contato);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    private List<HashMap<String, Object>> prepararLista() {

        DataBaseHelper helper = new DataBaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM AGENDA",null);
        cursor.moveToFirst();

        contatos = new ArrayList<HashMap<String, Object>>();

        if(cursor.getCount()>0) {
            for (int i = 0; i < cursor.getCount(); i++) {

                HashMap<String, Object> item = new HashMap<String, Object>();
                String id = cursor.getString(0);
                String nome = cursor.getString(1);
                byte[] foto = cursor.getBlob(6);

                item.put("id", id);
                item.put("foto", Utilitarios.getImage(foto));
                item.put("nome", nome);

                contatos.add(item);
                cursor.moveToNext();
            }
        }else{
            contatos.clear();
        }
        cursor.close();
        return contatos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tela_cadastro) {
            startActivity(new Intent(this,FormularioActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int idContato = Integer.parseInt((String) contatos.get(position).get("id"));
        Intent i = new Intent(getApplicationContext(),FormularioActivity.class);
        i.putExtra("ID_CONTATO", String.valueOf(idContato));
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final int idContato = Integer.parseInt((String) contatos.get(position).get("id"));
        final String nomeContato = (String) contatos.get(position).get("nome");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apagar");
        builder.setMessage("Deseja apagar " + nomeContato + " da sua lista de contatos?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    DataBaseHelper helper = new DataBaseHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.delete("AGENDA","id = ?",new String[]{String.valueOf(idContato)});
                    atualizarLista();
                    Toast.makeText(getApplicationContext(), nomeContato + " foi apagado da sua lista!", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Erro ao tentar apagar!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNeutralButton("Não",null);
        builder.show();
        return true;
    }
}
