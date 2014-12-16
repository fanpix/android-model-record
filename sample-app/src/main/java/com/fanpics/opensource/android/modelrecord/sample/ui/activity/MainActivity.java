package com.fanpics.opensource.android.modelrecord.sample.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.fanpics.opensource.android.modelrecord.sample.R;
import com.fanpics.opensource.android.modelrecord.sample.data.model.ImgurItem;
import com.fanpics.opensource.android.modelrecord.sample.data.model.record.ImgurDataRecord;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadFailedEvent;
import com.fanpics.opensource.android.modelrecord.sample.event.ImgurDataLoadSucceededEvent;
import com.fanpics.opensource.android.modelrecord.sample.ui.adapter.ImgurAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Bus bus;
    private List<ImgurItem> imgurItems;
    private ImgurAdapter adapter;
    private MenuItem refreshButton;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupEventBus();
        setupList();
        loadData();
    }

    private void setupEventBus() {
        bus = new Bus();
        bus.register(this);
    }

    private void setupList() {
        adapter = new ImgurAdapter(LayoutInflater.from(this), this);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
    }

    private void loadData() {
        new ImgurDataRecord(this, bus).load();
        isLoading = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refreshButton = menu.findItem(R.id.action_refresh);
        if (isLoading) {
            startReloadAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            startReloadAnimation();
            new ImgurDataRecord(this, bus).refresh();
            isLoading = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onListLoaded(ImgurDataLoadSucceededEvent event) {
        imgurItems = event.getResult().getFirstTenItems();
        displayImgurData();
        if (event.hasFinished()) {
            isLoading = false;
            stopReloadAnimation();
        }
    }

    private void displayImgurData() {
        adapter.setImgurItems(imgurItems);
    }

    private void stopReloadAnimation() {
        refreshButton.setEnabled(true);
        refreshButton.setActionView(null);
    }

    private void startReloadAnimation() {
        refreshButton.setEnabled(false);
        refreshButton.setActionView(R.layout.action_refresh);
    }

    @Subscribe
    public void onListLoadFailed(ImgurDataLoadFailedEvent event) {
        isLoading = false;
        stopReloadAnimation();
        Log.e("List load failed", "exception", event.getError());
//        throw new RuntimeException(event.getError());

        displayLoadFailedToast();
    }

    private void displayLoadFailedToast() {
        Toast.makeText(this, R.string.could_not_load, Toast.LENGTH_LONG).show();
    }
}
