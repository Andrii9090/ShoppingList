package com.kasandco.familyfinance.app.list.CreateList;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.list.ListActivityScope;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.app.list.ListRepository;
import com.kasandco.familyfinance.core.BasePresenter;

import javax.inject.Inject;
@ListActivityScope
public class FragmentCreatePresenter extends BasePresenter<CreateListContract> {
    @Inject
    ListRepository listRepository;

    ListModel editItem;

    @Inject
    public FragmentCreatePresenter(){

    }

    @Override
    public void viewReady(CreateListContract view) {
        this.view = view;
    }

    public void create(String name, String iconPath, long statId) {
        if(validate(name)) {
            if(editItem==null) {
                ListModel listModel = new ListModel(name, String.valueOf(System.currentTimeMillis()), iconPath, statId);
                listRepository.create(listModel);
            }else {
                ListModel editListCopy = editItem.clone();
                editListCopy.setName(name);
                editListCopy.setIcon(iconPath);
                editListCopy.setDateMod(String.valueOf(System.currentTimeMillis()));
                listRepository.edit(editListCopy);
                editItem = null;
                view.setEditData(null);
            }
            view.close();
        }else {
            view.showToast(R.string.text_name_error);
        }
    }

    private boolean validate(String text){
        if(text!=null){
            return true;
        }
        if(text.length()>2){
            return true;
        }
        return false;
    }

    public void setEditItem(ListModel listModel) {
        this.editItem = listModel;
        view.setEditData(listModel);
    }
}
