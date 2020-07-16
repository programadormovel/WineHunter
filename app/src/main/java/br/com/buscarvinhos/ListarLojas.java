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

public class ListarLojas extends Activity {
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    public static String searchURL = null;
    ListView lista;
    ArrayAdapter<VinhoLoja> adapter;
    Cursor cursor;
    SQLiteDatabase db;
    EditText termo;
    TextView result;
    TextView tvPreco;
    TextView tvLoja;
    TextView tvLink;
    TextView tv01VinhoLojas;
    TextView tvLojaSelecionada;
    ImageButton ibtn01ComprarVinho;
    VinhoLoja vinhoAtual;
    // Buscador buscador = new Buscador();
    String descricao;
    private List<VinhoLoja> listaLoja = new ArrayList<VinhoLoja>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_activity_lojas);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        apagaLojasBuscadas();

        // Capturando o codigo que foi informado ao clicar na lista (Recuperou a
        // inst�ncia do objeto).
        Intent it = getIntent();
        descricao = it.getStringExtra("vinhoSelecionado");

        tv01VinhoLojas = (TextView) findViewById(R.id.tv01VinhoLojas);
        tv01VinhoLojas.setText(descricao.toString());

        // Buscador buscador = new Buscador();
        String mensagem = descricao.toString();
        vinhoAtual = new VinhoLoja();
        int num = 50;

        searchURL = GOOGLE_SEARCH_URL
                + "?q=Venda de "
                + mensagem.toString().trim()
                + "&rlz=1C1CHZL_pt-BRBR748BR748&source=lnms&tbm=shop&sa=X&ved=0ahUKEwjhvqbrrrraAhVJjZAKHTiQAB8Q_AUICygC&biw=1093&bih=490"
                + "&num=" + num;
        // without proper User-Agent, we will get 403 error
        new MyAsyncTask().execute(searchURL);

        popularListaLoja();

        popularListView();

        // Capturar o click do ListView, Chamar a Tela Editar e Carregar os
        // dados para edi��o
        /*
         * lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         *
         * @Override public void onItemClick(AdapterView<?> parent, View view,
         * int position, long id) { // TODO Auto-generated method stub // Abrir
         * o banco de dados e capturar o registro posicionado no // click dado
         * no ListView if (cursor.moveToPosition(position)) { Intent it = new
         * Intent(getBaseContext(), Consultar.class);
         *
         * // Capturar o c�digo do registro, antes de chamar a nova // tela
         * it.putExtra("descricao", cursor.getString(1)); // Limpando tela
         * principal // limpaListaVinho();
         *
         * // Iniciando tela de Altera��o de Produto startActivity(it); } }
         *
         * });
         */
    }

    private void limpaListaLoja() {
        // TODO Auto-generated method stub
        cursor.close();
        lista.setAdapter(null);
        listaLoja.clear();
    }

    private void popularListaLoja() {
        // Criando o Banco de Dados ListaDeCompras
        SQLiteDatabase db = openOrCreateDatabase("Motor.db",
                Context.MODE_PRIVATE, null);

        // Criando a tabela de Produto
        StringBuilder sqlLoja = new StringBuilder();

        sqlLoja.append("CREATE TABLE IF NOT EXISTS [vinho_loja] (");
        sqlLoja.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlLoja.append("descricao VARCHAR(256) , ");
        sqlLoja.append("loja VARCHAR(256) , ");
        sqlLoja.append("link VARCHAR(256) , ");
        sqlLoja.append("valor NUMBER(15,2) , ");
        sqlLoja.append("imagem VARCHAR(256)); ");
        db.execSQL(sqlLoja.toString());

        // Criar cursor para manipula��o dos dados
        cursor = db.rawQuery(
                "Select _id, descricao, loja, link, valor, imagem from vinho_loja;",
                null);

        if (cursor.moveToFirst()) {
            do {
                listaLoja.add(new VinhoLoja(cursor.getInt(0), cursor
                        .getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getDouble(4), cursor.getString(5)));
            } while (cursor.moveToNext());
        }

        // db.close();
    }

    private void popularListView() {
        adapter = new MinhaLista();
        lista = (ListView) findViewById(R.id.list01Lojas);
        lista.setAdapter(adapter);
    }

    public void inserirVinhoClick(View v) {
        Intent it = new Intent(getBaseContext(), Cadastro.class);
        // limpaListaLoja();
        startActivity(it);
    }

    public void carregarListaLoja(View v, String descricao, double valor,
                                  String loja, String link, String imagem) {
        // Abrindo o banco de dados e coletando as informa��es digitadas
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        ContentValues cvt = new ContentValues();

        cvt.put("descricao", descricao.toString());
        cvt.put("loja", loja.toString());
        cvt.put("link", link.toString());
        cvt.put("valor", valor);
        cvt.put("imagem", imagem);

        /*
         * cvt.put("ano", 2018); cvt.put("volume", 750.0); //Inserindo a imagem
         * do ImageView no banco de dados SQLite cvt.put("foto", "");
         * cvt.put("foto_texto", ""); cvt.put("caminho_foto", "");
         */

        try {
            db.insert("vinho_loja", "_id", cvt);
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
    public void apagaLojasBuscadas() {
        db = openOrCreateDatabase("Motor.db", Context.MODE_PRIVATE, null);
        db.execSQL("DELETE FROM vinho_loja");
        db.close();
        // cursor.close();
        listaLoja.clear();
    }

    private class MinhaLista extends ArrayAdapter<VinhoLoja> {

        public MinhaLista() {
            super(ListarLojas.this, R.layout.listar_activity_layout_lojas,
                    listaLoja);
        }

        @Override
        public int getCount() {
            return listaLoja.size();// retorna o tamanho da lista
        }

        @Override
        public VinhoLoja getItem(int position) {
            return listaLoja.get(position);// retorna um item da lista
        }

        @Override
        public long getItemId(int position) {
            return position;// retorna a posi��o de um item
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
            MinhaViewHolder holder = null;
            if (itemView == null) {
                LayoutInflater inflater = (LayoutInflater) getBaseContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = getLayoutInflater().inflate(
                        R.layout.listar_activity_layout_lojas, parent, false);
                holder = new MinhaViewHolder(itemView);
                itemView.setTag(holder);
            } else {
                holder = (MinhaViewHolder) itemView.getTag();
            }

            VinhoLoja registroAtual = listaLoja.get(position);

            holder.tv_texto.setText(listaLoja.get(position)._descricao);
            holder.tv_valor
                    .setText(String.valueOf(listaLoja.get(position)._valor));
            holder.tv_loja.setText(listaLoja.get(position)._loja);
            holder.tv_link.setText(listaLoja.get(position)._link);
            // holder.tv_texto.setTextColor(R.color.fundo3);
            holder.bt_botao.setTag(listaLoja.get(position).BtTitulo);

            // Foto do produto
            ImageView imagemFoto = (ImageView) itemView
                    .findViewById(R.id.iv01ListarLojasFoto);
            Picasso.with(getBaseContext())
                    .load(listaLoja.get(position)._imagem)
                    .into(imagemFoto);
            /*
             * if (registroAtual.get_foto() == null) {
             * imagemFoto.setImageBitmap(null); } else { byte[] foto =
             * registroAtual.get_foto(); Bitmap bmp = BitmapFactory
             * .decodeByteArray(foto, 0, foto.length);
             * imagemFoto.setImageBitmap(bmp); }
             */

            // ImageView imagemFotoTexto = (ImageView) itemView
            // .findViewById(R.id.iv02ListarFoto);

            /*
             * if(registroAtual.get_foto_texto()==null){
             * imagemFotoTexto.setImageBitmap(null); }else{ byte[] fotoTexto =
             * registroAtual.get_foto_texto().getBytes(); Bitmap foto2 =
             * BitmapFactory.decodeByteArray(fotoTexto, 0, fotoTexto.length);
             *
             * imagemFotoTexto.setImageBitmap(foto2); }
             */

            /*
             * // Caminho Foto TextView caminhoFoto = (TextView) itemView
             * .findViewById(R.id.txv01ListarCaminho);
             * caminhoFoto.setText(registroAtual.get_caminho_foto());
             */

            // C�digo de Barras
            TextView id = (TextView) itemView
                    .findViewById(R.id.txv01ListarLayoutLojasID);
            id.setText(String.valueOf(registroAtual.getID()));

            // Loja
            TextView descricao = (TextView) itemView
                    .findViewById(R.id.txv02ListarLayoutLojasLoja);
            // descricao.setText(registroAtual.getDescricao());

            // Link
            TextView link = (TextView) itemView
                    .findViewById(R.id.txv04ListarLayoutLink);
            link.setText(registroAtual.getLink());

            /*
             * // Fornecedor TextView fornecedor = (TextView) itemView
             * .findViewById(R.id.txv04ListarLayoutFornecedor);
             * fornecedor.setText(registroAtual.getFornecedor());
             *
             * // Ano TextView ano = (TextView) itemView
             * .findViewById(R.id.txv05ListarLayoutAno);
             * ano.setText(String.valueOf(registroAtual.getAno()));
             *
             * // Volume TextView volume = (TextView) itemView
             * .findViewById(R.id.txv06ListarLayoutVolume);
             * volume.setText(String.valueOf(registroAtual.getVolume()));
             */

            // Valor
            TextView valor = (TextView) itemView
                    .findViewById(R.id.txv03ListarLayoutLojasValor);
            valor.setText(String.valueOf(registroAtual.getValor()));

            /*
             * // Sexo TextView sexo = (TextView)
             * itemView.findViewById(R.id.txv05ListarLayoutSexo);
             * if(registroAtual.getSexo().toString().equals("M"))
             * sexo.setText("Masculino"); else sexo.setText("Feminino");
             */

            ibtn01ComprarVinho = (ImageButton) findViewById(R.id.ibtn01ComprarVinho);
            if ((itemView != null) && (listaLoja != null)) {
                ibtn01ComprarVinho = (ImageButton) itemView
                        .findViewById(R.id.ibtn01ComprarVinho);
                final VinhoLoja vLoja = getItem(position);
                ibtn01ComprarVinho.setTag(position); // registra tag
                ibtn01ComprarVinho.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getBaseContext(),
                                Consultar.class);
                        tvLojaSelecionada = (TextView) findViewById(R.id.txv02ListarLayoutLojasLoja);
                        it.putExtra("lojaSelecionada",
                                vLoja._descricao.toString());
                        it.putExtra("valorVinho", vLoja._valor);
                        it.putExtra("lojaVinho", vLoja._loja);
                        it.putExtra("linkVinho", vLoja._link);
                        it.putExtra("imagemVinho", vLoja._imagem);
                        // limpaListaLoja();
                        db.close();
                        startActivity(it);
                    }
                });
            }

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
            Elements results = doc.select("h3.r > a");
            //Elements results2 = doc.select("div.C4eCVc cite.UdQCqe");
            Elements results2 = doc.select("div.g div.pslires div.A8OWCb");
            Elements results3 = doc.select("div.g div.pslires div.A8OWCb div b");
            Elements results4 = doc.select("div.g div.pslires div.psliimg img");


            // ArrayList<String> listaResultado = new ArrayList<String>();

            // apagaLojasBuscadas();

            // Nome do vinho
            String texto = "";
            // int contador = 0;
            // for (Element result : results) {

            if (results.size() == results2.size()) {
                for (int contador = 0; contador < results.size(); contador++) {
                    Element result = results.get(contador);
                    // Link
                    String linkHref = result.attr("href");
                    // Nome do Vinho
                    texto = "" + result.text() + "\n";
                    // Loja
                    Element l = results2.get(contador);
                    String loja = l.text().toString().replace("&nbsp;", "").replace(",", ".").replace("<b>", "")
                            .replace("</b>", "").replace("R$", "").replace(".", ",").replaceAll("[0-9],[0-9]", "").substring(4);
                    // String m =
                    // result.select("div.C4eCVc cite.UdQCqe").text();
                    //String loja = l.text();
                    // Valor
                    Element x = results3.get(contador);
                    // String x =
                    // result.select("div.g div.pslires div.A8OWCb div b").text();
                    String valor = x.getElementsContainingText("$").toString();
                    // String valor = x.toString();

                    //Imagem
                    Element w = results4.get(contador);
                    String imagem = w.attr("src");
                    /*
                     * Element x =
                     * doc.select("div.g div.pslires div.A8OWCb div b").get
                     * (contador); String valor =
                     * x.getElementsContainingText("R$").text(); double preco =
                     * Double.parseDouble(valor.toString()
                     * .replace("R$","").replace(".", "").replace(",",
                     * ".").trim());
                     */
                    carregarListaLoja(
                            lista,
                            texto.toString(),
                            Double.parseDouble(valor.toString()
                                    .replace("&nbsp;", "").replace(",", ".")
                                    .replace("<b>", "").replace("</b>", "")
                                    .replace("R$", "")), loja.toString(),
                            linkHref.toString(), imagem.toString());
                    listaLoja.add(new VinhoLoja(contador, texto.toString(),
                            loja.toString(), linkHref.toString(), Double
                            .parseDouble(valor.toString()
                                    .replace("&nbsp;", "")
                                    .replace(",", ".")
                                    .replace("<b>", "")
                                    .replace("</b>", "")
                                    .replace("R$", "")), imagem.toString()));
                    // contador++;
                    // listaResultado.add(texto.toString());
                }
            }

            // tvLoja.setText(texto.toString());
            /*
             * for (Element result : results) { String linkHref =
             * result.attr("href"); String linkText = result.text(); texto = ""
             * + linkText + ";" + linkHref.substring(6, linkHref.indexOf("&"));
             * listaResultado.add(texto.toString()); }
             */

            // limpaListaVinho();
            // contador=0;
            popularListView();

        }
    }

    class MinhaViewHolder {
        TextView tv_texto;
        TextView tv_valor;
        TextView tv_loja;
        TextView tv_link;
        ImageView imagemVinho;
        ImageButton bt_botao;

        MinhaViewHolder(View v) {
            tv_texto = (TextView) v
                    .findViewById(R.id.txv02ListarLayoutLojasLoja);
            tv_valor = (TextView) v
                    .findViewById(R.id.txv03ListarLayoutLojasValor);
            tv_loja = (TextView) v
                    .findViewById(R.id.txv02ListarLayoutLojasLoja);
            tv_link = (TextView) v.findViewById(R.id.txv04ListarLayoutLink);
            imagemVinho = (ImageView) v.findViewById(R.id.iv01ListarLojasFoto);

            bt_botao = (ImageButton) v.findViewById(R.id.ibtn01ComprarVinho);
        }
    }
}
