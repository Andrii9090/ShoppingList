package com.kasandco.familyfinance.app.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.ItemNetworkInterface;
import com.kasandco.familyfinance.network.model.ImageItemApiModel;
import com.kasandco.familyfinance.network.model.ItemApiModel;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SaveImageUtils;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

@ItemActivityScope
public class ItemRepository {
    private ItemDao itemDao;
    private long listId;
    private long serverListId;
    private CompositeDisposable disposable;
    private ItemNetworkInterface network;
    private SharedPreferenceUtil sharedPreference;
    private boolean isLogged;
    private IsNetworkConnect isNetworkConnect;
    private ItemSyncHistoryDao itemSyncDao;
    private String deviceId;
    private SaveImageUtils saveImageUtils;

    @Inject
    public ItemRepository(ItemNetworkInterface _networkInterface, ItemDao _itemDao, SaveImageUtils _saveImage, SharedPreferenceUtil _sharedPreference, IsNetworkConnect networkConnect, ItemSyncHistoryDao _itemSyncDao) {
        network = _networkInterface;
        itemDao = _itemDao;
        sharedPreference = _sharedPreference;
        isLogged = sharedPreference.getSharedPreferences().getString(Constants.TOKEN, null) != null;
        isNetworkConnect = networkConnect;
        itemSyncDao = _itemSyncDao;
        saveImageUtils = _saveImage;
        disposable = new CompositeDisposable();
        deviceId = sharedPreference.getSharedPreferences().getString(Constants.DEVICE_ID, null);
    }

    public void create(String[] arrayText, long listId, long serverListId) {
        new Thread(() -> {
            long itemId = itemDao.insert(new ItemModel(arrayText[0], listId, serverListId));
            plusActiveItem(this.listId);
            ItemModel item = itemDao.getItem(itemId);
            networkCreate(item);
        }).start();
    }

