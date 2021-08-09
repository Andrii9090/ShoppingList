package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenterInterface;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ListPresenter extends BasePresenter<ListActivity> {
    @Inject
    ListDao listDao;

    private ListContract view;
    private List<ListModel> listItems;

    @Inject
    public ListPresenter() {
        listItems = new ArrayList<>();
    }

    public void getListItems() {
        view.showLoading();
        class Async extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                listItems = listDao.getAllActiveList();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                view.addAdapter();
                view.hideLoading();
            }
        }
        Async async = new Async();
        async.execute();
    }

    private void showEmptyText() {
        if (listItems.size() > 0) {
            view.showEmptyText(false);
        } else {
            view.showEmptyText(true);
        }
    }

    @Override
    public void viewReady(ListActivity view) {
        this.view = (ListContract) view;
    }

    public void onContextMenuClick(int itemId, int position) {
        switch (itemId) {
            case R.id.context_menu_list_item_add_cost:
                break;
            case R.id.context_menu_list_item_clear_all:
                break;
            case R.id.context_menu_list_item_clear_byed:
                break;
            case R.id.context_menu_list_item_edit:
                view.editListItem(listItems.get(position));
                break;
            case R.id.context_menu_list_item_remove:
                removeList(position);
                break;
            case R.id.context_menu_list_item_share_list:
                break;
            default:
                view.showToast(R.string.text_error);
        }
    }

    private void removeList(int position) {
        class Async extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                listDao.delete(listItems.remove(position));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                view.removeItemUpdate(position);
                showEmptyText();
            }
        }
        Async async = new Async();
        async.execute();
    }

    public void create(String text, String iconPath) {
        if (validateListName(text)) {
            @SuppressLint("StaticFieldLeak")
            class AsyncUpd extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    long id = listDao.getLastId();
                    listItems.add(0, listDao.getList(id));
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    view.updateAdapter(0, false);
                }
            }
            AsyncUpd asyncUpd = new AsyncUpd();

            @SuppressLint("StaticFieldLeak")
            class Async extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    listDao.insert(new ListModel(text, String.valueOf(System.currentTimeMillis()), iconPath));
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    asyncUpd.execute();
                }
            }
            Async async = new Async();
            async.execute();

        } else {
            view.showToast(R.string.text_error_create_list);
        }
    }

    private boolean validateListName(String name) {
        if (name.isEmpty()) {
            return false;
        }
        if (name.length() < 2) {
            return false;
        }
        return true;
    }

    public List<ListModel> getItems() {
        return listItems;
    }

    public void edit(int position, String text, String iconPath) {
        if (validateListName(text)) {
                listItems.get(position).setName(text);
                    listItems.get(position).setIcon(iconPath);

                @SuppressLint("StaticFieldLeak")
                class Async extends AsyncTask<Void, Void, Void> {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        listDao.update(listItems.get(position));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        super.onPostExecute(unused);
                        view.updateAdapter(position, true);
                    }
                }
                Async async = new Async();
                async.execute();
        } else {
            view.showToast(R.string.text_error_create_list);
        }
    }
}
