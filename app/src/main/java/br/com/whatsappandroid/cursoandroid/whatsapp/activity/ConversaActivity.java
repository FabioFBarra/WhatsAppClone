package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private EditText editMensagem;
    private ImageButton btMensagem;

    private DatabaseReference databaseReference;

    //Dados do destinatatio

    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //Dados do remetente
    private String idUsuarioRemetent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tb_conversa);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);

        //Dados do usuario logado

        idUsuarioRemetent = new Preferencias(getApplicationContext()).getIdentificador();

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }


        //Configura toolbar

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //EnviarMensagem

        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoMensagem = editMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Digite uma mensagem para enviar", Toast.LENGTH_LONG).show();
                } else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetent);
                    mensagem.setMensagem(textoMensagem);

                    salvarMensagem(idUsuarioRemetent, idUsuarioDestinatario, mensagem);

                    editMensagem.setText("");
                }
            }
        });

    }

    private Boolean salvarMensagem(String idRemetente,String idDestinatario, Mensagem mensagem){
        try {
            databaseReference = ConfiguracaoFirebase.getFirebase().child("mensagens");


            //push cria novo nó com identificador unico
            databaseReference.child(idRemetente).child(idDestinatario).push().setValue(mensagem);

            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
