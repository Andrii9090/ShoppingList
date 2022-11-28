package com.kasandco.shoplist.app.item.create;

import com.kasandco.shoplist.app.item.ItemModel;
import com.kasandco.shoplist.app.item.ItemRepository;
import com.kasandco.shoplist.core.BasePresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class ItemCreatePresenter extends BasePresenter<FragmentItemCreateContract> {
    @Inject
    public ItemRepository repository;

    @Inject
    public ItemCreatePresenter() {

    }

    @Override
    public void viewReady(FragmentItemCreateContract view) {
        this.view = view;
    }

    @Override
    public void swipeRefresh() {

    }

    public void create(String name, long listId, long serverListId) {
        if (!name.isEmpty()) {
            repository.create(name, listId, serverListId);
            try {
                Thread.sleep(250);
                repository.sync(listId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        view.showToast();
    }

    public void edit(String text, ItemModel itemEdit) {
        String[] arrayTextName = getQuantity(text);
        String name = arrayTextName[0];
        if (!itemEdit.getName().equals(name)) {
            itemEdit.setName(name);
            itemEdit.setDateMod(String.valueOf(System.currentTimeMillis()));
            repository.update(itemEdit);
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
