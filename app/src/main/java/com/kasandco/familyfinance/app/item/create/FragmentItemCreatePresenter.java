package com.kasandco.familyfinance.app.item.create;

import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.app.item.ItemRepository;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class FragmentItemCreatePresenter extends BasePresenter<FragmentItemCreateContract> {
    @Inject
    public ItemDao itemDao;
    @Inject
    public ItemRepository repository;

    @Inject
    public FragmentItemCreatePresenter() {

    }

    @Override
    public void viewReady(FragmentItemCreateContract view) {
        this.view = view;
    }

    public void create(String name) {
        repository.create(getQuantity(name));
        view.showToast();
    }

    public void edit(String text, ItemModel itemEdit) {
        String[] arrayTextName = getQuantity(text);
        String name = arrayTextName[0];
        String quantity = "";
        if(arrayTextName.length==2) {
            quantity = arrayTextName[1];
        }
        if (!itemEdit.getName().equals(name) || !itemEdit.getQuantity().equals(quantity)) {
            ItemModel edited = new ItemModel();
            edited = itemEdit.clone();
            edited.setName(name);
            edited.setQuantity(quantity);
            edited.setDateMod(String.valueOf(System.currentTimeMillis()));
            repository.edit(edited);
            view.setEditedItemNull();
        }
    }


    private String[] getQuantity(String name) {
        if (name.contains(":")) {
            return name.split(":");
        } else {
            Pattern pattern = Pattern.compile("([a-zA-Zа-яА-Я]+)\\s(\\d+\\s?[a-zA-Zа-яА-Я]+)$");
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                if (matcher.groupCount() == 2) {
                    return new String[]{matcher.group(1).trim(), matcher.group(2).trim()};
                } else {
                    return new String[]{name, ""};
                }
            } else {
                return new String[]{name, ""};
            }
        }
    }
}
