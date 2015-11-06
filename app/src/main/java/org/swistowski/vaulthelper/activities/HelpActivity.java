package org.swistowski.vaulthelper.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.swistowski.vaulthelper.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public static void showIntent(Context parent) {
        Intent intent = new Intent(parent, HelpActivity.class);
        Bundle b = new Bundle();
        intent.putExtras(b);
        parent.startActivity(intent);
    }
}
