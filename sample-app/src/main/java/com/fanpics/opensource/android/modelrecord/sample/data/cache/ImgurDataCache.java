package com.fanpics.opensource.android.modelrecord.sample.data.cache;

import android.content.Context;

import com.fanpics.opensource.android.modelrecord.RecordCache;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurData;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ImgurDataCache implements RecordCache<ImgurData> {
    private final Context context;

    public ImgurDataCache(Context context) {
        this.context = context;
    }

    @Override
    public ImgurData load(Object key) {
        Realm realm = Realm.getInstance(context);
        RealmQuery<ImgurItem> query = realm.where(ImgurItem.class);
        RealmResults<ImgurItem> realmResults = query.findAll();

        if (realmResults.size() > 0) {
            return createImgurData(realmResults);
        }

        return null;
    }

    private ImgurData createImgurData(RealmResults<ImgurItem> realmResults) {
        ImgurData imgurData = new ImgurData();
        List<ImgurItem> convertedList = new ArrayList<>();

        for (ImgurItem item: realmResults) {
            ImgurItem convertItem = new ImgurItem();
            ImgurItem.cloneInto(item, convertItem);
            convertedList.add(convertItem);
        }

        imgurData.setImgurItems(convertedList);
        return imgurData;
    }

    @Override
    public void store(Object key, ImgurData imgurData) {
        clear();
        storeNewList(imgurData);
    }

    private void storeNewList(ImgurData imgurData) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        for (ImgurItem item : imgurData.getFirstTenItems()) {
            storeItem(realm, item);
        }

        realm.commitTransaction();

    }

    private void storeItem(Realm realm, ImgurItem item) {
        ImgurItem realmItem = realm.createObject(ImgurItem.class);
        ImgurItem.cloneInto(item, realmItem);
    }

    @Override
    public void store(Object key, List<ImgurData> imgurData) {

    }

    @Override
    public List<ImgurData> loadList(Object key) {
        return null;
    }

    @Override
    public void clear() {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        RealmQuery<ImgurItem> query = realm.where(ImgurItem.class);
        query.findAll().clear();
        realm.commitTransaction();
    }

    @Override
    public void delete(ImgurData object) {

    }
}
