package com.kasandco.shoplist.app.list;

import android.os.Handler;
import android.os.Looper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kasandco.shoplist.core.BaseRepository;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.core.icon.IconDao;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.app.item.ItemDao;
import com.kasandco.shoplist.app.item.ItemModel;
import com.kasandco.shoplist.network.ListNetworkInterface;
import com.kasandco.shoplist.network.Requests;
import com.kasandco.shoplist.network.model.FMSTokenModel;
import com.kasandco.shoplist.network.model.LastSyncApiDataModel;
import com.kasandco.shoplist.network.model.ListApiModel;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

@ListActivityScope
public class ListRepository extends BaseRepository {
    private ListDao listDao;
    private ItemDao itemDao;
    private ListRepositoryInterface callback;
    private ListNetworkInterface network;
    private ListSyncHistoryDao listSyncHistoryDao;

    private IconDao iconDao;

    private final CompositeDisposable disposable;


    @Inject
    public ListRepository(ListDao _listDao, IconDao _iconDao, ItemDao _itemDao, ListSyncHistoryDao _listSyncHistoryDao, Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect _connect) {
        super(sharedPreferenceUtil, _connect);
        listDao = _listDao;
        iconDao = _iconDao;
        itemDao = _itemDao;
        listSyncHistoryDao = _listSyncHistoryDao;
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
                Call<ListApiModel> call = network.createNewList(networkData);
                Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                    @Override
                    public void success(ListApiModel obj, Headers headers) {
                        if (networkData.equals(obj)) {
                            listModel.setServerId(obj.getId());
                            listModel.setDateMod(obj.getDateMod());
                            listModel.setDateModServer(obj.getDateMod());
                            new Thread(() -> listDao.update(listModel)).start();
                        }
                    }

                    @Override
                    public void error() {
                        callback.error();
                    }

                    @Override
                    public void noPermit() {
                        callback.noPermit();
                    }

                };
                Requests.request(call, callbackResponse);
            }).start();
        }
    }

    public void update(ListModel listModel) {
        if (isLogged) {
            networkUpdate(listModel);
        }
        new Thread(() -> listDao.update(listModel)).start();

    }

    private void networkUpdate(ListModel listModel) {
        if (isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> {
                ListApiModel networkData = new ListApiModel(listModel);
                networkData.setFinanceCategoryId(listModel.getServerFinanceCategoryId());
                Call<ListApiModel> call = network.updateList(networkData);
                Requests.RequestsInterface<ListApiModel> callbackResponse = new Requests.RequestsInterface<ListApiModel>() {
                    @Override
                    public void success(ListApiModel obj, Headers headers) {
                        if (networkData.equals(obj)) {
                            ListModel responseItem = new ListModel(obj);
                            new Thread(() -> listDao.update(responseItem)).start();
                        }
                    }

                    @Override
                    public void error() {
                        callback.noPermit();
                    }

                    @Override
                    public void noPermit() {

                    }
                };
                Requests.request(call, callbackResponse);
            }).start();
        }
    }

    public void getAll(ListRepositoryInterface callback) {
        this.callback = callback;
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            sync();
        } else if (isLogged && !isNetworkConnect.isInternetAvailable()) {
            callback.noConnectionToInternet();
            getAllItems();
        } else {
            getAllItems();
        }
    }

    private void getAllItems() {
        disposable.add(listDao.getAllActiveList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::setListItems, throwable -> callback.setListItems(null)));
    }

    private void sync() {

        sendFMSToken();

        new Thread(() -> {
            List<ListSyncHistoryModel> lastSyncItems = listSyncHistoryDao.getAll();
            List<LastSyncApiDataModel> lastSyncData = new ArrayList<>();
            for (ListSyncHistoryModel item : lastSyncItems) {
                lastSyncData.add(new LastSyncApiDataModel(item.getServerId(), item.getDateMod()));
            }

            Call<List<ListApiModel>> call = network.syncData(lastSyncData, deviceId);

            Requests.RequestsInterface<List<ListApiModel>> callbackResponse = new Requests.RequestsInterface<List<ListApiModel>>() {
                @Override
                public void success(List<ListApiModel> responseObj, Headers headers) {
                    if (headers.get("Is-Pro")!=null && Objects.equals(headers.get("Is-Pro"), "True")){
                        sharedPreference.setIsPro(true);
                    }
                    if(headers.get("Is-Pro")!=null && Objects.equals(headers.get("Is-Pro"), "False")){
                        sharedPreference.setIsPro(false);
                    }
                    new Thread(() -> listSyncHistoryDao.clear()).start();
                    if (responseObj != null && responseObj.size() > 0) {
                        for (ListApiModel item : responseObj) {
                            ListModel listModel = listDao.getListForServerId(item.getId());
                            new Thread(() -> {
                                if (listModel != null) {
                                    if (item.isDelete() || !item.isOwner() && item.isPrivate()) {
                                        new Thread(() -> {
                                            listDao.delete(item.getLocalId(), item.getId());
                                            listDao.deleteListItems(item.getLocalId(), item.getId());
                                        }).start();
                                    }
                                    ListModel listModify = new ListModel(item);
                                    listModify.setId(listModel.getId());
                                    listModify.setId(listDao.getId(listModify.getServerId()));
                                    listModify.setFinanceCategoryId(listModel.getFinanceCategoryId());
                                    listDao.update(listModify);
                                } else if (item.isPrivate() && item.isOwner() && !item.isDelete()) {
                                    listDao.insert(new ListModel(item));
                                } else if (!item.isPrivate() && !item.isDelete()) {
                                    listDao.insert(new ListModel(item));
                                }
                            }).start();
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

                @Override
                public void noPermit() {
                    getAllItems();
                    callback.noPermit();
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

    private void getIsPro() {
        String subscrToken = sharedPreference.getSharedPreferences().getString(Constants.SUBSCR_TOKEN, null);
        if (subscrToken !=null){
                new Thread(()->{
                    Call<ResponseBody> call = network.isPro(subscrToken);

                    Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                        @Override
                        public void success(ResponseBody responseObj, Headers headers) {
                        }

                        @Override
                        public void error() {
                            sharedPreference.getEditor().putBoolean(Constants.IS_PRO, false).commit();
                        }

                        @Override
                        public void noPermit() {
                            sharedPreference.getEditor().putBoolean(Constants.IS_PRO, false).commit();
                        }
                    };
                    Requests.request(call, callbackResponse);
                }).start();
        }
    }

    private void sendFMSToken() {
        String oldToken = sharedPreference.getSharedPreferences().getString(Constants.FMC_TOKEN, null);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    if(oldToken == null || !oldToken.equals(token)){
                        new Thread(()->{
                            FMSTokenModel model = new FMSTokenModel(token);
                            Call<ResponseBody> call = network.saveFMSToken(model);

                            Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                                @Override
                                public void success(ResponseBody responseObj, Headers headers) {
                                    sharedPreference.getEditor().putString(Constants.FMC_TOKEN, token).commit();
                                }

                                @Override
                                public void error() {
                                }

                                @Override
                                public void noPermit() {
                                }
                            };
                            Requests.request(call, callbackResponse);
                        }).start();
                    }
                });


    }

    public void removeList(ListModel listModel) {
        if (isLogged) {
            listModel.setIsDelete(1);
            new Thread(() -> listDao.update(listModel)).start();
            if (isNetworkConnect.isInternetAvailable()) {
                if (listModel.getServerId() != 0) {
                    long serverId = listModel.getServerId();
                    if (serverId > 0) {
                        Call<ResponseBody> call = network.removeList(serverId);
                        Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                            @Override
                            public void success(ResponseBody responseObj, Headers headers) {
                                new Thread(() -> listDao.delete(listModel)).start();
                            }

                            @Override
                            public void error() {
                                callback.error();
                            }

                            @Override
                            public void noPermit() {
                                callback.noPermit();
                            }
                        };
                        Requests.request(call, callbackResponse);
                    }
                } else {
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
                        public void success(ResponseBody responseObj, Headers headers) {
                            new Thread(() -> itemDao.deleteActiveItems(listId)).start();
                        }

                        @Override
                        public void error() {
                            callback.error();
                        }

                        @Override
                        public void noPermit() {
                            callback.noPermit();
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
                        public void success(ResponseBody responseObj, Headers headers) {
                            new Thread(() -> itemDao.deleteInActiveItems(listId)).start();
                        }

                        @Override
                        public void error() {
                            callback.error();
                        }

                        @Override
                        public void noPermit() {
                            callback.noPermit();
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

    public void setPrivate(ListModel listModel) {
        if (listModel.getIsPrivate() == 1) {
            listModel.setIsPrivate(0);
        } else {
            listModel.setIsPrivate(1);
        }
        listModel.setDateMod(String.valueOf(System.currentTimeMillis()));
        update(listModel);
    }

    public void saveFMCToken(String token) {
        sharedPreference.getEditor().putString(Constants.FMC_TOKEN, token).apply();
    }

    public void removeAllItems(ListModel listModel) {
        networkClearActiveListItems(listModel.getId());
        networkClearInactiveListItems(listModel.getId());
    }

    public void removeInactiveItems(ListModel listModel) {
        networkClearInactiveListItems(listModel.getId());
    }

    public boolean isLogged() {
        return sharedPreference.isLogged();
    }


    public interface ListRepositoryInterface {
        void setListItems(List<ListModel> listModel);

        void noConnectionToInternet();

        void getAllActiveListItems(List<ItemModel> items);

        void noPermit();

        void error();
    }

    public interface IconCallback {
        void setIcons(List<IconModel> iconModels);
    }

}
