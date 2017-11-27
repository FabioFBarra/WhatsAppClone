package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Permissao;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextView linkCadastrar;
    private TextView senha;
    private TextView email;
    private Button botaologar;
    private Usuario usuario;
    private String identificadorUsuarioLogado;
    private FirebaseAuth autenticacao;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        verificarUsuarioLogado();

        abrirCadastroUsuario();

        email = (TextView) findViewById(R.id.editEmail);
        senha = (TextView) findViewById(R.id.editSenha);
        botaologar = (Button) findViewById(R.id.buttonLogar);

        botaologar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();
            }
        });


    }

    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()){
            Toast.makeText(LoginActivity.this, "Digite seu email e senha", Toast.LENGTH_LONG).show();
        } else {
            autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){


                        identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());


                        firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorUsuarioLogado);

                        valueEventListenerUsuario = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);

                                Preferencias preferencias = new Preferencias(LoginActivity.this);

                                preferencias.salvarUsuarioPreferencias(identificadorUsuarioLogado, usuarioRecuperado.getNome());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };

                        firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);




                        abrirTelaPrincipal();
                        Toast.makeText(LoginActivity.this, "Login efetuado", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Falha no login", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void abrirCadastroUsuario(){
        linkCadastrar = (TextView) findViewById(R.id.linkCadastrar);

        linkCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                startActivity(intent);
            }
        });
    }

}