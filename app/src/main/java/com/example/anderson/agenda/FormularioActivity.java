package com.example.anderson.agenda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;


public class FormularioActivity extends ActionBarActivity {

    EditText nome, idade, cidade, email;
    Spinner uf;
    ImageView foto;
    Button salvar;
    private static final int FOLDER_REQUEST = 0;
    private static final int CAMERA_REQUEST = 1;
    Uri uriImagem;
    int ID_CONTATO;
    MenuItem itemSalva,itemAltera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nome = (EditText) findViewById(R.id.nome);
        idade = (EditText) findViewById(R.id.idade);
        cidade = (EditText) findViewById(R.id.cidade);
        uf = (Spinner) findViewById(R.id.uf);
        email = (EditText) findViewById(R.id.email);
        foto = (ImageView) findViewById(R.id.foto);
        salvar = (Button) findViewById(R.id.salvar);

        if(getIntent().hasExtra("ID_CONTATO")){
            ID_CONTATO = Integer.parseInt(getIntent().getStringExtra("ID_CONTATO"));

            setValues();

            setEditable(false);

            getSupportActionBar().setTitle(nome.getText().toString());
        }else{
            nome.requestFocus();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().hasExtra("ID_CONTATO")){
            getMenuInflater().inflate(R.menu.menu_visualiza, menu);

            itemSalva = menu.findItem(R.id.menu_salva);
            itemAltera = menu.findItem(R.id.menu_altera);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_altera) {
            setEditable(true);
            item.setVisible(false);
            itemSalva.setVisible(true);
            return true;
        }else if(id == R.id.menu_salva){
            setEditable(false);
            item.setVisible(false);
            itemAltera.setVisible(true);
            return true;
        }else if(id == R.id.menu_deleta){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Apagar");
            builder.setMessage("Deseja apagar " + nome.getText().toString() + " da sua lista de contatos?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        DataBaseHelper helper = new DataBaseHelper(getApplicationContext());
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.delete("AGENDA","id = ?",new String[]{String.valueOf(ID_CONTATO)});
                        Toast.makeText(getApplicationContext(),nome.getText().toString() + " foi apagado da sua lista!",Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Erro ao tentar apagar!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("Não",null);
            builder.show();
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void salvar(View view) {
        foto.setDrawingCacheEnabled(true);
        foto.buildDrawingCache();
        byte[] bytes = Utilitarios.getBytes(foto.getDrawingCache());

        Agenda a = new Agenda();

        try {
            a.setNome(nome.getText().toString());
            a.setIdade(Integer.parseInt(idade.getText().toString()));
            a.setCidade(cidade.getText().toString());
            a.setUf(uf.getSelectedItemPosition());
            a.setEmail(email.getText().toString());
            a.setFoto(bytes);

            if(getIntent().hasExtra("ID_CONTATO")){
                DataBaseHelper helper = new DataBaseHelper(this);
                SQLiteDatabase dbh = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("foto",a.getFoto());
                values.put("nome",a.getNome());
                values.put("idade", a.getIdade());
                values.put("cidade", a.getCidade());
                values.put("uf", a.getUf());
                values.put("email",a.getEmail());

                dbh.update("Agenda",values,"id = ?", new String[]{String.valueOf(ID_CONTATO)});
                Toast.makeText(this, a.getNome().toString() + " foi editado com sucesso!", Toast.LENGTH_SHORT).show();
            }else {
                DataBaseHelper dbh = new DataBaseHelper(this);
                dbh.addAgenda(a);
                Toast.makeText(this, a.getNome().toString() + " foi adicionado aos contatos!", Toast.LENGTH_SHORT).show();
            }
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao adicionar contato!", Toast.LENGTH_LONG).show();
        }
    }

    public void opcaoImagem(View view) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.alert_dialog_photo);
        dialog.setTitle("Escolhar o local da foto");
        dialog.show();

        Button tirarFoto = (Button) dialog.findViewById(R.id.tirar_foto);
         tirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST);
                dialog.dismiss();
            }
        });
        Button buscarFoto = (Button) dialog.findViewById(R.id.buscar_foto);
        buscarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Escolha a imagem"), FOLDER_REQUEST);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.foto);
            imageView.setImageBitmap(photo);
        }
        else if (requestCode == FOLDER_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImg = data.getData();
            try {
                Bitmap photo = (Bitmap) MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                ImageView imageView = (ImageView) findViewById(R.id.foto);
                imageView.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,"Erro ao buscar imagem!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setValues(){
        DataBaseHelper db = new DataBaseHelper(this);
        Agenda a = db.getAgenda(ID_CONTATO);
        try{
            nome.setText(a.getNome());
            idade.setText(String.valueOf(a.getIdade()));
            cidade.setText(a.getCidade());
            uf.setSelection(a.getUf());
            email.setText(a.getEmail());
            foto.setImageBitmap(Utilitarios.getImage(a.getFoto()));
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Erro ao buscar informações!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEditable(boolean permission){
        foto.setEnabled(permission);
        nome.setEnabled(permission);
        idade.setEnabled(permission);
        cidade.setEnabled(permission);
        uf.setEnabled(permission);
        email.setEnabled(permission);
        if(permission == true){
            salvar.setVisibility(View.VISIBLE);
        }else{
            //Deixando o botão de salvar invisível
            salvar.setVisibility(View.INVISIBLE);
        }
    }
}
