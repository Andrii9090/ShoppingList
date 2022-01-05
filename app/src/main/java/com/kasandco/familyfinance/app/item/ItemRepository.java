package com.kasandco.familyfinance.app.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.kasandco.familyfinance.core.BaseRepository;
import com.kasandco.familyfinance.network.ItemNetworkInterface;
import com.kasandco.familyfinance.network.Requests;
import com.kasandco.familyfinance.network.model.ImageItemApiModel;
import com.kasandco.familyfinance.network.model.ItemApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SaveImageUtils;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;

@ItemActivityScope
public class ItemRepository extends BaseRepository {
    private ItemDao itemDao;
    private long listId;
    private long serverListId;
    private CompositeDisposable disposable;
    private ItemNetworkInterface network;
    private ItemSyncHistoryDao itemSyncDao;
    private SaveImageUtils saveImageUtils;
    private ItemRepositoryCallback callback;

    @Inject
    public ItemRepository(ItemNetworkInterface _networkInterface, ItemDao _itemDao, SaveImageUtils _saveImage, SharedPreferenceUtil _sharedPreference, IsNetworkConnect networkConnect, ItemSyncHistoryDao _itemSyncDao) {
        super(_sharedPreference, networkConnect);
        network = _networkInterface;
        itemDao = _itemDao;
        itemSyncDao = _itemSyncDao;
        saveImageUtils = _saveImage;
        disposable = new CompositeDisposable();
    }

    public void create(String name, long listId, long serverListId) {
        new Thread(() -> {
            long itemId = itemDao.insert(new ItemModel(name, listId, serverListId));
            ItemModel item = itemDao.getItem(itemId);
            networkCreate(item);
        }).start();
    }

