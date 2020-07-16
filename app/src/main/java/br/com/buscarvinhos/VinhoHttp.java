package br.com.buscarvinhos;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VinhoHttp {
    public final static String GARRAFAS_URL_JSON = "http://10.0.2.2:8082/ProjetoWebXML/listaContatosOraJSON.jsp";
    //public final static String CONTATOS_URL_JSON = "http://raw.10.0.2.2:8080/ProjetoWebXML/teste.json";

    // "http://10.0.2.2:8080/ProjetoWebXML/listaContatosOraJSON.jsp";

    private static HttpURLConnection connectar(String urlArquivo)
            throws IOException {
        final int SEGUNDOS = 1000;
        URL url = new URL(urlArquivo);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setReadTimeout(10 * SEGUNDOS);
        conexao.setConnectTimeout(15 * SEGUNDOS);
        conexao.setRequestMethod("GET");
        conexao.setDoInput(true);
        conexao.setDoOutput(false);
        conexao.connect();
        return conexao;
    }

    public static boolean temConexao(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String temConexaoString(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        String s = "Conex�o: ";
        if (wifi.isConnected()) {
            s += "Wi-fi";
        } else if (mobile.isConnected()) {
            s += "M�vel";
        } else {
            s += "Nenhuma";
        }
        return s;
    }

    public static List<Vinho> carregarGarrafasJson() {
        List<Vinho> x = null;
        try {
            HttpURLConnection conexao = connectar(GARRAFAS_URL_JSON);
            int resposta = conexao.getResponseCode();
            if (resposta == HttpURLConnection.HTTP_OK) {
                InputStream is = conexao.getInputStream();
                JSONObject json = new JSONObject(bytesParaString(is));
                x = lerJsonGarrafas(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return x;
    }

    @SuppressWarnings("null")
    public static List<Vinho> lerJsonGarrafas(JSONObject json)
            throws JSONException {
        List<Vinho> listaDeGarrafas = new ArrayList<Vinho>();
        JSONArray jsonVinho = json.getJSONArray("vinho");
        for (int i = 0; i < jsonVinho.length(); i++) {
            JSONObject jsonGarrafa = jsonVinho.getJSONObject(i);
            JSONArray jsonFoto = jsonGarrafa.getJSONArray("_foto");
            byte[] fotoJSON = null;
            for (int j = 0; j < jsonFoto.length(); j++) {
                fotoJSON[j] = Byte.parseByte(jsonFoto.getString(j));
            }
            Vinho garrafa = new Vinho(jsonGarrafa.getInt("_id"),
                    jsonGarrafa.getString("_descricao"),
                    jsonGarrafa.getString("_vinicula"),
                    jsonGarrafa.getString("_fornecedor"),
                    jsonGarrafa.getInt("_ano"),
                    jsonGarrafa.getDouble("_volume"),
                    jsonGarrafa.getDouble("_valor"),
                    jsonGarrafa.getString("_caminho_foto"),
                    jsonGarrafa.getString("_foto_texto"), fotoJSON);
            listaDeGarrafas.add(garrafa);
        }
        return listaDeGarrafas;
    }

    public static void insereGarrafasSQLite(Activity ctx, List<Vinho> garrafas) {
        try {
            DatabaseHandler db = new DatabaseHandler(ctx);
            for (Vinho garrafa : garrafas) {
                db.addVine(garrafa.getDescricao(), garrafa.getVinicula(),
                        garrafa.getFornecedor(), garrafa.getAno(),
                        garrafa.getVolume(), garrafa.getValor(),
                        garrafa.get_caminho_foto(), garrafa.get_foto_texto(),
                        garrafa.get_foto());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String bytesParaString(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        // O bufferzao vai armazenar todos os bytes lidos
        ByteArrayOutputStream bufferzao = new ByteArrayOutputStream();
        // precisamos saber quantos bytes foram lidos
        int bytesLidos;
        // Vamos lendo de 1KB por vez...
        while ((bytesLidos = is.read(buffer)) != -1) {
            // copiando a quantidade de bytes lidos do buffer para o bufferzão
            bufferzao.write(buffer, 0, bytesLidos);
        }
        return new String(bufferzao.toByteArray(), "UTF-8");
    }
}
