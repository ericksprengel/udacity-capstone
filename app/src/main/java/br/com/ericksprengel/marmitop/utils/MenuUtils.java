package br.com.ericksprengel.marmitop.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.ericksprengel.marmitop.data.MtopMenuItem;


public class MenuUtils {

    public static String getMenuOfTheDay() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //return "2018-01-29";
    }


    //TODO: it's just for tests
    private static void createMtopMenuItem(String name, String description, Map<String, MtopMenuItem.Option> options) {
        Date date = new Date(); // today :)
        DatabaseReference menuDatabaseReference = FirebaseDatabase.getInstance().getReference("menus")
                .child(new SimpleDateFormat("yyyy-MM-dd").format(date));
        MtopMenuItem menuItem = new MtopMenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setOptions(options);

        menuDatabaseReference.push().setValue(menuItem);
    }

    //TODO: it's just for tests
    public static void createMenuOfTheDay() {
        Map<String, MtopMenuItem.Option> options = new HashMap<>();
        options.put("01", new MtopMenuItem.Option("500g", 14.0));
        options.put("02", new MtopMenuItem.Option("850g", 19.0));

        createMtopMenuItem("Virado à Paulista", "Arroz, tutu, calabresa, ovo frito, couve e farofa", options);
        createMtopMenuItem("Filé de frango grelhado", "Arroz, feijão e creme de milho", options);
        createMtopMenuItem("Picadinho de Carne", "Arroz, feijão, batata com cenoura e farofa", options);
        createMtopMenuItem("Prato Vegetariano", "Arroz, feijão, strogonoff de grão de bico e batata palha", options);
        createMtopMenuItem("Prato Fitness", "*Carb:* Arroz integral ou Purê de batata doce \n" +
                "*Proteina:* Frango desfiado com ricota e milho \n" +
                "*Leguminoso:* Abobrinha com cenoura ralada", options);
    }
}
