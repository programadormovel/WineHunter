package br.com.buscarvinhos;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Alterar extends Activity {

    private static final int IMAGE_GALLERY_REQUEST = 20;
    private static final int CAMERA_RESULT = 5;
    Bitmap bitmap = null;
    ImageView imagemAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alterar_activity);

        //Capturando o codigo que foi informado ao clicar na lista (Recuperou a inst�ncia do objeto).
        Intent it = getIntent();
        int id = it.getIntExtra("codigo", 0);

        //Abrindo/Conectando no Banco
        SQLiteDatabase db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);

        //Abrindo e apontando o cursor para o registro desejado (vamos selecionar via Select);
        Cursor cursor = db.rawQuery("Select * from vinho Where _id = ?", new String[]{String.valueOf(id)});

        //Caso o cursor tenha recuperado algum registro
        if (cursor.moveToFirst()) {
            EditText edt01AlterarNome = (EditText) findViewById(R.id.edt01AlterarNome);
            EditText edt02AlterarEmail = (EditText) findViewById(R.id.edt02AlterarEmail);
            EditText edt03AlterarTelefone = (EditText) findViewById(R.id.edt03AlterarTelefone);
            RadioGroup rdg01AlteraSexo = (RadioGroup) findViewById(R.id.rdg01AlteraSexo);
            //RadioButton rdb01AlterarMasculino = (RadioButton) findViewById(R.id.rdb01AlterarMasculino);
            //RadioButton rdb02AlterarFeminino = (RadioButton) findViewById(R.id.rdb02AlterarFeminino);

            //Inserindo as informa��es do cursor nos campos desejados
            edt01AlterarNome.setText(cursor.getString(1));
            edt02AlterarEmail.setText(cursor.getString(2));
            edt03AlterarTelefone.setText(cursor.getString(3));

            //Realizando a verifica��o do sexo e marcando a op��o correspondente
            String sexo = cursor.getString(4);
            if (sexo.equals("M")) {
                rdg01AlteraSexo.check(R.id.rdb01AlterarMasculino);
            } else {
                rdg01AlteraSexo.check(R.id.rdb02AlterarFeminino);
            }

            //Recuperar imagem e converte-la de BLOB para BITMAP
            byte[] foto = cursor.getBlob(5);
            bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            imagemAgenda = (ImageView) findViewById(R.id.ivAlterarFoto);

            imagemAgenda.setImageBitmap(bitmap);
        }
    }

    public void bnt01AlterarAlterarClick(View v) {
        //Especificar a��es ao clicar no bot�o Alterar
        //Declarando e vinculando os objetos que usaremos nesta tela
        EditText edt01AlterarNome = (EditText) findViewById(R.id.edt01AlterarNome);
        EditText edt02AlterarEmail = (EditText) findViewById(R.id.edt02AlterarEmail);
        EditText edt03AlterarTelefone = (EditText) findViewById(R.id.edt03AlterarTelefone);
        RadioButton rdb01AlterarMasculino = (RadioButton) findViewById(R.id.rdb01AlterarMasculino);
        RadioButton rdb02AlterarFeminino = (RadioButton) findViewById(R.id.rdb02AlterarFeminino);

        byte[] foto = DbBitmapUtility.getBytes(bitmap);

        //Validando Campos Obrigat�rios
        if (edt01AlterarNome.getText().toString().length() <= 0) {
            edt01AlterarNome.setError("Preencha o campo nome!");
            edt01AlterarNome.requestFocus();
        } else if (edt02AlterarEmail.getText().toString().length() <= 0) {
            edt02AlterarEmail.setError("Preencha o campo email!");
            edt02AlterarEmail.requestFocus();
        } else if (edt03AlterarTelefone.getText().toString().length() <= 0) {
            edt03AlterarTelefone.setError("Preencha o campo telefone!");
            edt03AlterarTelefone.requestFocus();
        } else {
            //Caso os campos obrigat�rios estejam preenchidos, gravar o registro
            try {
                //Abrindo o banco de dados e coletando as informa��es digitadas
                SQLiteDatabase db = openOrCreateDatabase("Agenda_Foto.db", Context.MODE_PRIVATE, null);
                ContentValues cvt = new ContentValues();
                cvt.put("nome", edt01AlterarNome.getText().toString());
                cvt.put("email", edt02AlterarEmail.getText().toString());
                cvt.put("fone", edt03AlterarTelefone.getText().toString());

                //Selecionando o sexo, conforme op��o selecionada
                String sexo = "";
                if (rdb01AlterarMasculino.isChecked()) {
                    sexo = "M";
                } else if (rdb02AlterarFeminino.isChecked()) {
                    sexo = "F";
                }
                cvt.put("sexo", sexo.toString());

                cvt.put("foto", foto);

                //Utilizando o c�digo que foi informado ao clicar no registro para altera��o
                Intent it = getIntent();
                int id = it.getIntExtra("codigo", 0);

                //Validando a grava��o e retornando a mensagem de status
                if (db.update("agenda", cvt, "_id=?", new String[]{String.valueOf(id)}) > 0) {
                    Toast.makeText(getBaseContext(), "Altera��o Realizada com Sucesso!!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Erro ao realizar Altera��o!!!", Toast.LENGTH_SHORT).show();
                }
                db.close();
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void bnt02AlterarApagarClick(View v) {
        try {
            //Conectando no banco de dados
            final SQLiteDatabase db = openOrCreateDatabase("Agenda_Foto.db", Context.MODE_PRIVATE, null);

            //Capturando o c�digo que foi informado ao clicar na lista (Recuperou a inst�ncia do objeto)
            Intent it = getIntent();
            final int id = it.getIntExtra("codigo", 0);

            //Criando o objeto Builder (Estilo Caixa de Mensagem)
            Builder msg = new Builder(Alterar.this);
            msg.setMessage("Deseja realmente excluir este registro?");
            msg.setNegativeButton("N�o", null);
            msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                //Caso o botao SIM seja clicado, vamos realizar a exclus�o (delete) do registro na tabela agenda
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (db.delete("agenda", "_id=?", new String[]{String.valueOf(id)}) > 0) {
                        Toast.makeText(getBaseContext(), "Exclus�o Realizada com Sucesso!!!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Erro ao realizar Exclus�o do Registro!!!", Toast.LENGTH_SHORT).show();
                    }
                    db.close();
                }
            });
            msg.show();
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void btnFotoProdutoAlterarClick(View v) {

        //invoca a imagem da galeria utilizando um intent impl�cito
        Intent intent = new Intent(Intent.ACTION_PICK);

        //Log.i("foto",""+intent);

        //Buscar arquivo (imagem) no SD Card (Galeria)
        File imagemGaleria = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String caminho = imagemGaleria.getPath();

        //recupera o URI (caminho da imagem no SD Card)
        Uri data = Uri.parse(caminho);

        //define o tipo e formato da informa��o (imagem). Recupera todos os tipos de imagens.
        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

    public void btnCapturarFotoAlterarClick(View v) {
        // Utilizando um Intent impl�cito para invocar a c�mera do celular
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Iniciando o Intent e antecipando o resultado
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //VAI ENTRAR NESTE IF, SE A IMAGEM FOI ENCONTRADA
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                //if(requestCode == 0){
                //VE ARE HEARING BACK FROM THE IMAGE GALLERY

                //O endere�o da imagem no SD Card
                Uri imageUri = data.getData();

                //declara um stream (seguimento de dados) para ler a imagem recuperada do SD Card
                InputStream inputStream;

                //recuperando a sequencia de entrada, baseada no caminho (uri) da imagem
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    //recuperando um bitmap do stream
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    //mostrar a imagem escolhida
                    imagemAgenda = (ImageView) findViewById(R.id.ivAlterarFoto);
                    imagemAgenda.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_RESULT) {

                // Recebendo a imagem capturada pela c�mera
                bitmap = (Bitmap) data.getExtras().get("data");

                // Inserindo imagem no ImageView
                imagemAgenda = (ImageView) findViewById(R.id.ivAlterarFoto);
                imagemAgenda.setImageBitmap(bitmap);
            }
        }
    }

}