    private void networkCreate(ItemModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ItemApiModel body = new ItemApiModel(item);
            Call<ItemApiModel> call = network.create(body);
            call.enqueue(new Callback<ItemApiModel>() {
                @Override
                public void onResponse(Call<ItemApiModel> call, Response<ItemApiModel> response) {
                    if (response.isSuccessful()) {
                        ItemModel itemResp = new ItemModel(response.body());
                        itemResp.setLocalListId(item.getLocalListId());
                        new Thread(() -> itemDao.update(itemResp)).start();
                    }
                }

                @Override
                public void onFailure(Call<ItemApiModel> call, Throwable t) {

                }
            });
        }
    }

    public void plusActiveItem(long listId) {
        itemDao.plusActiveItemsInList(listId);
    }

    public void minusActiveItem(long listId) {
        itemDao.minusActiveItemsInList(listId);
    }

    public void plusInactiveItem(long listId) {
        itemDao.plusInactiveItemsInList(listId);
    }

    public void minusInactiveItem(long listId) {
        itemDao.minusInactiveItemsInList(listId);
    }

    public void remove(ItemModel item) {
        if (isLogged) {
            networkRemove(item);
            new Thread(() -> {
                if (item.getStatus() == 1) {
                    itemDao.minusActiveItemsInList(listId);
                } else {
                    itemDao.minusInactiveItemsInList(listId);
                }
            }).start();
        } else {
            new Thread(() -> {
                itemDao.delete(item);
                if (item.getStatus() == 1) {
                    itemDao.minusActiveItemsInList(listId);
                } else {
                    itemDao.minusInactiveItemsInList(listId);
                }
            }).start();
        }

    }

    private void networkRemove(ItemModel item) {

        if (!isNetworkConnect.isInternetAvailable()) {
            if (item.getIsDelete() != 1) {
                item.setIsDelete(1);
                new Thread(() -> itemDao.update(item)).start();
            }
        } else {
            Call<ResponseBody> call = network.remove(item.getServerId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        new Thread(() -> {
                            itemDao.delete(item);
                        }).start();
                    } else {
                        item.setIsDelete(1);
                        new Thread(() -> itemDao.update(item)).start();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    item.setIsDelete(1);
                    new Thread(() -> itemDao.update(item)).start();
                }
            });
        }
    }

    public void update(ItemModel item) {
        new Thread(() -> itemDao.update(item)).start();
        networkUpdate(item);
    }

    private void networkUpdate(ItemModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            List<ItemApiModel> array = new ArrayList<>();
            array.add(new ItemApiModel(item));
            Call<List<ItemApiModel>> call = network.update(array);
            call.enqueue(new Callback<List<ItemApiModel>>() {
                @Override
                public void onResponse(Call<List<ItemApiModel>> call, Response<List<ItemApiModel>> response) {
                    if (response.isSuccessful()) {
                        for (ItemApiModel item : response.body()) {
                            ItemModel itemModel = new ItemModel(item);
                            new Thread(() -> itemDao.update(itemModel));
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ItemApiModel>> call, Throwable t) {

                }
            });
        }
    }

    public void getAll(long listId, long serverListId, ItemRepositoryCallback callback) {
        this.listId = listId;
        this.serverListId = serverListId;
        disposable.add(itemDao.getAllItems(listId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> callback.setItems(list), error -> Log.e("Error", error.toString())));
        sync(listId);
    }

    private void sync(long listId) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            new Thread(() -> {
                List<ItemModel> items = itemDao.getItems(listId);
                for (ItemModel item : items) {
                    if (item.getServerId() == 0) {
                        networkCreate(item);
                    }
                    if (item.getImagePath() != null && !item.getImagePath().isEmpty() && item.getServerImageName()!=null && item.getServerImageName().isEmpty()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(item.getImagePath());
                        saveImageFromServer(bitmap, item);
                    }
                    if (item.getIsDelete() == 1) {
                        networkRemove(item);
                    }
                    if (Long.parseLong(sharedPreference.getSharedPreferences().getString(Constants.LAST_SYNC_ITEM_LIST, "0")) < Long.parseLong(item.getDateMod().substring(0, 10))) {
                        networkUpdate(item);
                    }
                }
                List<ItemSyncHistoryModel> syncHistory = itemSyncDao.getAll(serverListId);

                Call<List<ItemApiModel>> call = network.sync(syncHistory, deviceId, serverListId);
                call.enqueue(new Callback<List<ItemApiModel>>() {
                    @Override
                    public void onResponse(Call<List<ItemApiModel>> call, Response<List<ItemApiModel>> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> {
                                itemSyncDao.deleteAll(Long.parseLong(response.headers().get("server-list-id")));
                            }).start();
                            if (response.body() != null && response.body().size() > 0) {
                                String lastUpdate = null;
                                for (ItemApiModel itemResponse : response.body()) {
                                    ItemModel itemModel = new ItemModel(itemResponse);
                                    new Thread(() -> {
                                        itemSyncDao.insert(new ItemSyncHistoryModel(itemResponse.getId(), itemResponse.getDateMod(), itemResponse.getServerListId()));
                                        itemModel.setLocalListId(listId);
                                        ItemModel item = itemDao.getItemForServerId(itemModel.getServerId());
                                        if (item != null) {
                                            if (!itemResponse.getServerImageName().isEmpty()) {
                                                if (item.getImagePath() == null || item.getImagePath().isEmpty()) {
                                                    downloadImageFromServer(item.getServerId());
                                                } else if (item.getServerImageName() == null) {
                                                    downloadImageFromServer(item.getServerId());
                                                } else if (item.getServerImageName() != null && !item.getServerImageName().equals(itemResponse.getServerImageName())) {
                                                    downloadImageFromServer(item.getServerId());
                                                } else {
                                                    itemModel.setServerImageName(itemResponse.getServerImageName());
                                                    itemModel.setImagePath(item.getImagePath());
                                                }
                                            }
                                            itemModel.setId(item.getId());
                                            if (itemResponse.isDelete()) {
                                                if(itemModel.getStatus()==1){
                                                    minusActiveItem(itemModel.getLocalListId());
                                                }else {
                                                    minusInactiveItem(itemModel.getLocalListId());
                                                }
                                                itemDao.delete(itemModel);
                                            } else if (itemDao.update(itemModel) <= 0) {
                                                itemDao.insert(itemModel);
                                                if(itemModel.getStatus()==1){
                                                    plusActiveItem(itemModel.getLocalListId());
                                                }else {
                                                    plusInactiveItem(itemModel.getLocalListId());
                                                }
                                            }
                                        } else {
                                            itemDao.insert(itemModel);
                                            if (itemResponse.getHasImage()) {
                                                downloadImageFromServer(itemResponse.getId());
                                            }
                                        }
                                    }).start();
                                    if (lastUpdate == null) {
                                        lastUpdate = itemResponse.getDateMod();
                                    } else {
                                        if (Long.parseLong(lastUpdate) < Long.parseLong(itemResponse.getDateMod()))
                                            lastUpdate = itemResponse.getDateMod();
                                    }
                                }
                                sharedPreference.getEditor().putString(Constants.LAST_SYNC_ITEM_LIST, lastUpdate).apply();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ItemApiModel>> call, Throwable t) {

                    }
                });
            }).start();
        }
    }

    private void downloadImageFromServer(long serverId) {
        Call<ImageItemApiModel> call = network.downloadImage(serverId);
        call.enqueue(new Callback<ImageItemApiModel>() {
            @Override
            public void onResponse(Call<ImageItemApiModel> call, Response<ImageItemApiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        if (response.body().getImage() != null && !response.body().getImage().isEmpty()) {
                            saveImageUtils.saveBase64ToImage(response.body().getImage());
                            Uri uri = saveImageUtils.copyImageToGallery(saveImageUtils.getCurrentFilePath());
                            new Thread(() -> {
                                ItemModel item = itemDao.getItemForServerId(serverId);
                                item.setServerImageName(response.body().getImageName());
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
            public void onFailure(Call<ImageItemApiModel> call, Throwable t) {

            }
        });
    }


    public void saveImagePath(String imagePath, Bitmap bitmap, long id) {
        new Thread(() -> itemDao.saveImagePath(imagePath, id)).start();
        if (imagePath.isEmpty()) {
            deleteImageInServer(id);
        }
        if (bitmap != null) {
            saveImageFromServer(bitmap, id);
        }
    }

    private void deleteImageInServer(long id) {
        new Thread(() -> {
            ItemModel item = itemDao.getItem(id);
            Call<ResponseBody> call = network.removeImage(item.getServerId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
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
                Call<ImageItemApiModel> call = network.sendImage(imageBody);
                call.enqueue(new Callback<ImageItemApiModel>() {
                    @Override
                    public void onResponse(Call<ImageItemApiModel> call, Response<ImageItemApiModel> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> itemDao.setServerImageName(id, response.body().getImage())).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageItemApiModel> call, Throwable t) {

                    }
                });
            }).start();
        }
    }

    private void saveImageFromServer(Bitmap bitmap, ItemModel item) {
        if (isLogged && isNetworkConnect.isInternetAvailable()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            new Thread(() -> {
                ImageItemApiModel imageBody = new ImageItemApiModel(item.getServerId(), encodedBase64);
                Call<ImageItemApiModel> call = network.sendImage(imageBody);
                call.enqueue(new Callback<ImageItemApiModel>() {
                    @Override
                    public void onResponse(Call<ImageItemApiModel> call, Response<ImageItemApiModel> response) {
                        if (response.isSuccessful()) {
                            new Thread(() -> itemDao.setServerImageName(item.getId(), response.body().getImage())).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageItemApiModel> call, Throwable t) {

                    }
                });
            }).start();
        }
    }

    public void unSubscribe() {
        disposable.dispose();
    }

    public void changeStatus(ItemModel item) {
        new Thread(() -> {
            if (item.getStatus() == 0) {
                plusInactiveItem(listId);
                minusActiveItem(listId);
            } else {
                plusActiveItem(listId);
                minusInactiveItem(listId);
            }
            itemDao.update(item);
        }).start();
        networkUpdate(item);
    }

    public interface ItemRepositoryCallback {
        void setItems(List<ItemModel> items);
    }
}
