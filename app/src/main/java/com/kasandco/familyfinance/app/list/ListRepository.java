package com.kasandco.familyfinance.app.list;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;


import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.network.ListNetworkInterface;
import com.kasandco.familyfinance.network.model.LastSyncDataModel;
import com.kasandco.familyfinance.network.model.NetworkListData;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@ListActivityScope
public class ListRepository {
    private ListDao listDao;
    private ItemDao itemDao;
    private ListRepositoryInterface callback;
    private ListNetworkInterface network;
    private SharedPreferenceUtil sharedPreference;
    private boolean isLogged;
    private ListSyncHistoryDao listSyncHistoryDao;
    private String deviceId;

    private IconDao iconDao;

    private final CompositeDisposable disposable;

    @Inject
    public ListRepository(ListDao _listDao, IconDao _iconDao, ItemDao _itemDao, ListSyncHistoryDao _listSyncHistoryDao, Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil) {
        listDao = _listDao;
        iconDao = _iconDao;
        itemDao = _itemDao;
        listSyncHistoryDao = _listSyncHistoryDao;
        network = retrofit.create(ListNetworkInterface.class);
        sharedPreference = sharedPreferenceUtil;
        disposable = new CompositeDisposable();
    }

    public void setIsLogged(boolean _isLogged) {
        isLogged = _isLogged;
    }

    public void create(ListModel listModel) {
        new Thread(() -> {
            long id = listDao.insert(listModel);
            listModel.setId(id);
            networkCreate(listModel);
        }).start();
    }

