package br.edu.ifspsaocarlos.sdm.cuidador.data;

import java.util.ArrayList;

import br.edu.ifspsaocarlos.sdm.cuidador.R;
import br.edu.ifspsaocarlos.sdm.cuidador.entities.MenuItem;

/**
 * Lista de item de menu
 *
 * @author Anderson Canale Garcia
 */

public class MenuItemLista {

    public static ArrayList<MenuItem> getData() {

        ArrayList<MenuItem> data = new ArrayList<>();

        data.add(new MenuItem(R.string.menu_remedios, R.drawable.ic_alarm_add_black_48dp));
        data.add(new MenuItem(R.string.menu_contatos, R.drawable.ic_contacts_black_48dp));
        data.add(new MenuItem(R.string.menu_programas_favoritos, R.drawable.ic_live_tv_black_48dp));

        return data;
    }

}