    private void networkCreate(ItemModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ItemApiModel body = new ItemApiModel(item);
            Requests.RequestsInterface<ItemApiModel> callbackResponse = new Requests.RequestsInterface<ItemApiModel>() {
                @Override
                public void success(ItemApiModel responseObj) {
                    ItemModel itemResp = new ItemModel(responseObj);
                    itemResp.setLocalListId(item.getLocalListId());
                    new Thread(() -> itemDao.update(itemResp)).start();
                }

                @Override
                public void error() {

                }

                @Override
                public void noPermit() {
                    if(callback!=null) {
                        callback.noPerm();
                    }
                }
            };

            Call<ItemApiModel> call = network.create(body);
            Requests.request(call, callbackResponse);
        }
    }

    public void remove(ItemModel item) {
        if (isLogged) {
            networkRemove(item);
        } else {
            new Thread(() -> {
                itemDao.delete(item);
            }).start();
        }

    }

    private void networkRemove(ItemModel item) {
        item.setIsDelete(1);
        if (!isNetworkConnect.isInternetAvailable()) {
            if (item.getIsDelete() != 1) {
                new Thread(() -> {
                    itemDao.update(item);
                }).start();
            }
        } else {
            Requests.RequestsInterface<ResponseBody> callbackResponse = new Requests.RequestsInterface<ResponseBody>() {
                @Override
                public void success(ResponseBody responseObj) {
                    new Thread(() -> itemDao.delete(item)).start();
                }

                @Override
                public void error() {
                    new Thread(() -> itemDao.update(item)).start();
                }

                @Override
                public void noPermit() {
                    if(callback!=null) {
                        callback.noPerm();
                    }
                }
            };

            Call<ResponseBody> call = network.remove(item.getServerId());
            Requests.request(call, callbackResponse);
        }
    }

    public void update(ItemModel item) {
        new Thread(() -> itemDao.update(item)).start();
        networkUpdate(item);
    }

    private void networkUpdate(ItemModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ItemApiModel apiModel = new ItemApiModel(item);

            Requests.RequestsInterface<ItemApiModel> callbackResponse = new Requests.RequestsInterface<ItemApiModel>() {
                @Override
                public void success(ItemApiModel responseObj) {
                    new Thread(() -> {
                        ItemModel itemConverted = new ItemModel(responseObj);
                        itemDao.update(itemConverted);
                    }).start();
                }

                @Override
                public void error() {

                }

                @Override
                public void noPermit() {
                    if(callback!=null) {
                        callback.noPerm();
                    }
                }
            };

            Call<ItemApiModel> call = network.update(apiModel);

            Requests.request(call, callbackResponse);
        }
    }

    public void getAll(long listId, long serverListId, ItemRepositoryCallback callback) {
        this.listId = listId;
        this.serverListId = serverListId;
        this.callback = callback;
        disposable.add(itemDao.getAllItems(listId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::setItems, error -> Log.e("Error", error.toString())));
        sync(listId);
    }

    public void sync(long listId) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            new Thread(()->{
                List<ItemSyncHistoryModel> syncHistory = itemSyncDao.getAll(serverListId);

                Requests.RequestsInterface<List<ItemApiModel>> callbackResponse = new Requests.RequestsInterface<List<ItemApiModel>>() {
                    @Override
                    public void success(List<ItemApiModel> responseObj) {
                        new Thread(() -> itemSyncDao.clearAll(serverListId)).start();
                        if (responseObj != null && responseObj.size() > 0) {
                            for (ItemApiModel itemResponse : responseObj) {
                                ItemModel itemModel = new ItemModel(itemResponse);
                                new Thread(() -> {
                                    itemSyncDao.insert(new ItemSyncHistoryModel(itemResponse.getId(), itemResponse.getDateMod(), itemResponse.getServerListId()));

                                    itemModel.setLocalListId(listId);

                                    ItemModel item = itemDao.getItemForServerId(itemModel.getServerId());

                                    if (item != null) {
                                        itemModel.setId(item.getId());
                                        itemModel.setImagePath(item.getImagePath());
                                        if (!itemResponse.getServerImageName().isEmpty()) {
                                            if (item.getImagePath() == null || item.getImagePath().isEmpty() && !itemResponse.isDelete()) {
                                                downloadImageFromServer(item.getServerId());
                                            } else if (item.getServerImageName() == null && !itemResponse.isDelete()) {
                                                downloadImageFromServer(item.getServerId());
                                            } else if (item.getServerImageName() != null && !item.getServerImageName().equals(itemResponse.getServerImageName()) && !itemResponse.isDelete()) {
                                                downloadImageFromServer(item.getServerId());
                                            } else {
                                                itemModel.setServerImageName(itemResponse.getServerImageName());
                                                itemModel.setImagePath(item.getImagePath());
                                            }
                                        }
                                        if (itemResponse.isDelete()) {
                                            itemDao.delete(itemModel);
                                        } else {
                                            itemDao.update(itemModel);
                                        }
                                        if (itemResponse.getHasImage() && !itemResponse.getServerImageName().equals(itemModel.getServerImageName()) && !itemResponse.isDelete()) {
                                            downloadImageFromServer(itemResponse.getId());
                                        }
                                    } else {
                                        if (!itemResponse.isDelete()) {
                                            itemModel.setLocalListId(listId);
                                            itemDao.insert(itemModel);
                                            if (itemResponse.getHasImage()) {
                                                downloadImageFromServer(itemResponse.getId());
                                            }
                                        }
                                    }

                                }).start();
                            }

                        }
                    }

                    @Override
                    public void error() {

                    }

                    @Override
                    public void noPermit() {
                        if(callback!=null) {
                            callback.noPerm();
                        }
                    }
                };

                Call<List<ItemApiModel>> call = network.sync(syncHistory, deviceId, serverListId);
                Requests.request(call, callbackResponse);
            }).start();
            new Thread(() -> {
                List<ItemModel> items = itemDao.getItems(listId);
                for (ItemModel item : items) {
                    if (item.getServerId() == 0) {
                        networkCreate(item);
                    }
                    if (item.getImagePath() != null && !item.getImagePath().isEmpty() && item.getServerImageName() != null && item.getServerImageName().isEmpty()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(item.getImagePath());
                        saveImageFromServer(bitmap, item.getId());
                    }
                    if (item.getIsDelete() == 1) {
                        networkRemove(item);
                    }
                    if (item.getDateModServer()!=null && Long.parseLong(item.getDateMod().substring(0, 10)) > Long.parseLong(item.getDateModServer().substring(0, 10))) {
                        networkUpdate(item);
                    }
                }

            }).start();
        }
    }

    private void downloadImageFromServer(long serverId) {
        Requests.RequestsInterface<ImageItemApiModel> callbackResponse = new Requests.RequestsInterface<ImageItemApiModel>() {
            @Override
            public void success(ImageItemApiModel responseObj) {
                if (responseObj != null) {
                    try {
                        if (responseObj.getImage() != null && !responseObj.getImage().isEmpty()) {
                            saveImageUtils.saveBase64ToImage(responseObj.getImage());
                            Uri uri = saveImageUtils.copyImageToGallery(saveImageUtils.getCurrentFilePath());
                            new Thread(() -> {
                                ItemModel item = itemDao.getItemForServerId(serverId);
                                item.setServerImageName(responseObj.getImageName());
                                item.setImagePath(uri.getPath());
                                itemDao.update(item);
                            }).start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void error() {
            }

            @Override
            public void noPermit() {
                if(callback!=null) {
                    callback.noPerm();
                }
            }
        };

        Call<ImageItemApiModel> call = network.downloadImage(serverId);
        Requests.request(call, callbackResponse);
    }


    public void saveImagePath(String imagePath, Bitmap bitmap, long id) {
        new Thread(() -> itemDao.saveImagePath(imagePath, id)).start();
        if (imagePath.isEmpty()) {
            deleteImageInServer(id);
        }
    }

    private void deleteImageInServer(long id) {
        new Thread(() -> {
            ItemModel item = itemDao.getItem(id);
            Call<ResponseBody> call = network.removeImage(item.getServerId());
            Requests.request(call, null);
        }).start();
    }

    private void saveImageFromServer(Bitmap bitmap, long id) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            String encodedBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            new Thread(() -> {
                ItemModel item = itemDao.getItem(id);
                ImageItemApiModel imageBody = new ImageItemApiModel(item.getServerId(), encodedBase64);
                Requests.RequestsInterface<ImageItemApiModel> callbackResponse = new Requests.RequestsInterface<ImageItemApiModel>() {
                    @Override
                    public void success(ImageItemApiModel responseObj) {
                        new Thread(() -> itemDao.setServerImageName(id, responseObj.getImage())).start();
                    }

                    @Override
                    public void error() {
                        callback.errorLoadImage();
                    }

                    @Override
                    public void noPermit() {
                        if(callback!=null) {
                            callback.noPerm();
                        }
                    }
                };

                Call<ImageItemApiModel> call = network.sendImage(imageBody);
                Requests.request(call, callbackResponse);
            }).start();
        }
    }

    public void unSubscribe() {
        disposable.dispose();
    }

    public void changeStatus(ItemModel item) {
        new Thread(() -> {
            itemDao.update(item);
            networkUpdate(item);
        }).start();
    }

    public void setServerListId(long serverId) {
        serverListId = serverId;
    }

    public interface ItemRepositoryCallback {
        void setItems(List<ItemModel> items);
        void errorLoadImage();

        void noPerm();
    }
}
