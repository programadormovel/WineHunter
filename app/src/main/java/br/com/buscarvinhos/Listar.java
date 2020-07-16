package br.com.buscarvinhos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class Listar extends Activity {
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    public static String searchURL = null;
    ListView lista;
    ArrayAdapter<Termo> adapter;
    Cursor cursor;
    SQLiteDatabase db;
    EditText termo;
    TextView result;
    Termo vinhoAtual;
    private List<Termo> listaVinho = new ArrayList<Termo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_activity);
        lista = (ListView) findViewById(R.id.ltvListarActivity);
        lista.setAdapter(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        //apagaTermos();

        popularListaVinho();

        popularListView();

        // Capturar o click do ListView, Chamar a Tela Editar e Carregar os
        // dados para edi��o
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // Abrir o banco de dados e capturar o registro posicionado no
                // click dado no ListView
                if (cursor.moveToPosition(position)) {
                    Intent it = new Intent(getBaseContext(), ListarVinhos.class);

                    // Capturar o c�digo do registro, antes de chamar a nova
                    // tela
                    it.putExtra("termoPesquisado", cursor.getString(1));
                    // Limpando tela principal
                    // limpaListaVinho();
                    db.close();
                    // Iniciando tela de Altera��o de Produto
                    startActivity(it);
                }
            }

        });
    }

    private void limpaListaVinho() {
        // TODO Auto-generated method stub
        cursor.close();
        lista.setAdapter(null);
        listaVinho.clear();
    }

    // Deleting all contact
    public void apagaTermos() {
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM termo");
        db.close();
    }

    private void popularListaVinho() {
        // Criando o Banco de Dados ListaDeCompras
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);

        // Criando a tabela de Termos Pesquisados
        StringBuilder sqlTermo = new StringBuilder();

        sqlTermo.append("CREATE TABLE IF NOT EXISTS [termo] (");
        sqlTermo.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlTermo.append("descricao VARCHAR(256)); ");
        db.execSQL(sqlTermo.toString());

        // Criando a tabela de Produto
        StringBuilder sqlVinho = new StringBuilder();

        sqlVinho.append("CREATE TABLE IF NOT EXISTS [Vinho] (");
        sqlVinho.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlVinho.append("descricao VARCHAR(256) , ");
        sqlVinho.append("vinicula VARCHAR(256) , ");
        sqlVinho.append("fornecedor VARCHAR(256) , ");
        sqlVinho.append("ano INTEGER, ");
        sqlVinho.append("volume NUMBER(15,2), ");
        sqlVinho.append("valor NUMBER(15,2), ");
        sqlVinho.append("caminho_foto VARCHAR(256) NULL, ");
        sqlVinho.append("foto_texto VARCHAR(256) NULL, ");
        sqlVinho.append("foto BLOB NULL); ");
        db.execSQL(sqlVinho.toString());

        // Criar cursor para manipula��o dos dados
        cursor = db.rawQuery("Select _id, descricao from termo;", null);

        if (cursor.moveToFirst()) {
            do {
                listaVinho
                        .add(new Termo(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        db.close();
    }

    private void popularListView() {
        adapter = new MinhaLista();
        lista = (ListView) findViewById(R.id.ltvListarActivity);
        lista.setAdapter(adapter);
    }

    public void inserirVinhoClick(View v) {
        Intent it = new Intent(getBaseContext(), Cadastro.class);
        limpaListaVinho();
        startActivity(it);
    }

    public void carregarListaTermo(View v, String termo) {
        // Abrindo o banco de dados e coletando as informa��es digitadas
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        ContentValues cvt = new ContentValues();

        cvt.put("descricao", termo.toString());
        /*
         * cvt.put("vinicula", ""); cvt.put("fornecedor", ""); cvt.put("ano",
         * 2018); cvt.put("volume", 750.0); //Inserindo a imagem do ImageView no
         * banco de dados SQLite cvt.put("foto", ""); cvt.put("foto_texto", "");
         * cvt.put("caminho_foto", "");
         */

        // Validando a grava��o e retornando a mensagem de status
        if (db.insert("termo", "_id", cvt) > 0) {
            // Toast.makeText(getBaseContext(),
            // "Cadastro Realizado com Sucesso!!!", Toast.LENGTH_SHORT)
            // .show();
            // finish();
        } else {
            // Toast.makeText(getBaseContext(), "Erro ao realizar Cadastro!!!",
            // .LENGTH_SHORT).show();
        }
        db.close();
    }

    public void bntBuscarVinhosClick(View v) {
        termo = (EditText) findViewById(R.id.edt01BuscarVinho);
        // result = (TextView) findViewById(R.id.tvResultado);

        // Buscador buscador = new Buscador();
        String mensagem = termo.getText().toString();
        // vinhoAtual = new Termo();
        // int num = 5;

        /*
         * searchURL = GOOGLE_SEARCH_URL + "?q=venda de vinho " +
         * mensagem.toString().trim() //+
         * "&rlz=1C1CHZL_pt-BRBR748BR748&source=lnms&tbm=shop&sa=X&ved=0ahUKEwiPy5D7ksTaAhULh5AKHfSOAyAQ_AUICigB&biw=1093&bih=530"
         * +
         * "&rlz=1C1CHZL_pt-BRBR748BR748&source=lnms&tbm=shop&sa=X&ved=0ahUKEwjhvqbrrrraAhVJjZAKHTiQAB8Q_AUICygC&biw=1093&bih=490"
         * + "&num=" + num;
         */

        // without proper User-Agent, we will get 403 error
        // new MyAsyncTask().execute(searchURL);

        Intent it = new Intent(getBaseContext(), ListarVinhos.class);
        // Capturar o c�digo do registro, antes de chamar a nova
        // tela
        it.putExtra("termoPesquisado", mensagem.toString().toString().trim());
        // Limpando tela principal
        // limpaListaVinho();
        //
        carregarListaTermo(v, mensagem.toString());
        // Iniciando tela de Altera��o de Produto
        startActivity(it);

        limpaListaVinho();

        // popularListView();

    }

    private class MinhaLista extends ArrayAdapter<Termo> {
        public MinhaLista() {
            super(Listar.this, R.layout.listar_activity_layout, listaVinho);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(
                        R.layout.listar_activity_layout, parent, false);
            }

            Termo registroAtual = listaVinho.get(position);

            // Foto do produto
            ImageView imagemFoto = (ImageView) itemView
                    .findViewById(R.id.iv01ListarFoto);
            Picasso.with(getBaseContext())
                    .load("https://i0.wp.com/www.clubevinhosportugueses.pt/wp-content/uploads/2017/09/copo-vinho-tinto.jpg?ssl=1")
                    .into(imagemFoto);

            // C�digo de Barras
            TextView id = (TextView) itemView
                    .findViewById(R.id.txv01ListarLayoutID);
            id.setText(String.valueOf(registroAtual.getID()));

            // Descri��o
            TextView descricao = (TextView) itemView
                    .findViewById(R.id.txv02ListarLayoutNome);
            descricao.setText(registroAtual.getDescricao());

            return itemView;
        }
    }

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
            /*
             * Elements results = doc.select("h3.r > a");
             *
             * ArrayList<String> listaResultado = new ArrayList<String>();
             *
             * // Nome do vinho String texto = ""; for (Element result :
             * results) { texto = "" + result.text() + "\n";
             * listaResultado.add(texto.toString()); }
             */
            // tvNome.setText(texto.toString());
            /*
             * for (Element result : results) { String linkHref =
             * result.attr("href"); String linkText = result.text(); texto = ""
             * + linkText + ";" + linkHref.substring(6, linkHref.indexOf("&"));
             * listaResultado.add(texto.toString()); }
             */

            /*
             * StringBuilder resultado = new StringBuilder(); for(String item:
             * listaResultado){ System.out.println(item);
             * vinhoAtual.setDescricao(item); listaVinho.add(vinhoAtual);
             * //vinhoAtual.setTermos(vinhoAtual);
             *
             * resultado.append(vinhoAtual.getDescricao()); }
             */

            // result.setText(resultado.toString());

        }
    }
}
