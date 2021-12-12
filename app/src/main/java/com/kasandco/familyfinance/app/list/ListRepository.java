package com.kasandco.familyfinance.app.list;

import android.os.Handler;
import android.os.Looper;


import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.item.ItemRepository;
import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.network.ListNetworkInterface;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.model.LastSyncApiDataModel;
import com.kasandco.familyfinance.network.model.ListApiModel;
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
import retrofit2.Retrofit;

public class ListRepository extends BaseRepository {
    private ListDao listDao;
    private ItemDao itemDao;
    private FinanceCategoryDao financeDao;
    private ListRepositoryInterface callback;
    private ListNetworkInterface network;
    private ListSyncHistoryDao listSyncHistoryDao;
    private ItemRepository itemRepository;

    private IconDao iconDao;

    private final CompositeDisposable disposable;


    @Inject
    public ListRepository(ListDao _listDao, IconDao _iconDao, ItemDao _itemDao, FinanceCategoryDao _financeDao, ListSyncHistoryDao _listSyncHistoryDao, Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil, IsNetworkConnect _connect, ItemRepository _itemRepository) {
        super(sharedPreferenceUtil, _connect);
        listDao = _listDao;
        iconDao = _iconDao;
        itemDao = _itemDao;
        financeDao = _financeDao;
        listSyncHistoryDao = _listSyncHistoryDao;
        itemRepository = _itemRepository;
        if (isLogged) {
            network = retrofit.create(ListNetworkInterface.class);
        }
        disposable = new CompositeDisposable();
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
            new Thread(() -> {
                ListApiModel networkData = new ListApiModel(listModel);
                if (listModel.getFinanceCategoryId() != 0) {
                    long categoryId = financeDao.getServerId(listModel.getFinanceCategoryId());
                    networkData.setFinanceCategoryId(categoryId);
                }
                Call<ListApiModel> call = network.createNewList(networkData);
                Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                    @Override
                    public void success(ListApiModel obj) {
                        if (networkData.equals(obj)) {
                            listModel.setServerId(obj.getId());
                            listModel.setDateMod(obj.getDateMod());
                            listModel.setDateModServer(obj.getDateMod());
                            new Thread(() -> listDao.update(listModel)).start();
                        }
                    }

                    @Override
                    public void error() {

                    }
                };
                Requests.request(call, callbackResponse);
            }).start();

        }
    }

    public void update(ListModel listModel) {
        new Thread(() -> listDao.update(listModel)).start();
        networkUpdate(listModel);
    }

    private void networkUpdate(ListModel listModel) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ListApiModel networkData = new ListApiModel(listModel);
            Call<ListApiModel> call = network.updateList(networkData);
            Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                @Override
                public void success(ListApiModel obj) {
                    if (networkData.equals(obj)) {
                        ListModel responseItem = new ListModel(obj);
                        new Thread(() -> listDao.update(responseItem)).start();
                    }
                }

                @Override
                public void error() {

                }
            };
            Requests.request(call, callbackResponse);
        }
    }

    public void getAll(ListRepositoryInterface callback) {
        this.callback = callback;
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            sync();
        }else if (isLogged && !isNetworkConnect.isInternetAvailable()){
            callback.noConnectionToInternet();
            getAllItems();
        }else {
            getAllItems();
        }
    }

    private void getAllItems() {
        disposable.add(listDao.getAllActiveList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::setListItems, throwable -> callback.setListItems(null)));
    }

    private void sync() {
        new Thread(() -> {
            List<ListSyncHistoryModel> lastSyncItems = listSyncHistoryDao.getAll();
            List<LastSyncApiDataModel> lastSyncData = new ArrayList<>();
            for (ListSyncHistoryModel item : lastSyncItems) {
                lastSyncData.add(new LastSyncApiDataModel(item.getServerId(), item.getDateMod()));
            }
            Call<List<ListApiModel>> call = network.syncData(lastSyncData, deviceId);

            Requests.RequestsInterface<List<ListApiModel>> callbackResponse = new Requests.RequestsInterface<List<ListApiModel>>() {
                @Override
                public void success(List<ListApiModel> responseObj) {
                    if (responseObj != null && responseObj.size() > 0) {
                        new Thread(() -> listSyncHistoryDao.clear()).start();
                        for (ListApiModel item : responseObj) {
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
                                        listModify.setFinanceCategoryId(listModel.getFinanceCategoryId());
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
                    getAllItems();
                }

                @Override
                public void error() {
                    getAllItems();
                }
            };
            Requests.request(call, callbackResponse);
        }).start();
        new Thread(() -> {
            List<ListModel> listModels = listDao.getAllList();
            for (ListModel item : listModels) {
                if (item.getIsDelete() == 1) {
                    removeList(item);
                    continue;
                }
                if (item.getServerId() == 0) {
                    networkCreate(item);
                } else if (item.getDateModServer() != null && Long.parseLong(item.getDateMod().substring(0, 10)) > Long.parseLong(item.getDateModServer().substring(0, 10))) {
                    networkUpdate(item);
                }
            }

        }).start();

    }

    public void removeList(ListModel listModel) {
        if (isLogged) {
            listModel.setIsDelete(1);
            new Thread(() -> listDao.update(listModel)).start();
            if (isNetworkConnect.isInternetAvailable()) {
                if (listModel.getServerId()!=0) {
                    long serverId = listModel.getServerId();
                    if (serverId > 0) {
                        Call<ResponseBody> call = network.removeList(serverId);
                        Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                            @Override
                            public void success(ResponseBody responseObj) {
                                new Thread(() -> listDao.delete(listModel)).start();
                            }

                            @Override
                            public void error() {

                            }
                        };
                        Requests.request(call, callbackResponse);
                    }
                }else {
                    new Thread(() -> listDao.delete(listModel)).start();
                }
            }
        } else {
            new Thread(() -> listDao.delete(listModel)).start();
        }
    }

    private void networkClearActiveListItems(long listId) {
        if (isLogged) {
            new Thread(() -> {
                itemDao.softDeleteActiveItems(listId);
                if (isNetworkConnect.isInternetAvailable()) {
                    long serverListId = listDao.getServerListId(listId);
                    Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                        @Override
                        public void success(ResponseBody responseObj) {
                            new Thread(() -> itemDao.deleteActiveItems(listId)).start();
                        }

                        @Override
                        public void error() {

                        }
                    };

                    Call<ResponseBody> call = network.clearListItems(serverListId, 1);
                    Requests.request(call, callbackResponse);
                }
            }).start();
        } else {
            new Thread(() -> itemDao.deleteActiveItems(listId)).start();
        }
    }

    private void networkClearInactiveListItems(long listId) {
        if (isLogged) {
            new Thread(() -> itemDao.softDeleteInActiveItems(listId)).start();
            if (isNetworkConnect.isInternetAvailable()) {
                new Thread(() -> {
                    long serverListId = listDao.getServerListId(listId);

                    Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                        @Override
                        public void success(ResponseBody responseObj) {
                            new Thread(() -> itemDao.deleteInActiveItems(listId)).start();
                        }

                        @Override
                        public void error() {

                        }
                    };

                    Call<ResponseBody> call = network.clearListItems(serverListId, 0);
                    Requests.request(call, callbackResponse);
                }).start();

            }
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
        Call<ListApiModel> call = network.subscribeToList(token);
        Handler handler = new Handler();
        Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
            @Override
            public void success(ListApiModel responseObj) {
                if (responseObj != null) {
                    ListModel listModel = new ListModel(responseObj);
                    new Thread(() -> listDao.insert(listModel)).start();
                    handler.post(callback::closeCreateForm);
                }
            }

            @Override
            public void error() {
                callback.noSubscribed();
            }
        };

        Requests.request(call, callbackResponse);
    }

    public void setPrivate(ListModel listModel) {
        if(listModel.getIsPrivate()==1){
            listModel.setIsPrivate(0);
        }else {
            listModel.setIsPrivate(1);
        }
        listModel.setDateMod(String.valueOf(System.currentTimeMillis()));
        update(listModel);
    }

    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);
        void noConnectionToInternet();
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