    private void networkCreate(ListModel listModel) {
        if (isLogged) {
            NetworkListData networkData = new NetworkListData(listModel);
            Call<NetworkListData> call = network.createNewList(networkData);
            call.enqueue(new Callback<NetworkListData>() {
                @Override
                public void onResponse(Call<NetworkListData> call, Response<NetworkListData> response) {
                    if (response.isSuccessful()) {
                        if (networkData.equals(response.body())) {
                            listModel.setServerId(response.body().getId());
                            listModel.setDateMod(response.body().getDateMod());
                            new Thread(() -> listDao.update(listModel)).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NetworkListData> call, Throwable t) {

                }
            });
        }
    }

    public void update(ListModel listModel) {
        new Thread(() -> listDao.update(listModel)).start();
        networkUpdate(listModel);
    }

    private void networkUpdate(ListModel listModel) {
        if (isLogged) {
            NetworkListData networkData = new NetworkListData(listModel);
            Call<NetworkListData> call = network.updateList(networkData);
            call.enqueue(new Callback<NetworkListData>() {
                @Override
                public void onResponse(Call<NetworkListData> call, Response<NetworkListData> response) {
                    if (response.isSuccessful()) {
                        if (networkData.equals(response.body())) {
                            ListModel responseItem = new ListModel(response.body());
                            new Thread(() -> listDao.update(responseItem)).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NetworkListData> call, Throwable t) {

                }
            });
        }
    }

    public void getAll(ListRepositoryInterface callback) {
        this.callback = callback;
        disposable.add(listDao.getAllActiveList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> callback.setListItems(null))
                .subscribe(callback::setListItems));
        if (isLogged) {
            sync();
        }
    }

    private void sync() {
        new Thread(() -> {
            List<ListModel> listModels = listDao.getAllList();
            for (ListModel item : listModels) {
                if (item.getIsDelete() == 1) {
                    removeList(item);
                    continue;
                }
                if (item.getServerId() == 0) {
                    networkCreate(item);
                } else if (Long.parseLong(sharedPreference.getSharedPreferences().getString(Constants.LAST_SYNC_LIST, "0")) < Long.parseLong(item.getDateMod().substring(0, 10))) {
                    networkUpdate(item);
                }
            }

            List<ListSyncHistory> lastSyncItems = listSyncHistoryDao.getAll();
            List<LastSyncDataModel> lastSyncData = new ArrayList<>();
            for (ListSyncHistory item : lastSyncItems) {
                lastSyncData.add(new LastSyncDataModel(item.getServerId(), item.getDateMod()));
            }

            Call<List<NetworkListData>> call = network.syncData(lastSyncData, deviceId);

            call.enqueue(new Callback<List<NetworkListData>>() {
                @Override
                public void onResponse(Call<List<NetworkListData>> call, Response<List<NetworkListData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        new Thread(() -> listSyncHistoryDao.clear()).start();
                        String lastUpdate = "0";
                        for (NetworkListData item : response.body()) {
                            if (lastUpdate.isEmpty()) {
                                lastUpdate = item.getDateMod();
                            } else {
                                if (Long.parseLong(lastUpdate) < Long.parseLong(item.getDateMod())) {
                                    lastUpdate = item.getDateMod();
                                }
                            }
                            if (item.isDelete()) {
                                new Thread(() -> {
                                    listDao.delete(item.getLocalId(), item.getId());
                                    listDao.deleteListItems(item.getLocalId(), item.getId());
                                });
                            } else {
                                ListModel list = new ListModel(item);
                                new Thread(() -> {
                                    list.setId(listDao.getId(list.getServerId()));
                                    if (listDao.update(list) == 0) {
                                        listDao.insert(list);
                                    }
                                }).start();
                            }
                            ListSyncHistory syncData = new ListSyncHistory(item);
                            new Thread(() -> listSyncHistoryDao.insert(syncData)).start();
                        }
                        sharedPreference.getEditor().putString(Constants.LAST_SYNC_LIST, lastUpdate).apply();
                    }
                }

                @Override
                public void onFailure(Call<List<NetworkListData>> call, Throwable t) {

                }
            });
        }).start();
    }

    public void removeList(ListModel listModel) {
        if (isLogged) {
            listModel.setIsDelete(1);
            new Thread(() -> listDao.update(listModel)).start();
            long serverId = listModel.getServerId();
            if (serverId > 0) {
                Call<ResponseBody> call = network.removeList(serverId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> listDao.delete(listModel)).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        } else {
            new Thread(() -> listDao.delete(listModel)).start();
        }
    }

    public void clearInactiveItems(ListModel listModel) {
        new Thread(() -> listDao.clearInactiveItems(listModel.getId())).start();
        deleteInactiveListItems(listModel.getId());
    }

    public void clearActiveItems(ListModel listModel) {
        new Thread(() -> listDao.clearActiveItems(listModel.getId())).start();
        deleteActiveListItems(listModel.getId());
    }

    private void deleteActiveListItems(long listId) {
        new Thread(() -> listDao.deleteActiveListItem(listId)).start();
        //@TODO Сделать удаление listItem использую интерфейс айтемов
    }

    private void deleteInactiveListItems(long listId) {
        new Thread(() -> listDao.deleteInactiveListItem(listId)).start();
        //@TODO Сделать удаление listItem использую интерфейс айтемов
    }

    public void unsubscribe() {
        disposable.dispose();
    }

    public void getAllIcons(IconCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            List<IconModel> icons = iconDao.getAllIcon();
            handler.post(() -> {
                if (icons.size() > 0) {
                    callback.setIcons(icons);
                } else {
                    callback.setIcons(new ArrayList<>());
                }
            });
        }).start();
    }

    public void getAllListActiveItem(long listId, ListRepositoryInterface callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            List<ItemModel> items = itemDao.getActiveItems(listId);
            handler.post(() -> callback.getAllActiveListItems(items));
        }).start();
    }

    public void subscribeToList(String token, ListResponseListener callback) {
        Call<NetworkListData> call = network.subscribeToList(token);
        call.enqueue(new Callback<NetworkListData>() {
            @Override
            public void onResponse(Call<NetworkListData> call, Response<NetworkListData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListModel listModel = new ListModel(response.body());
                    new Thread(() -> listDao.insert(listModel)).start();
                    callback.closeCreateForm();
                }
            }

            @Override
            public void onFailure(Call<NetworkListData> call, Throwable t) {
                callback.closeCreateForm();
            }
        });
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);

        void getAllActiveListItems(List<ItemModel> items);
    }

    public interface ListResponseListener {
        void closeCreateForm();
    }

    public interface IconCallback {
        void setIcons(List<IconModel> iconModels);
    }

}
