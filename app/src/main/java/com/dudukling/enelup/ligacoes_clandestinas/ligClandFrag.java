package com.dudukling.enelup.ligacoes_clandestinas;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dudukling.enelup.R;
import com.dudukling.enelup.adapter.ligCland_recyclerAdapter;
import com.dudukling.enelup.dao.ligProvDAO;
import com.dudukling.enelup.model.ligClandModel;

import java.util.List;

public class ligClandFrag extends Fragment {
    private RecyclerView recyclerView;
    private ligCland_recyclerAdapter RecyclerAdapter;
    private List<ligClandModel> lpClandest;
    private View view;

//    private OnItemSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.lig_cland_frag_main, container,false);


        recyclerView = view.findViewById(R.id.recyclerViewClandest);
        registerForContextMenu(recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ligProvDAO dao = new ligProvDAO(getContext());
        lpClandest = dao.getClandestinoList();

//        RecyclerAdapter = new ligCland_recyclerAdapter(lpClandest, getContext(), listener);
        RecyclerAdapter = new ligCland_recyclerAdapter(lpClandest, getContext());
        recyclerView.setAdapter(RecyclerAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        TextView textViewNoClandestinos = view.findViewById(R.id.textViewNoClandestinos);
        if(lpClandest.size()>0){
            textViewNoClandestinos.setVisibility(View.GONE);
        }else{
            textViewNoClandestinos.setVisibility(View.VISIBLE);
        }

        dao.close();
    }








//    public interface OnItemSelectedListener {
//        void onItemSelected(ligClandModel clandest, int lpID, String type);
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnItemSelectedListener) {
//            listener = (OnItemSelectedListener) context;
//        } else {
//            throw new ClassCastException();
//        }
//    }

}
