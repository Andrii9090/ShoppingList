package com.kasandco.familyfinance.app.list;

import android.os.Handler;
import android.os.Looper;


import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.network.ListNetworkInterface;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListDataApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
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

    private IsNetworkConnect isNetworkConnect;

    @Inject
    public ListRepository(ListDao _listDao, IconDao _iconDao, ItemDao _itemDao, ListSyncHistoryDao _listSyncHistoryDao, Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil, IsNetworkConnect _connect) {
        listDao = _listDao;
        iconDao = _iconDao;
        itemDao = _itemDao;
        listSyncHistoryDao = _listSyncHistoryDao;
        network = retrofit.create(ListNetworkInterface.class);
        sharedPreference = sharedPreferenceUtil;
        disposable = new CompositeDisposable();
        isNetworkConnect = _connect;
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
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ListDataApiModel networkData = new ListDataApiModel(listModel);
            Call<ListDataApiModel> call = network.createNewList(networkData);
            call.enqueue(new Callback<ListDataApiModel>() {
                @Override
                public void onResponse(Call<ListDataApiModel> call, Response<ListDataApiModel> response) {
                    if (response.isSuccessful()) {
                        if (networkData.equals(response.body())) {
                            listModel.setServerId(response.body().getId());
                            listModel.setDateMod(response.body().getDateMod());
                            listModel.setDateModServer(response.body().getDateMod());
                            new Thread(() -> listDao.update(listModel)).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListDataApiModel> call, Throwable t) {

                }
            });
        }
    }

    public void update(ListModel listModel) {
        new Thread(() -> listDao.update(listModel)).start();
        networkUpdate(listModel);
    }

    private void networkUpdate(ListModel listModel) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ListDataApiModel networkData = new ListDataApiModel(listModel);
            Call<ListDataApiModel> call = network.updateList(networkData);
            call.enqueue(new Callback<ListDataApiModel>() {
                @Override
                public void onResponse(Call<ListDataApiModel> call, Response<ListDataApiModel> response) {
                    if (response.isSuccessful()) {
                        if (networkData.equals(response.body())) {
                            ListModel responseItem = new ListModel(response.body());
                            new Thread(() -> listDao.update(responseItem)).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ListDataApiModel> call, Throwable t) {

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
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
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
                } else if (Long.parseLong(item.getDateMod().substring(0, 10)) > Long.parseLong(item.getDateModServer().substring(0, 10))) {
                    networkUpdate(item);
                }
            }

            List<ListSyncHistoryModel> lastSyncItems = listSyncHistoryDao.getAll();
            List<LastSyncApiDataModel> lastSyncData = new ArrayList<>();
            for (ListSyncHistoryModel item : lastSyncItems) {
                lastSyncData.add(new LastSyncApiDataModel(item.getServerId(), item.getDateMod()));
            }

            Call<List<ListDataApiModel>> call = network.syncData(lastSyncData, deviceId);

            call.enqueue(new Callback<List<ListDataApiModel>>() {
                @Override
                public void onResponse(Call<List<ListDataApiModel>> call, Response<List<ListDataApiModel>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                        new Thread(() -> listSyncHistoryDao.clear()).start();
                        for (ListDataApiModel item : response.body()) {
                            if (item.isDelete()) {
                                new Thread(() -> {
                                    listDao.delete(item.getLocalId(), item.getId());
                                    listDao.deleteListItems(item.getLocalId(), item.getId());
                                });
                            } else {
                                ListModel listModel = listDao.getListForServerId(item.getId());
                                new Thread(() -> {
                                    if (listModel != null) {
                                        ListModel listModify = new ListModel(item);
                                        listModify.setId(listModel.getId());
                                        listModify.setId(listDao.getId(listModify.getServerId()));
                                        listDao.update(listModify);
                                    } else {
                                        listDao.insert(new ListModel(item));
                                    }
                                }).start();
                            }

                            ListSyncHistoryModel syncData = new ListSyncHistoryModel(item);
                            new Thread(() -> listSyncHistoryDao.insert(syncData)).start();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ListDataApiModel>> call, Throwable t) {

                }
            });
        }).

                start();
    }

    public void removeList(ListModel listModel) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
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
        if (listModel.getQuantityInactive() > 0) {
            new Thread(() -> listDao.clearInactiveItems(listModel.getId())).start();
            deleteInactiveListItems(listModel.getId());
        }
    }

    public void clearActiveItems(ListModel listModel) {
        if (listModel.getQuantityActive() > 0) {
            new Thread(() -> listDao.clearActiveItems(listModel.getId())).start();
            deleteActiveListItems(listModel.getId());
        }
    }

    private void deleteActiveListItems(long listId) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> {
                new Thread(() -> itemDao.softDeleteActiveItems(listId)).start();
                long serverListId = listDao.getServerListId(listId);
                Call<ResponseBody> call = network.clearListItems(serverListId, 1);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> itemDao.deleteActiveItems(listId)).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }).start();
        } else if (isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> itemDao.softDeleteActiveItems(listId)).start();
        } else {
            new Thread(() -> itemDao.deleteActiveItems(listId)).start();
        }
    }

    private void deleteInactiveListItems(long listId) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> {
                new Thread(() -> itemDao.softDeleteInActiveItems(listId)).start();
                long serverListId = listDao.getServerListId(listId);
                Call<ResponseBody> call = network.clearListItems(serverListId, 0);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> itemDao.deleteInActiveItems(listId)).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }).start();
        } else if (isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> itemDao.softDeleteInActiveItems(listId)).start();
        } else {
            new Thread(() -> itemDao.deleteInActiveItems(listId)).start();
        }
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
        Call<ListDataApiModel> call = network.subscribeToList(token);
        call.enqueue(new Callback<ListDataApiModel>() {
            @Override
            public void onResponse(Call<ListDataApiModel> call, Response<ListDataApiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ListModel listModel = new ListModel(response.body());
                    new Thread(() -> listDao.insert(listModel)).start();
                    callback.closeCreateForm();
                } else {
                    callback.noSubscribed();
                }
            }

            @Override
            public void onFailure(Call<ListDataApiModel> call, Throwable t) {
                callback.noSubscribed();
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

        void noSubscribed();
    }

    public interface IconCallback {
        void setIcons(List<IconModel> iconModels);
    }

}
