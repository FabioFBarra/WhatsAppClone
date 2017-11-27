package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.MensagemAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Conversa;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private EditText editMensagem;
    private ImageButton btMensagem;

    private DatabaseReference databaseReference;

    private ValueEventListener valueEventListenerMensagem;

    private ListView listView;

    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;

    //Dados do destinatatio

    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //Dados do remetente
    private String idUsuarioRemetent;
    private String nomeUsuarioRemetente;
    private String emailDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tb_conversa);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //Dados do usuario logado

        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetent = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }


        //Configura toolbar

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Monta ListView e adapter


        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter(adapter);

        //Recuperar mensagens do firebase

        databaseReference = ConfiguracaoFirebase.getFirebase().child("mensagens").child(idUsuarioRemetent).child(idUsuarioDestinatario);

        //Cria listener para mensagens

        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mensagens.clear();

                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListenerMensagem);

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

                    //saçva para remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetent, idUsuarioDestinatario, mensagem);
                    if(!retornoMensagemRemetente){
                        Toast.makeText(getApplicationContext(), "Problema ao salvar mensagem", Toast.LENGTH_LONG).show();
                    } else {
                        //saçva para destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetent, mensagem);
                        if(!retornoMensagemDestinatario){
                            Toast.makeText(getApplicationContext(), "Problema ao salvar mensagem", Toast.LENGTH_LONG).show();
                        }
                    }

                    editMensagem.setText("");
                }
                Conversa conversa = new Conversa();
                conversa.setIdUsuario(idUsuarioDestinatario);
                conversa.setNome(nomeUsuarioDestinatario);
                conversa.setMensagem(textoMensagem);
                Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetent, idUsuarioDestinatario, conversa);
                if(!retornoConversaRemetente){
                    Toast.makeText(getApplicationContext(), "Problema ao salvar sua conversa", Toast.LENGTH_LONG).show();
                } else {
                    conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioRemetent);
                    conversa.setNome(nomeUsuarioRemetente);
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetent, conversa);
                    if(!retornoConversaDestinatario){
                        Toast.makeText(getApplicationContext(), "Problema ao salvar conversa do Destinatario", Toast.LENGTH_LONG).show();

                    }
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

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{
            databaseReference = ConfiguracaoFirebase.getFirebase().child("conversas");
            databaseReference.child(idUsuarioRemetent).child(idUsuarioDestinatario).setValue(conversa);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerMensagem);
    }
}
