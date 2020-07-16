package br.com.buscarvinhos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/*class  OperacaoAlgumaCoisaWebServiceAsyncTask
           extends AsyncTask<Void, Void, Void> {
}
* Vendo a implementa��o desta classe percebemos que precisamos definir alguns tipos �Generic�. 
* Para o exemplo acima, colocamos todos como Void. Vamos entender para que serve cada tipo:
* Primeiro Generic(Params)  : o tipo que pode ser enviado para a thread  background desse mecanismo, ou seja, o m�todo doInBackground.
* Segundo  Generic(Progress): o tipo que pode ser usado para usar com finalidade de enviar o progresso de alguma execu��o da thread background.
* Terceiro Generic(Result)  : o tipo de retorno da thread background. */
public class RecebeJSP extends AsyncTask<Integer, Double, StringBuilder> {
    private Activity ctx;
    private ProgressDialog progress;

    public RecebeJSP(Activity ctx) {
        this.ctx = ctx;
    }

    //onPreExecute() � � executado antes do doInBackground().
    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(ctx, "Aguarde...", "Baixando dados da web!!!", true);
    }

    // doInBackground(Params�)� A thread background. � chamado quando o onPreExecute finaliza.
    @Override
    protected StringBuilder doInBackground(Integer... params) {

        StringBuilder sb = new StringBuilder();
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //String endereco = "http://10.0.0.10:8080/ProjetoWebFoto/listaContatosOra.jsp";
            //String endereco = "http://localhost:8080/ProjetoWeb/listaContatosOra.jsp";
            //String endereco = "http://localhost:8085/ProjetoWeb/listaContatosOra.jsp";
            //String endereco = "http://raw.10.0.2.2:8080/ProjetoWebXML/teste.json";
            StringBuilder endereco = new StringBuilder();
            endereco.append("http://10.0.2.2:8082/ProjetoWebXML/listaGarrafasOraJSON.jsp");
            Log.i("URL", endereco.toString());

            HttpGet httpget = new HttpGet(endereco.toString());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Scanner s = new Scanner(entity.getContent());
                while (s.hasNext()) {
                    sb.append(s.next());
                }
            }
            Log.i("Finalizou... ", "Fim");

        } catch (ClientProtocolException e) {
            sb.append(e.getMessage());
        } catch (IllegalStateException e) {
            sb.append(e.getMessage());
        } catch (IOException e) {
            sb.append(e.getMessage());
        }
        return sb;
    }

    //onPostExecute(Result)� Invocado depois que a thread principal finaliza e esta retorna algum valor como par�metro para este m�todo.
    @Override
    protected void onPostExecute(StringBuilder result) {
        progress.dismiss();
        Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

        //String[] matrizTela= new String[5];
        StringBuilder linhaDados = new StringBuilder();
        linhaDados.append(result);
        List<Vinho> garrafas = null;
        //JSONObject json = null;
        JSONObject json = null;
        try {
            json = new JSONObject(linhaDados.toString());

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            garrafas = VinhoHttp.lerJsonGarrafas(json);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //aqui ele pega a quantidade de carcteres que tem uma determinada vari�vel
        //e armazena numa INT para us�-la de contador
        int contador = result.length();
        String total = result.substring(result.indexOf("{") + 1, result.indexOf("}"));
        Log.i("Tot. Reg.:  ", total);
        Log.i("Tot. Car.:  ", String.valueOf(contador));
	    /*int PosIni = 0;
	    int PosFin = 0;
	    String Pos1, Pos2, Pos3, Pos4, Pos5;
		String Pos6, Pos7, Pos8;
	        */
        //cria um for( para fazer uma varredura letra por letra at� encontrar
        //for(int i = 0;i<contador;i++)
        for (Vinho garrafa : garrafas) {
            //usamos substring pra pegar um caractere, passando como par�metro,
            //o primeiro caractere a ser pega, at� a ultima.
            //fiz um if para verificar se o caractere � igual a "?"
            //if (result.substring(i,i+1).equals("?")){
            //	{
            //Log.i("i     :  ", String.valueOf(i));
            //Log.i("PosIni:  ", String.valueOf(PosIni));
            //Log.i("PosFin:  ", String.valueOf(PosFin=i));
            //LinhaDados = result.substring(PosIni,PosFin).trim();
            //LinhaDados = LinhaDados.replaceAll("[&]", " ");
            //Log.i("String:  ", LinhaDados);
            //PosIni = PosFin+1;
            /*Caso utilize a Classe Split.java que realiza a separa��o dos dados
             * Split separados;
             * separados = new Split();
             * matrizTela = separados.separaDados(LinhaDados, "|"); */
            //Utilizando a metodo Split nativo do Java
            //Exemplo nomeString.split(limit);
            //matrizTela = LinhaDados.split("[|]");
            //Retorna cada campo em uma posicao
            //Pos1 = matrizTela[0].toString();
            //Pos2 = matrizTela[1].toString();
            //Pos3 = matrizTela[2].toString();
            //Pos4 = matrizTela[3].toString();
            //Pos5 = matrizTela[4].toString();
            //Pos6 = matrizTela[5].toString();
            // Pos7 = matrizTela[6].toString();
            //Pos8 = matrizTela[7].toString();
            //Exibindo no console (Logcat)
            //Log.i("Pos1  :  ", Pos1);
            //Log.i("Pos2  :  ", Pos2);
            //Log.i("Pos3  :  ", Pos3);
            //Log.i("Pos4  :  ", Pos4);
            //Log.i("Pos5  :  ", Pos5);
            //Log.i("Pos6  :  ", Pos6);
            // Log.i("Pos7  :  ", Pos7);
            // Log.i("Pos8  :  ", Pos8);
            //Declarando a classe de controle de conexao
            DatabaseHandler db = new DatabaseHandler(ctx);
            // Inserting Contacts
            // Log.d("Insert: ", "Inserting ..");
			       /* if(Pos8.equals("")||Pos8.equals("null")){    
			        	try {
							db.addContact(Pos2, Pos3, Pos4, Pos5, Pos6, Pos7, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }else{*/
            //Bitmap x = null;
            //byte[] foto = null;
            //Recupera string de dados da foto e converte de String para byte
            //foto = Pos8.getBytes();
            //Converte array de byte para Bitmap
            //Bitmap bitmap = DbBitmapUtility.getImage(foto);
            //Realiza compress�o PNG
            //byte[] fotoPNG = DbBitmapUtility.getBytes(itmap);
            try {
                //db.addContact(Pos2, Pos3, Pos4, Pos5, Pos6, Pos7, foto);
                db.addVine(garrafa.getDescricao(), garrafa.getVinicula(),
                        garrafa.getFornecedor(), garrafa.getAno(),
                        garrafa.getVolume(), garrafa.getValor(),
                        garrafa.get_caminho_foto(), garrafa.get_foto_texto(),
                        garrafa.get_foto());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //}

        }
        //}
        //}
    }
}