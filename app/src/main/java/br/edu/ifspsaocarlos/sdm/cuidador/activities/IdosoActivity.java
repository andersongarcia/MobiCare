package br.edu.ifspsaocarlos.sdm.cuidador.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.adapters.MenuItemAdapter;
import br.edu.ifspsaocarlos.sdm.cuidador.data.MenuItemLista;
import br.edu.ifspsaocarlos.sdm.cuidador.fragments.IdosoFragment;

public class IdosoActivity extends AppCompatActivity {
    private RecyclerView drawerList;
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idoso);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (RecyclerView) findViewById(R.id.left_drawer);

        MenuItemAdapter itemAdapter = new MenuItemAdapter(this, MenuItemLista.getData(), listener);
        drawerList.setAdapter(itemAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                R.string.abrir_drawer,
                R.string.fechar_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                IdosoFragment.newInstance()).commit();
    }


    private View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = drawerList.getChildLayoutPosition(view);
            switch (MenuItemLista.getData().get(position).getTitulo()){
                case R.string.menu_remedios:
                    break;
                case R.string.menu_contatos:
                    break;
                case R.string.menu_programas_favoritos:
                    break;
            }
        }
    };

}
