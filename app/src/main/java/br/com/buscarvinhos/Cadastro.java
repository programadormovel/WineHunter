package br.com.buscarvinhos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Cadastro extends Activity {

    //CONSTANTES DA GALERIA DE FOTOS
    private static final int IMAGE_GALLERY_REQUEST = 20;
    private static final int CAMERA_RESULT = 5;

    //VARI�VEIS PARA CAPTURA DE IMAGEM
    Bitmap bitmap = null;
    ImageView imagemFoto;
    ImageView imagemFotoTexto;
    String caminho;
    EditText txtCaminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_activity);
    }

    public void bntCadastroCadastrarClick(View v) throws IOException {
        //Declarando e vinculando os objetos que usaremos nesta tela
        EditText edt01CadastroDescricao = (EditText) findViewById(R.id.edt01CadastroDescricao);
        EditText edt02CadastroVinicula = (EditText) findViewById(R.id.edt02CadastroVinicula);
        EditText edt03CadastroFornecedor = (EditText) findViewById(R.id.edt03CadastroFornecedor);
        EditText edt04CadastroAno = (EditText) findViewById(R.id.edt04CadastroAno);
        EditText edt05CadastroVolume = (EditText) findViewById(R.id.edt05CadastroVolume);
        EditText edt06CadastroValor = (EditText) findViewById(R.id.edt06CadastroValor);
        //RadioButton rdb01CadastroMasculino = (RadioButton) findViewById(R.id.rdb01CadastroMasculino);
        //RadioButton rdb02CadastroFeminino = (RadioButton) findViewById(R.id.rdb02CadastroFeminino);

        //Vinculo do objeto visual (ImageView) da foto
        imagemFoto = (ImageView) findViewById(R.id.ivCadastroFoto);
        imagemFotoTexto = (ImageView) findViewById(R.id.ivFotoTexto);

        //Cria��o do array de bytes para ser gravado no banco de dados
        byte[] foto = DbBitmapUtility.getBytes(bitmap);
        //Array de bytes convertido para texto
        String fotoTexto = DbBitmapUtility.encodeTobase64(bitmap);

        //Validando Campos Obrigat�rios
        if (edt01CadastroDescricao.getText().toString().length() <= 0) {
            edt01CadastroDescricao.setError("Preencha o campo nome do vinho!");
            edt01CadastroDescricao.requestFocus();
        } else if (edt02CadastroVinicula.getText().toString().length() <= 0) {
            edt02CadastroVinicula.setError("Preencha o campo vin�cula!");
            edt02CadastroVinicula.requestFocus();
        } else if (edt03CadastroFornecedor.getText().toString().length() <= 0) {
            edt03CadastroFornecedor.setError("Preencha o campo fornecedor!");
            edt03CadastroFornecedor.requestFocus();
        } else if (edt04CadastroAno.getText().toString().length() <= 0) {
            edt04CadastroAno.setError("Preencha o campo ano!");
            edt04CadastroAno.requestFocus();
        } else if (edt05CadastroVolume.getText().toString().length() <= 0) {
            edt05CadastroVolume.setError("Preencha o campo volume!");
            edt05CadastroVolume.requestFocus();
        } else if (edt06CadastroValor.getText().toString().length() <= 0) {
            edt06CadastroValor.setError("Preencha o campo valor!");
            edt06CadastroValor.requestFocus();
        } else if (bitmap == null) {
            edt01CadastroDescricao.setError("Inserir foto do produto!");
            edt01CadastroDescricao.requestFocus();
        } else {
            //Caso os campos obrigat�rios estejam preenchidos, gravar o registro
            try {
                //Abrindo o banco de dados e coletando as informa��es digitadas
                SQLiteDatabase db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
                ContentValues cvt = new ContentValues();
                cvt.put("descricao", edt01CadastroDescricao.getText().toString());
                cvt.put("vinicula", edt02CadastroVinicula.getText().toString());
                cvt.put("fornecedor", edt03CadastroFornecedor.getText().toString());
                cvt.put("ano", edt04CadastroAno.getText().toString());
                cvt.put("volume", edt05CadastroVolume.getText().toString());
                cvt.put("valor", edt06CadastroValor.getText().toString());
                //Inserindo a imagem do ImageView no banco de dados SQLite
                cvt.put("foto", foto);
                cvt.put("foto_texto", fotoTexto);
                cvt.put("caminho_foto", txtCaminhoFoto.getText().toString());

                //Validando a grava��o e retornando a mensagem de status
                if (db.insert("vinho", "_id", cvt) > 0) {
                    Toast.makeText(getBaseContext(), "Cadastro Realizado com Sucesso!!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Erro ao realizar Cadastro!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //M�todos de manipula��o de imagens
    public void btnInserirFotoClick(View v) {

        //invoca a imagem da galeria utilizando um intent impl�cito
        Intent intent = new Intent(Intent.ACTION_PICK);

        //Buscar arquivo (imagem) no SD Card (Galeria)
        File imagemGaleria = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        txtCaminhoFoto = (EditText) findViewById(R.id.txtCaminhoFoto);
        caminho = imagemGaleria.getPath();
        txtCaminhoFoto.setText(caminho);

        //recupera o URI (caminho da imagem no SD Card)
        Uri data = Uri.parse(caminho);

        //define o tipo e formato da informa��o (imagem). Recupera todos os tipos de imagens.
        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

    public void btnCapturarFotoClick(View v) {
        // Utilizando um Intent impl�cito para invocar a c�mera do celular
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // Iniciando o Intent e antecipando o resultado
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */

    //COPIAR O M�TODO ABAIXO PARA A CLASSE DA CAPTURA
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
                    imagemFoto = (ImageView) findViewById(R.id.ivCadastroFoto);
                    imagemFoto.setImageBitmap(bitmap);

                    imagemFotoTexto = (ImageView) findViewById(R.id.ivFotoTexto);
                    imagemFotoTexto.setImageBitmap(bitmap);

                    txtCaminhoFoto = (EditText) findViewById(R.id.txtCaminhoFoto);
                    txtCaminhoFoto.setText(imageUri.getPath());

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_RESULT) {

                // Recebendo a imagem capturada pela c�mera
                bitmap = (Bitmap) data.getExtras().get("data");

                // Inserindo imagem no ImageView
                imagemFoto = (ImageView) findViewById(R.id.ivCadastroFoto);
                imagemFoto.setImageBitmap(bitmap);

                imagemFotoTexto = (ImageView) findViewById(R.id.ivFotoTexto);
                imagemFotoTexto.setImageBitmap(bitmap);

                txtCaminhoFoto = (EditText) findViewById(R.id.txtCaminhoFoto);
                txtCaminhoFoto.setText(data.getExtras().get("data").toString());
            }
        }
    }

}
