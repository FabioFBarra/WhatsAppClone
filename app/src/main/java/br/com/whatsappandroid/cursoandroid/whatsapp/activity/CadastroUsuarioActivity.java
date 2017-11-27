package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome =           (EditText) findViewById(R.id.editNome);
        email =          (EditText) findViewById(R.id.editEmail);
        senha =          (EditText) findViewById(R.id.editSenha);
        botaoCadastrar = (Button) findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuario();
            }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        if (usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()){
            Toast.makeText(CadastroUsuarioActivity.this, "Preencha os dados", Toast.LENGTH_LONG).show();
        } else {

            autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CadastroUsuarioActivity.this, "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                        FirebaseUser firebaseUser = task.getResult().getUser();

                        String userID= Base64Custom.codificarBase64(usuario.getEmail());

                        usuario.setId(userID);
                        usuario.salvar();



                        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                        String identificadorUsuario = preferencias.getIdentificador();
                        preferencias.salvarUsuarioPreferencias(identificadorUsuario, preferencias.getNome());


                        abrirUsuarioLogado();
                    } else {

                        String erroExcessao = "";

                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            erroExcessao = "Digite uma senha mais forte, contendo mais caracteres e com letras e números";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erroExcessao = "O email digitado é inválido. Digite um novo email";
                        } catch (FirebaseAuthUserCollisionException e) {
                            erroExcessao = "Esse email já está em uso";
                        } catch (Exception e) {
                            erroExcessao = "falha ao efetuar o cadastro";
                            e.printStackTrace();
                        }

                        Toast.makeText(CadastroUsuarioActivity.this, "Erro: " + erroExcessao, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void abrirUsuarioLogado(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
