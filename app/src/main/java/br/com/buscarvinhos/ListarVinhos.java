package br.com.buscarvinhos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

public class ListarVinhos extends Activity {
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    public static String searchURL = null;
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
    TextView txv01ListarLayoutVinhosID;
    TextView txv02ListarLayoutVinhosNome;
    MinhaViewHolder holder = null;
    VinhoBuscado vinhoAtual;

    // Buscador buscador = new Buscador();
    String descricao;
    private List<VinhoBuscado> listaVinho = new ArrayList<VinhoBuscado>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_activity_vinhos);
        lista = (ListView) findViewById(R.id.list01Vinhos);
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

        apagaVinhosBuscados();
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);

        // Capturando o codigo que foi informado ao clicar na lista (Recuperou a
        // inst�ncia do objeto).
        Intent it = getIntent();
        descricao = it.getStringExtra("termoPesquisado");

        tv01Vinhos = (TextView) findViewById(R.id.tv01Vinhos);
        tv01Vinhos.setText(descricao.toString());

        // Buscador buscador = new Buscador();
        String mensagem = descricao.toString();
        vinhoAtual = new VinhoBuscado();
        int num = 50;

        // op��o de pesquisa
        int op = 4;
        //String searchURL = "";

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

        // without proper User-Agent, we will get 403 error


        // popularListaVinho();

        // popularListView();

        // Capturar o click do ListView, Chamar a Tela Editar e Carregar os
        // dados para edi��o

        // ibtn01BuscaLojas = (ImageButton) findViewById(R.id.ibtn01BuscaLojas);

        /*
         * lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         *
         * @Override public void onItemClick(AdapterView<?> parent, View view,
         * int position, long id) { // TODO Auto-generated method stub // Abrir
         * o banco de dados e capturar o registro posicionado no // click dado
         * no ListView //db = openOrCreateDatabase("Motor.db",
         * Context.MODE_PRIVATE, null);
         *
         * if (cursor.moveToPosition(position)) { Intent it = new
         * Intent(getBaseContext(), ListarLojas.class);
         *
         * // Capturar o c�digo do registro, antes de chamar a nova // tela
         * it.putExtra("vinhoSelecionado", cursor.getString(1)); // Limpando
         * tela principal // limpaListaVinho();
         *
         * // Iniciando tela de Altera��o de Produto startActivity(it); } }
         *
         * });
         */
    }

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
        cursor = db.rawQuery("Select _id, descricao, imagem from vinho_buscado;", null);

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
        lista = (ListView) findViewById(R.id.list01Vinhos);
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
            super(ListarVinhos.this, R.layout.listar_activity_layout_vinhos,
                    listaVinho);
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
                LayoutInflater inflater = (LayoutInflater) getBaseContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = getLayoutInflater().inflate(
                        R.layout.listar_activity_layout_vinhos, parent, false);
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
                    .findViewById(R.id.iv01ListarVinhosFoto);
            Picasso.with(getBaseContext())
                    .load(listaVinho.get(position)._imagemVinho)
                    .into(imagemFoto);

            // C�digo de Barras
            txv01ListarLayoutVinhosID = (TextView) itemView
                    .findViewById(R.id.txv01ListarLayoutVinhosID);
            txv01ListarLayoutVinhosID.setText(String.valueOf(registroAtual
                    .getID()));

            // Descri��o
            txv02ListarLayoutVinhosNome = (TextView) itemView
                    .findViewById(R.id.txv02ListarLayoutVinhosNome);

            if ((itemView != null) && (listaVinho != null)) {
                ibtn01BuscaLojas = (ImageButton) itemView
                        .findViewById(R.id.ibtn01BuscaLojas);
                final VinhoBuscado vBuscado = getItem(position);
                ibtn01BuscaLojas.setTag(position); // registra tag
                ibtn01BuscaLojas.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getBaseContext(),
                                ListarLojas.class);
                        tvVinhoSelecionado = (TextView) findViewById(R.id.txv02ListarLayoutVinhosNome);
                        it.putExtra("vinhoSelecionado",
                                vBuscado._descricao.toString());
                        //limpaListaVinho();
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
            //Elements results2 = doc.select("div.MUQY0 img");


            // ArrayList<String> listaResultado = new ArrayList<String>();
            // vinhoB = new ArrayList<VinhoBuscado>();

            // apagaVinhosBuscados();
            // limpaListaVinho();

            // Nome do vinho
            String texto = "";
            //long contador = 0;
            //for (Element result : results) {
            for (int contador = 0; contador < results.size(); contador++) {
                Element result = results.get(contador);
                Element results2 = doc.select("div.g div.pslires div.psliimg img").get(contador);
                String imagem = results2.attr("src");
                texto = "" + result.text() + "\n";
                // listaResultado.add(texto.toString());
                // vinhoAtual.setDescricao(texto.toString());
                carregarListaVinho(texto.toString(), imagem.toString());

                //contador++;
            }

            // limpaListaVinho();
            //contador = 0;
            popularListaVinho();

            popularListView();

        }
    }

    class MinhaViewHolder {
        TextView tv_texto;
        ImageView imagemVinho;
        ImageButton bt_botao;

        MinhaViewHolder(View v) {
            tv_texto = (TextView) v
                    .findViewById(R.id.txv02ListarLayoutVinhosNome);
            bt_botao = (ImageButton) v.findViewById(R.id.ibtn01BuscaLojas);
            imagemVinho = (ImageView) v.findViewById(R.id.iv01ListarVinhosFoto);
        }
    }
}
