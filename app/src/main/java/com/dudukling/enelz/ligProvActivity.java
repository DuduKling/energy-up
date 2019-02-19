package com.dudukling.enelz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dudukling.enelz.adapter.ligProv_recyclerAdapter;
import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.model.lpModel;

import java.util.List;

public class ligProvActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ligProv_recyclerAdapter RecyclerAdapter;
    private List<lpModel> lpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ligações Provisórias");
        setContentView(R.layout.activity_lig_prov);
        recyclerView = findViewById(R.id.collection_list);

        registerForContextMenu(recyclerView);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lig_prov, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Pesquisar...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRecords();
    }

    public void checkRecords() {
        lpDAO dao = new lpDAO(this);
        TextView textViewNoRecord = this.findViewById(R.id.textViewNoRecords);

        if (dao.lastID() == 0) {
            textViewNoRecord.setVisibility(View.VISIBLE);
        }else{
            textViewNoRecord.setVisibility(View.GONE);
            lpList = dao.getSamples();

            RecyclerAdapter = new ligProv_recyclerAdapter(lpList, this);
            recyclerView.setAdapter(RecyclerAdapter);

            RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layout);
        }
        dao.close();
    }
}
