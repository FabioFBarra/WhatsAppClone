package br.com.whatsappandroid.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Contato;

/**
 * Created by fabio on 20/11/2017.
 */

public class ContatoAdapter extends ArrayAdapter<Contato>{

    private ArrayList<Contato> contatos;
    private Context context;

    public ContatoAdapter(@NonNull Context context, @NonNull ArrayList<Contato> objects) {
        super(context, 0, objects);
        this.contatos = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        //verifica se lista está vazia

        if(contatos != null){
            //iniciar objetos para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //montar a view a partir do xml

            view = inflater.inflate(R.layout.lista_contato, parent, false);

            //recupera elemento para exibição

            TextView nomeContato = (TextView) view.findViewById(R.id.tv_nome);
            TextView emailContato = (TextView) view.findViewById(R.id.tv_email);

            Contato contato = contatos.get(position);
            nomeContato.setText(contato.getNome());
            emailContato.setText(contato.getEmail());

        }


        return view;

    }
}
