package br.com.whatsappandroid.cursoandroid.whatsapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.adapter.ConversaAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.modelo.Conversa;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> conversaArrayAdapter;
    private ArrayList<Conversa> conversaArrayList;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        conversaArrayList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);


        conversaArrayAdapter = new ConversaAdapter(getActivity(), conversaArrayList);

        listView = view.findViewById(R.id.lv_conversas);

        listView.setAdapter(conversaArrayAdapter);

        Preferencias preferencias = new Preferencias(getContext());
        String idUsuarioLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase().child("conversas").child(idUsuarioLogado);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversaArrayList.clear();

                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversaArrayList.add(conversa);
                }

                conversaArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);
    }
}
