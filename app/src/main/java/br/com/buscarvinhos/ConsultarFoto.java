package br.com.buscarvinhos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.tesseract.android.TessBaseAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import it.sephiroth.android.library.picasso.Picasso;

//Importações da classe OCR
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConsultarFoto extends Activity {

    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    // public static final String DATA_PATH =
    // Environment.getExternalStorageDirectory().toString() +
    // "/br.com.buscarvinhos/";
    private static final int IMAGE_GALLERY_REQUEST = 20;
    private static final int CAMERA_RESULT = 5;
    public static String searchURL = null;
    Bitmap bitmap = null;
    Uri imageUri = null;
    ImageView imagemVinho;
    EditText tv01ConsultarFotoNome;
    ListView lista;
    ArrayAdapter<VinhoBuscado> adapter;
    Cursor cursor;
    SQLiteDatabase db;
    EditText termo;
    TextView result;
    TextView tvPreco;
    TextView tvVinicula;
    TextView tvFornecedor;
    TextView tvNome;
    TextView tvLink;
    TextView tv01Vinhos;
    TextView tvVinhoSelecionado;
    ImageButton ibtn01BuscaLojas;
    TextView txv01ListarLayoutVinhosFotoID;
    TextView txv02ListarLayoutVinhosFotoNome;
    MinhaViewHolder holder = null;
    VinhoBuscado vinhoAtual;
    ImageButton btn02ConsultaFotoClicaFoto;

    // Buscador buscador = new Buscador();
    String descricao = "";
    private List<VinhoBuscado> listaVinho = new ArrayList<VinhoBuscado>();

//    public static final String PACKAGE_NAME = "br.com.buscavinhos";
  //  public static final String DATA_PATH = Environment
    //        .getExternalStorageDirectory().toString() + "/br.com.buscarvinhos/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // https://github.com/tesseract-ocr/tessdata
    public static final String LANG = "por";

    private static final String TAG = "ConsultarFoto.java";

    @SuppressLint("NewApi")
    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("imagem", ".jpg");

        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            org.apache.commons.io.IOUtils.copy(in, out);
        } finally {
            // tempFile.deleteOnExit();
        }
        return tempFile;
    }

    //Declarações importadas da classe OCR
    public static final String PACKAGE_NAME = "br.com.buscavinhos";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() + "/br.com.buscarvinhos/";

    // You should have the trained data file in assets folder
    // You can get them at:
    // https://github.com/tesseract-ocr/tessdata
    public static final String lang = "por";

    //private static final String TAG = "SimpleAndroidOCR.java";

    protected Button _button;
    // protected ImageView _image;
    //protected EditText _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_foto_activity);
        lista = (ListView) findViewById(R.id.lista01ConsultarFoto);
        lista.setAdapter(null);
        //btn02ConsultaFotoClicaFotoClick(imagemVinho);

        imagemVinho = (ImageView) findViewById(R.id.ivConsultarFotoFoto);
        tv01ConsultarFotoNome = (EditText) findViewById(R.id.tv01ConsultarFotoNome);

        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }



        // _image = (ImageView) findViewById(R.id.image);
        //_field = (EditText) findViewById(R.id.field);
        btn02ConsultaFotoClicaFoto = (ImageButton) findViewById(R.id.btn02ConsultaFotoClicaFoto);
        btn02ConsultaFotoClicaFoto.setOnClickListener(new ButtonClickHandler());

        _path = DATA_PATH + "/ocr.jpg";

        //Chamada automática da câmera
        //startCameraActivity();
        onSelectImageClick();
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.v(TAG, "Starting Camera app");
            //startCameraActivity();
            onSelectImageClick();

        }
    }

    // Simple android photo capture:
    // http://labs.makemachine.net/2010/03/simple-android-photo-capture/

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    /** Start pick image activity with chooser. */
    public void onSelectImageClick() {
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

//        if (resultCode == -1) {
//            onPhotoTaken();
//        } else {
//            Log.v(TAG, "User cancelled");
//        }

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //onPhotoTaken();
//            // VAI ENTRAR NESTE IF, SE A IMAGEM FOI ENCONTRADA
//            if (requestCode == IMAGE_GALLERY_REQUEST) {
//                // if(requestCode == 0){
//                // VE ARE HEARING BACK FROM THE IMAGE GALLERY
//
//                // O endere�o da imagem no SD Card
//                imageUri = data.getData();
//
//                // declara um stream (seguimento de dados) para ler a imagem
//                // recuperada do SD Card
//                InputStream inputStream;
//
//                // recuperando a sequencia de entrada, baseada no caminho (uri)
//                // da imagem
//                try {
//                    inputStream = getContentResolver()
//                            .openInputStream(imageUri);
//
//                    // recuperando um bitmap do stream
//                    bitmap = BitmapFactory.decodeStream(inputStream);
//
//                    // mostrar a imagem escolhida
//                    imagemVinho = (ImageView) findViewById(R.id.ivConsultarFotoFoto);
//
//                    // bitmap = verificaPosicaoImagem(imageUri.toString());
//                    imagemVinho.setImageBitmap(bitmap);
//
//                    // BufferedImage bImageFromConvert =
//                    // ImageIO.read(inputStream);
//
//                    /*descricao = leitura(bitmap);
//                    TextView tv01VinhoNome = (EditText) findViewById(R.id.tv01ConsultarFotoNome);
//                    tv01VinhoNome.setText(descricao);
//
//                    procuraOCRDecifrado();
//*/
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    Toast.makeText(getBaseContext(), e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else if (requestCode == CAMERA_RESULT) {
//
//                // Recebendo a imagem capturada pela c�mera
//                bitmap = (Bitmap) data.getExtras().get("data");
//
//                // Inserindo imagem no ImageView
//                imagemVinho = (ImageView) findViewById(R.id.ivConsultarFotoFoto);
//                imagemVinho.setImageBitmap(bitmap);
////                try {
////                    descricao = leitura(bitmap);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                TextView tv01VinhoNome = (EditText) findViewById(R.id.tv01ConsultarFotoNome);
////                tv01VinhoNome.setText(descricao);
////                procuraOCRDecifrado();
//            } else
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                InputStream inputStream;
                if (resultCode == RESULT_OK) {
                    imagemVinho = (ImageView) findViewById(R.id.ivConsultarFotoFoto);
                    try {
                        inputStream = getContentResolver().openInputStream(result.getUri());
                        // recuperando um bitmap do stream
                        bitmap = BitmapFactory.decodeStream(inputStream);

                        imagemVinho.setImageBitmap(bitmap);

                        //_path = DATA_PATH + "/" + result.getUri().getPath().toString();
                        try {
                            File file = bitmapParaFile(bitmap);
                            Uri outputFileUri = Uri.fromFile(file);
                            _path = file.getPath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        onPhotoTaken();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(
                            this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                            .show();

                    // Recebendo a imagem capturada pela c�mera
                    //bitmap = imagemVinho.getDrawingCache();
//
//                    // Inserindo imagem no ImageView
//                    imagemVinho = (ImageView) findViewById(R.id.ivConsultarFotoFoto);
//                    imagemVinho.setImageBitmap(bitmap);
//
//                    try {
//                        descricao = leitura(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    //EditText tv01VinhoNome = (EditText) findViewById(R.id.tv01ConsultarFotoNome);
                    //tv01VinhoNome.setText(descricao);
                    //procuraOCRDecifrado();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Log.v(TAG, "User cancelled");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    protected void onPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
        //Bitmap bitmap = imagemRecortada.getDrawingCache();

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            imagemVinho.setImageBitmap(bitmap);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            tv01ConsultarFotoNome.setText(tv01ConsultarFotoNome.getText().toString().length() == 0 ? recognizedText : tv01ConsultarFotoNome.getText() + " " + recognizedText);
            tv01ConsultarFotoNome.setSelection(tv01ConsultarFotoNome.getText().toString().length());
            descricao = recognizedText.toString();
            procuraOCRDecifrado();
        }

        // Cycle done.
    }

    //old code

    public void btn01ConsultarFotoBuscaFotoClick(View v) {

        // invoca a imagem da galeria utilizando um intent impl�cito
        Intent intent = new Intent(Intent.ACTION_PICK);

        // Log.i("foto",""+intent);

        // Buscar arquivo (imagem) no SD Card (Galeria)
        File imagemGaleria = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String caminho = imagemGaleria.getPath();

        // recupera o URI (caminho da imagem no SD Card)
        Uri data = Uri.parse(caminho);

        // define o tipo e formato da informa��o (imagem). Recupera todos os
        // tipos de imagens.
        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

//    public void btn02ConsultaFotoClicaFotoClick(View v) {
//        // Utilizando um Intent impl�cito para invocar a c�mera do celular
//        Intent cameraIntent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // Iniciando o Intent e antecipando o resultado
//        startActivityForResult(cameraIntent, CAMERA_RESULT);
//    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */

    // Inser��o do carregamento da lista do vinho da foto tirada ou recuperada
    @Override
    protected void onResume() {
        super.onResume();

        apagaVinhosBuscados();
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);



    }

    public void procuraOCRDecifrado() {
        vinhoAtual = new VinhoBuscado();
        int num = 50;

        // op��o de pesquisa
        int op = 4;
        // String searchURL = "";

        // +searchTerm
        // pesquisa padr�o
        if (op == 1) {
            searchURL = GOOGLE_SEARCH_URL
                    + "?q=pre�o de vinho "
                    + descricao.toString().trim()
                    + "&rlz=1C1CHZL_pt-BRBR748BR748&source=lnms&tbm=shop&sa=X&ved=0ahUKEwjhvqbrrrraAhVJjZAKHTiQAB8Q_AUICygC&biw=1093&bih=490"
                    + "&num=" + num;
            new MyAsyncTask().execute(searchURL);
            // pesquisa menor pre�o
        } else if (op == 2) {
            searchURL = GOOGLE_SEARCH_URL
                    + "?q=pre�o de vinho "
                    + descricao.toString().trim()
                    + "&biw=1366&bih=662&tbm=shop&tbs=p_ord:p&ei=VXPjWpW-NcqdwASemrGoBw&ved=0ahUKEwjVvIT1kdvaAhXKDpAKHR5NDHUQuw0ItQIoAg"
                    + "&num=" + num;
            new MyAsyncTask().execute(searchURL);
            // pesquisa maior pre�o
        } else if (op == 3) {
            searchURL = GOOGLE_SEARCH_URL
                    + "?q=pre�o de vinho "
                    + descricao.toString().trim()
                    + "&biw=1366&bih=662&tbm=shop&tbs=p_ord:pd&ei=83XjWrnmFYPEwASfy6GgCQ&ved=0ahUKEwj5q6K0lNvaAhUDIpAKHZ9lCJQQuw0IuAIoAw"
                    + "&num=" + num;
            new MyAsyncTask().execute(searchURL);
            // pesquisa melhor avalia��o
        } else if (op == 4) {
            searchURL = GOOGLE_SEARCH_URL
                    + "?q=pre�o de vinho "
                    + descricao.toString().trim()
                    + "&biw=1366&bih=662&tbm=shop&tbs=p_ord:rv&ei=mXPjWoS0E4KEwQT_tL_gBg&ved=0ahUKEwjE5JiVktvaAhUCQpAKHX_aD2wQuw0IrwIoAQ"
                    + "&num=" + num;
            new MyAsyncTask().execute(searchURL);
        }
    }

    // without proper User-Agent, we will get 403 error

    // popularListaVinho();

    // popularListView();

    // Capturar o click do ListView, Chamar a Tela Editar e Carregar os
    // dados para edi��o

    // ibtn01BuscaLojas = (ImageButton) findViewById(R.id.ibtn01BuscaLojas);

    /*
     * lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
     *
     * @Override public void onItemClick(AdapterView<?> parent, View view, int
     * position, long id) { // TODO Auto-generated method stub // Abrir o banco
     * de dados e capturar o registro posicionado no // click dado no ListView
     * //db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
     *
     * if (cursor.moveToPosition(position)) { Intent it = new
     * Intent(getBaseContext(), ListarLojas.class);
     *
     * // Capturar o c�digo do registro, antes de chamar a nova // tela
     * it.putExtra("vinhoSelecionado", cursor.getString(1)); // Limpando tela
     * principal // limpaListaVinho();
     *
     * // Iniciando tela de Altera��o de Produto startActivity(it); } }
     *
     * });
     */

    /*
     * public void dcmconvpng(File file, int indice, File fileOutput) throws
     * IOException, DicomException {
     *
     * ConsumerFormatImageMaker.convertFileToEightBitImage(file.toString(),
     * fileOutput.toString(), "png", indice);
     *
     * }
     */

    public File bitmapParaFile(Bitmap myBitmap) throws IOException {

        // tamanho original
        // myBitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
        // Log.i("ORIGINAL WIDTH->", myBitmap.getWidth() + "");
        // Log.i("ORIGINAL HEIGHT->", myBitmap.getHeight() + "");

        // tamanho alterado
        // Bitmap bmp = ResizedBitmap.getResizedBitmap(bitmap, 120, 120);
        // Log.i("CHANGE WIDTH->", bmp.getWidth() + "");
        // Log.i("CHANGE HEIGHT->", bmp.getHeight() + "");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */,
                bos);
        byte[] bitmapdata = bos.toByteArray();

        String nomeImg = "ocr.png";
        //String seuDiretorio = Environment.getExternalStorageDirectory()+ File.separator + "br.com.buscarvinhos";
        String seuDiretorio = DATA_PATH;

        File imgToSdcard = new File(seuDiretorio + File.separator + nomeImg);
        FileOutputStream outputStream = new FileOutputStream(imgToSdcard, false);
        outputStream.write(bitmapdata, 0, bitmapdata.length);
        outputStream.flush();
        outputStream.close();
        return imgToSdcard;
    }


 /*   public String leitura2(Bitmap bitmap) {
        System.load("/libs/liblept.so");
        System.load("/libs/libtess.so");
        TessOCR tess = new TessOCR();
        String leitura = tess.getOCRResult(bitmap);
        tess.onDestroy();
        return leitura.toString();
    }
 */
    public String leitura3(Bitmap bitmap) {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, LANG);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.println(Log.VERBOSE,TAG,recognizedText.toString());

        return recognizedText.toString();
    }

    public String leitura(Bitmap bitmap) throws IOException {
        // File imagemGaleria = Environment
        // .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // String caminho = imagemGaleria.getPath();
        /*
         * BufferedImage imagemGaleria = null; try { imagemGaleria =
         * ImageIO.read(new File(x.toString())); } catch (IOException e1) { //
         * TODO Auto-generated catch block e1.printStackTrace(); }
         */
        // File imageFile = new File(x.toString());
        File imageFile = bitmapParaFile(bitmap);
        Tesseract instance = Tesseract.getInstance();
        instance.setLanguage(LANG);
        String result = null;
        try {
            // dcmconvpng(imageFile, 0, imageFile);
            result = instance.doOCR(imageFile);
            // System.out.println(result);

        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    private void limpaListaVinho() {
        // TODO Auto-generated method stub
        cursor.close();
        lista.setAdapter(null);
        listaVinho.clear();
    }

    private void popularListaVinho() {
        // Criando o Banco de Dados ListaDeCompras
        // db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);

        // Criando a tabela de Produto
        StringBuilder sqlVinho = new StringBuilder();

        sqlVinho.append("CREATE TABLE IF NOT EXISTS [vinho_buscado] (");
        sqlVinho.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlVinho.append("descricao VARCHAR(256), ");
        sqlVinho.append("imagem VARCHAR(256)); ");
        db.execSQL(sqlVinho.toString());

        // Criar cursor para manipula��o dos dados
        cursor = db.rawQuery(
                "Select _id, descricao, imagem from vinho_buscado;", null);

        // limpaListaVinho();

        listaVinho.clear();

        if (cursor.moveToFirst()) {
            do {
                listaVinho.add(new VinhoBuscado(cursor.getInt(0), cursor
                        .getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        // db.close();
    }

    private void popularListView() {
        adapter = new MinhaLista();
        lista = (ListView) findViewById(R.id.lista01ConsultarFoto);
        lista.setAdapter(adapter);
    }

    public void inserirVinhoClick(View v) {
        Intent it = new Intent(getBaseContext(), Cadastro.class);
        // limpaListaVinho();
        startActivity(it);
    }

    public void carregarListaVinho(String termo, String imagem) {
        // Abrindo o banco de dados e coletando as informa��es digitadas
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        ContentValues cvt = new ContentValues();

        cvt.put("descricao", termo.toString());
        cvt.put("imagem", imagem);
        // cvt.put("valor", 0.0);

        /*
         * cvt.put("vinicula", ""); cvt.put("fornecedor", ""); cvt.put("ano",
         * 2018); cvt.put("volume", 750.0); //Inserindo a imagem do ImageView no
         * banco de dados SQLite cvt.put("foto", ""); cvt.put("foto_texto", "");
         * cvt.put("caminho_foto", "");
         */

        try {
            db.insert("vinho_buscado", "_id", cvt);
        } finally {
            // db.close();
        }
        // Validando a grava��o e retornando a mensagem de status
        /*
         * if (db.insert("vinho_buscado", "_id", cvt) > 0) {
         * Toast.makeText(getBaseContext(), "Cadastro Realizado com Sucesso!!!",
         * Toast.LENGTH_SHORT) .show(); // finish(); } else {
         * Toast.makeText(getBaseContext(), "Erro ao realizar Cadastro!!!",
         * Toast.LENGTH_SHORT).show(); }
         */
        // db.close();
    }

    // Deleting all contact
    public void apagaVinhosBuscados() {
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM vinho_buscado");
        db.close();
    }

    private class MinhaLista extends ArrayAdapter<VinhoBuscado> {

        public MinhaLista() {
            super(ConsultarFoto.this,
                    R.layout.listar_activity_layout_vinhos_foto, listaVinho);
        }

        @Override
        public int getCount() {
            return listaVinho.size();// retorna o tamanho da lista
        }

        @Override
        public VinhoBuscado getItem(int position) {
            return listaVinho.get(position);// retorna um item da lista
        }

        @Override
        public long getItemId(int position) {
            return position;// retorna a posi��o de um item
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View itemView = convertView;
            // MinhaViewHolder holder = null;
            if (itemView == null) {
                getBaseContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                itemView = getLayoutInflater().inflate(
                        R.layout.listar_activity_layout_vinhos_foto, parent,
                        false);
                holder = new MinhaViewHolder(itemView);
                itemView.setTag(holder);
            } else {
                holder = (MinhaViewHolder) itemView.getTag();
            }

            holder.tv_texto.setText(listaVinho.get(position)._descricao);
            // holder.bt_botao.setTag(listaVinho.get(position)._ivBuscarLojas);

            VinhoBuscado registroAtual = listaVinho.get(position);

            // Foto do produto
            ImageView imagemFoto = (ImageView) itemView
                    .findViewById(R.id.iv01ListarVinhosFotoFoto);
            Picasso.with(getBaseContext())
                    .load(listaVinho.get(position)._imagemVinho)
                    .into(imagemFoto);

            // C�digo de Barras
            txv01ListarLayoutVinhosFotoID = (TextView) itemView
                    .findViewById(R.id.txv01ListarLayoutVinhosFotoID);
            txv01ListarLayoutVinhosFotoID.setText(String.valueOf(registroAtual
                    .getID()));

            // Descri��o
            txv02ListarLayoutVinhosFotoNome = (TextView) itemView
                    .findViewById(R.id.txv02ListarLayoutVinhosFotoNome);

            if ((itemView != null) && (listaVinho != null)) {
                ibtn01BuscaLojas = (ImageButton) itemView
                        .findViewById(R.id.ibtn01BuscaLojasFoto);
                final VinhoBuscado vBuscado = getItem(position);
                ibtn01BuscaLojas.setTag(position); // registra tag
                ibtn01BuscaLojas.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getBaseContext(),
                                ListarLojas.class);
                        tvVinhoSelecionado = (TextView) findViewById(R.id.txv02ListarLayoutVinhosFotoNome);
                        it.putExtra("vinhoSelecionado",
                                vBuscado._descricao.toString());
                        // limpaListaVinho();
                        db.close();
                        startActivity(it);
                    }
                });
            }

            return itemView;
        }
    }

    /*
     * public void bntBuscarVinhosSelecionadoClick(View v) { Intent it = new
     * Intent(getBaseContext(), ListarLojas.class); tvVinhoSelecionado =
     * (TextView) findViewById(R.id.txv02ListarLayoutVinhosNome);
     *
     * // Capturar o c�digo do registro, antes de chamar a nova tela //
     * it.putExtra("vinhoSelecionado", //
     * txv02ListarLayoutVinhosNome.getText().toString());
     * it.putExtra("vinhoSelecionado", registroAtual._descricao); // Limpando
     * tela principal // limpaListaVinho();
     *
     * // Iniciando tela de Altera��o de Produto startActivity(it);
     *
     * // limpaListaVinho();
     *
     * // popularListView(); }
     */

    private class MyAsyncTask extends AsyncTask<String, Integer, Document> {

        @Override
        protected Document doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                doc = null;
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            Elements results = doc.select("h3.r > a");
            // Elements results2 = doc.select("div.MUQY0 img");

            // ArrayList<String> listaResultado = new ArrayList<String>();
            // vinhoB = new ArrayList<VinhoBuscado>();

            // apagaVinhosBuscados();
            // limpaListaVinho();

            // Nome do vinho
            String texto = "";
            //long contador = 0;
            // for (Element result : results) {
            if (results.isEmpty()){
                Log.println(Log.VERBOSE,TAG,descricao.toString());
            }else {
                for (int contador = 0; contador < results.size(); contador++) {
                    Element result = results.get(contador);
                    //Element results2 = doc.select(
                     //       "div.g div.pslires div.psliimg img").get(contador);
                    //String imagem = results2.attr("src");
                    texto = "" + result.text() + "\n";
                    // listaResultado.add(texto.toString());
                    // vinhoAtual.setDescricao(texto.toString());
                    carregarListaVinho(texto.toString(), null); //, imagem.toString());

                    // contador++;
                }

                // limpaListaVinho();
                // contador = 0;
                popularListaVinho();

                popularListView();
            }

        }
    }

    class MinhaViewHolder {
        TextView tv_texto;
        ImageView imagemVinho;
        ImageButton bt_botao;

        MinhaViewHolder(View v) {
            tv_texto = (TextView) v
                    .findViewById(R.id.txv02ListarLayoutVinhosFotoNome);
            bt_botao = (ImageButton) v.findViewById(R.id.ibtn01BuscaLojasFoto);
            imagemVinho = (ImageView) v
                    .findViewById(R.id.iv01ListarVinhosFotoFoto);
        }
    }



/*	public class TessOCR {
		private ITessAPI.TessBaseAPI mTess;

		public TessOCR() {
			// TODO Auto-generated constructor stub
			mTess = new ITessAPI.TessBaseAPI();
			String datapath = Environment.getExternalStorageDirectory()
					.getPath();
			// + "/tesseract/";
			String language = "eng";
			File dir = new File(datapath + "tessdata/eng.traineddata");
			if (!dir.exists())
				dir.mkdirs();
			mTess.init(datapath, language);
		}

		public String getOCRResult(Bitmap bitmap) {

			mTess.setImage(bitmap);
			String result = mTess.getUTF8Text();

			return result;
		}

		public void onDestroy() {
			if (mTess != null)
				mTess.end();
		}

	}*/

}

