package org.swistowski.vaulthelper.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.adapters.LabelEditorAdapter;
import org.swistowski.vaulthelper.models.Label;
import org.swistowski.vaulthelper.storage.LabelMonitor;
import org.swistowski.vaulthelper.views.LabelEditView;

public class LabelsEditorActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String LOG_TAG = "LabelEditorActivity";
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddLabel();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listview = (ListView) findViewById(R.id.label_editor_list);
        adapter = new LabelEditorAdapter(this);
        listview.setAdapter(adapter);
        listview.setSelected(true);

        LabelMonitor.getInstance().registerAdapter(adapter);
        listview.setOnItemClickListener(this);
        listview.setOnItemLongClickListener(this);
    }

    @Override
    public void onDestroy() {
        LabelMonitor.getInstance().unregisterAdapter(adapter);
        super.onDestroy();
    }

    private void handleAddLabel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Label label = new Label(getString(R.string.new_label), -1, -196602);
        final LabelEditView labelEdit = new LabelEditView(this);
        labelEdit.setLabel(label);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(LOG_TAG, "Ok clicked");
                label.save();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v(LOG_TAG, "cancel clicked");
            }
        }).setView(labelEdit).setTitle(R.string.add_new_layout_label).create().show();

    }

    public static void showIntent(Context parent) {
        Intent intent = new Intent(parent, LabelsEditorActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        parent.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Label label = (Label) adapter.getItem(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LabelEditView labelEdit = new LabelEditView(this);
        labelEdit.setLabel(label);
        final String name = label.getName();
        final int color = (int) label.getColor();
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                label.save();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                label.setName(name);
                label.setColor(color);
            }
        }).setView(labelEdit).setTitle(R.string.edit_layout_label).create().show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(adapter.getCount()>1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final Label label = (Label) adapter.getItem(position);

            String text = String.format(getResources().getString(R.string.confirm_label_delete), label.getName());
            builder.setTitle(text).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    label.delete();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }
        return true;
    }
}
