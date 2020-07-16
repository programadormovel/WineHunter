package br.com.buscarvinhos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class EnviaJSP extends AsyncTask<Integer, Double, String> {
    int totalReplicado = 0;
    private Activity ctx;
    private ProgressDialog progress;

    public EnviaJSP(Activity ctx) {
        this.ctx = ctx;
    }

    //onPreExecute() � � executado antes do doInBackground().
    @Override
    protected void onPreExecute() {
        Log.i("onPreExecute...  ", " ");
        progress = ProgressDialog.show(ctx, "Aguarde...", "Enviando dados para web!!!", true);
        Log.i("onPreExecute...  ", "..OK..");
    }

    // doInBackground(Params�)� A thread background. � chamado quando o onPreExecute finaliza.
    @Override
    protected String doInBackground(Integer... params) {
        Log.i("doInBackground...  ", " Abrindo BD ");
        //Abrir/Criar o Banco de Dados
        //Declarando a classe de controle de conexao
        DatabaseHandler db = new DatabaseHandler(ctx);
        // Inserting Contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Vinho> garrafas = db.getAllVines();

        for (Vinho cn : garrafas) {
            //String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            //String.valueOf(contador)
            Log.d("ID:    ", String.valueOf(cn.getID()));
            Log.d("Descri��o:  ", cn.getDescricao());
            Log.d("Vin�cula: ", cn.getVinicula());
            Log.d("Fornecedor:  ", cn.getFornecedor());
            Log.d("Ano:  ", String.valueOf(cn.getAno()));
            Log.d("Volume:  ", String.valueOf(cn.getVolume()));
            Log.d("Valor:  ", String.valueOf(cn.getValor()));
            Log.d("Caminho_Foto:  ", cn.get_caminho_foto());
            Log.d("Foto_Texto:  ", cn.get_foto_texto());
            //Log.d("Foto:  ", String.valueOf(cn.get_foto()));
            //byte[] fotoBytes = cn.get_foto();

            //Log.d("Foto em bytes: ", String.valueOf(fotoBytes.toString()));
            ByteArrayInputStream is = new ByteArrayInputStream(cn.get_foto());
            String fotoConvertidaParaString = null;
            try {
                fotoConvertidaParaString = Utils.bytesParaString(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d("Foto em texto: ", fotoConvertidaParaString);
        	/*String vetorFoto[] = null;
        	
        	if(cn.get_foto().length>0){
        		for(int cont = 0; cont < fotoBytes.length-1; cont++){
            		char x = (char) fotoBytes[cont];
            		vetorFoto[cont] = String.valueOf(x);
            	}
        	}*/
            //Bitmap bmp = DbBitmapUtility.decodeBase64(cn.get_foto_texto());
            //byte[] vetorFoto = DbBitmapUtility.getBytes(bmp);
            //Log.d("Foto em texto:  ", vetorFoto.toString());

            //Compressor comp = new Compressor();
            //String fotoDescompressada = fotoBytes.toString();
            //byte[] fotoDescompressada = null;
            //try {
            //	fotoDescompressada = Compressor.compress1(fotoBytes);
            //} catch (IOException e) {
            //	// TODO Auto-generated catch block
            //	e.printStackTrace();
            //}
            //byte[] fotoDescompressada = Base64.decode(cn.get_foto_texto(), Base64.DEFAULT);
            byte[] fotoDescompressada = cn.get_foto();

			/*try {
				String fotoDescompressadaBytes = DbBitmapUtility.compressAndBase64(fotoBytes);
				fotoCompressada = Compressor.compress(fotoDescompressadaBytes.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
            Log.d("Foto em bytes:  ", fotoDescompressada.toString());

            StringBuilder strURL = new StringBuilder();
            strURL.append("http://10.0.2.2:8082/ProjetoWebXML/insereGarrafasOra.jsp?nome=");
            //strURL.append("http://10.0.0.10:8080/ProjetoWebFoto/insereContatosOra.jsp?nome=");
            //strURL.append("http://localhost:8085/ProjetoWeb/insereContatosOra.jsp?nome=");
            //strURL.append("http://192.168.0.101:8080/ProjetoWeb/insereContatosOra.jsp?nome=");
            //strURL.append(cn.getNome());
            //Retirar Espacos do Nome
            String nomeConv = cn.getDescricao();
            while (nomeConv.indexOf(' ') != -1) {
                System.out.println("Descri��o: " + nomeConv);
                nomeConv = nomeConv.substring(0, nomeConv.indexOf(' ')) + "%20" +
                        nomeConv.substring(nomeConv.indexOf(' ') + 1);
            }
            System.out.println("Vin�cula: " + nomeConv);
            //strURL.append(nomeConv);
            strURL.append(cn.getVinicula());
            strURL.append("&Fornecedor=");
            strURL.append(cn.getFornecedor());
            strURL.append("&Ano=");
            strURL.append(cn.getAno());
            strURL.append("&Volume=");
            strURL.append(cn.getVolume());
            strURL.append("&Valor=");
            strURL.append(cn.getValor());
            strURL.append("&caminho_foto=");
            strURL.append(cn.get_caminho_foto());
            strURL.append("&foto_texto=");
            //strURL.append(fotoConvertidaParaString);
            strURL.append(fotoDescompressada.toString());
            strURL.append("&foto=");
            //strURL.append(cn.get_foto());
            strURL.append(fotoDescompressada);
            Log.d("enviaJSP... ", strURL.toString());

            try {
                URL url = new URL(strURL.toString());
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStreamReader ips = new InputStreamReader(http.getInputStream());
                BufferedReader line = new BufferedReader(ips);

                String linhaRetorno = line.readLine();
                totalReplicado++;
                Log.d("Verificando Exportacao ", linhaRetorno);
                if (linhaRetorno.equals("Y")) {
                    Log.i("linhaRetorno.equals(Y)", " OK ");
                    db.deleteVine(cn.getID());
                    totalReplicado++;
                    Log.d("ExportarVinhoService...", linhaRetorno);
                } else {
                    Log.i("linhaRetorno.equals(N)", " Fail ");
                }
            } catch (Exception ex) {
                Log.d("ExportarVinhoService", ex.getMessage());
            }
        }
        return null;
    }

    //onPostExecute(Result)� Invocado depois que a thread principal finaliza e esta retorna algum valor como par�metro para este m�todo.
    @Override
    protected void onPostExecute(String result) {
        progress.dismiss();
        Toast.makeText(ctx, totalReplicado + " Registro(s) Enviado(s) com sucesso!", Toast.LENGTH_LONG).show();
    }
}