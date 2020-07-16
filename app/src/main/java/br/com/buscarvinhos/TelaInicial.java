package br.com.buscarvinhos;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TelaInicial extends Activity {
    private ProgressBar pb01TelaInicial;
    private TextView tv04ProgressTelaInicial;
    private int pStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        tv04ProgressTelaInicial = (TextView) findViewById(R.id.tv04ProgressTelaInicial);
        pb01TelaInicial = (ProgressBar) findViewById(R.id.pb01TelaInicial);

        pb01TelaInicial.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(pb01TelaInicial, "progress", 0, 100); // see this max value coming back here, we animate towards that value
        animation.setDuration(5000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();




       /* new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus <= 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pb01TelaInicial.setProgress(pStatus);
                            tv04ProgressTelaInicial.setText(pStatus + " %");
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus++;
                }
            }
        }).start();
*/
/*        try {
            Thread.sleep(1000);

            for (int i=0; i<4; i++) {
                publishProgress();
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/



        // Realizando a conex�o com o BD (Modo Privado - apenas a aplica��o ter�
        // acesso ao banco.)
        SQLiteDatabase db = openOrCreateDatabase("Motor.db",
                Context.MODE_PRIVATE, null);

        // Criando a tabela de Termos Pesquisados
        StringBuilder sqlTermo = new StringBuilder();

        sqlTermo.append("CREATE TABLE IF NOT EXISTS [termo] (");
        sqlTermo.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlTermo.append("descricao VARCHAR(256) UNIQUE); ");
        db.execSQL(sqlTermo.toString());

        // Criando a tabela de Produto
        StringBuilder sqlVinho = new StringBuilder();

        sqlVinho.append("CREATE TABLE IF NOT EXISTS [vinho_buscado] (");
        sqlVinho.append("[_id] INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlVinho.append("descricao VARCHAR(256), ");
        sqlVinho.append("imagem VARCHAR(256)); ");
        db.execSQL(sqlVinho.toString());

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

        // Criando a tabela Agenda para o banco de dados Agenda.db
        StringBuilder sqlAgenda = new StringBuilder();

        sqlAgenda.append("CREATE TABLE IF NOT EXISTS [vinho] (");
        sqlAgenda.append("[_id] INTEGER PRIMARY KEY, ");
        sqlAgenda.append("DESCRICAO VARCHAR(256) , ");
        sqlAgenda.append("VINICULA VARCHAR(256) , ");
        sqlAgenda.append("FORNECEDOR VARCHAR(256) , ");
        sqlAgenda.append("ANO INTEGER, ");
        sqlAgenda.append("VOLUME NUMBER(15,2), ");
        sqlAgenda.append("VALOR NUMBER(15,2), ");
        sqlAgenda.append("caminho_foto VARCHAR(256) NULL, ");
        sqlAgenda.append("foto_texto VARCHAR(256) NULL, ");
        sqlAgenda.append("foto BLOB NULL); ");
        db.execSQL(sqlAgenda.toString());

        db.close();

        // Carregamento de imagem com Picasso
        // ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        // imageView2.setImageBitmap(bm);
        // Picasso.with(getBaseContext())
        // .load("http://www.dicaslegais.net/wp-content/uploads/2010/09/imagens-lindas2.jpg")
        // .into(imageView2);

        //Intent it = new Intent(getBaseContext(), Listar.class);
        //startActivity(it);

        pb01TelaInicial.clearAnimation();
        animation.end();

    }

    public void TelaInicialCadastrarClick(View v) {
        Intent it = new Intent(getBaseContext(), Cadastro.class);
        startActivity(it);
    }

    public void TelaInicialListarClick(View v) {
        Intent it = new Intent(getBaseContext(), Listar.class);
        startActivity(it);
    }

    public void btnTelaInicialCapturarFotoClick(View v) {
        Intent it = new Intent(getBaseContext(), ConsultarFoto.class);
        //Intent it = new Intent(getBaseContext(), SimpleAndroidOCRActivity.class);
        //Intent it = new Intent(getBaseContext(), JavaCameraActivity.class);
        //Intent it = new Intent(getBaseContext(), ActivityCroper.class);
        startActivity(it);
    }

    // Criando m�todo para criar um menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Declarando objeto que manipula menus e Vinculando a Tela Inicial
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tela_inicial, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Capturando posi��o do item clicado
        int itemClicado = item.getItemId();

        switch (itemClicado) {
            // Verificar com o case cada um dos itens do menu
            case R.id.mnu01TelaInicialCadastrar:
                Intent irCadastro = new Intent(getBaseContext(), Cadastro.class);
                startActivity(irCadastro);
                break;

            case R.id.mnu02TelaInicialListar:
                Intent irListar = new Intent(getBaseContext(), Listar.class);
                startActivity(irListar);
                break;

            case R.id.mnu03TelaInicialSair:
                // Criando objeto Builder (Estilo Caixa de Mensagem)
                Builder msg = new Builder(this);
                msg.setMessage("Deseja realmente sair?");
                msg.setNegativeButton("N�o", null);
                msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
                // Necess�rio para exibir a caixa de mensagem criada acima (Builder)
                msg.show();
                break;

            case R.id.mnu05TelaInicialBaixarDados:
                RecebeJSP sinc = new RecebeJSP(this);
                sinc.execute(0);
                Log.v(null, "Click no Baixa Dados...");
                break;

            case R.id.mnu06TelaInicialEnviarDados:
                EnviaJSP env = new EnviaJSP(this);
                env.execute(0);
                Log.v(null, "Click no Envia Dados...");
                break;

            case R.id.mnu07TelaInicialDeletarDados:
                // Criando objeto Builder (Estilo Caixa de Mensagem)
                Builder msg2 = new Builder(this);
                msg2.setMessage("Deseja deletar todos os dados?");
                msg2.setNegativeButton("N�o", null);
                msg2.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            // Caso o bot�o sim seja clicado, finalizar o app
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                DatabaseHandler db = new DatabaseHandler(
                                        TelaInicial.this);
                                Log.d("Delete:", "Deleting...");
                                db.deleteAllVines();
                                Log.d("Count:", "...");

                                // Inserindo mensagem (Toast) de confirma��o/erro
                                if (db.getVinesCount() == 0) {
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Todos os registros foram deletados com sucesso!!!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(),
                                            "Erro ao deletar registros!!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                // Necess�rio para abrir a caixa de mensagem criada acima
                msg2.show();
                break;

            case R.id.mnu08TelaInicialBaixarDadosJSON:
                // Log.v(null, "Click no Baixa Dados via JSON...");

                //Intent it = new Intent(getBaseContext(), VinhoList.class);
                //startActivity(it);

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
