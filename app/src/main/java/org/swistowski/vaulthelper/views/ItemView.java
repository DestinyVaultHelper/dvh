package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.storage.Labels;
import org.swistowski.vaulthelper.util.ImageStorage;


public class ItemView extends FrameLayout {
    private static final String LOG_TAG = "ItemView";
    private ImageStorage.DownloadImageTask mDownloadImageTask;
    private Runnable requireReloadDataListener;
    private boolean isGrayed;
    private Item mItem;

    public ItemView(Context context) {
        super(context);
        init();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view, this, true);
    }

    public void setItem(Item item) {
        String details = "";
        mItem = item;
        /*
        if(item.getBucketName()!=null){
            details += item.getBucketName()+ " ";
        }*/
        if (item.getStackSize() != 1) {
            details += "Stack size: " + item.getStackSize() + " ";
        }
        if (item.getPrimaryStatValue() != 0) {
            details += item.getPrimaryStatValue() + " ";
        }
        if (item.getDamageType() != 0) {
            details += item.getDamageTypeName() + " ";
        }
        if (item.isEquipped()) {
            details += "Equipped ";
        }
        setDetails(details);
        setLabel(item.getName());
        if (item.getIsCompleted()) {
            makeBorder();
        } else {
            removeBorder();
        }
        setUrl(item);
        setOwner(Items.getInstance().getItemOwnerName(item));
        CheckBox cb = ((CheckBox) findViewById(R.id.favorite_button));
        final long item_id = item.getInstanceId();
        if(item_id==0){
            cb.setVisibility(INVISIBLE);
        } else {
            cb.setVisibility(VISIBLE);
        }

        boolean hasLabel = Labels.getInstance().hasLabel(item_id, Labels.getInstance().getCurrent());
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(hasLabel);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Labels.getInstance().addLabelToItem(item_id, Labels.getInstance().getCurrent());
                } else {
                    Labels.getInstance().deleteLabelFromItem(item_id, Labels.getInstance().getCurrent());
                }
                requireReloadDataListener.run();
            }
        });
    }

    private void removeBorder() {
        ImageView iv = (ImageView) findViewById(R.id.icon_preview);
        iv.setPadding(0, 0, 0, 0);
    }

    private void makeBorder() {
        ImageView iv = (ImageView) findViewById(R.id.icon_preview);
        int border = 5;
        iv.setPadding(border, border, border, border);
        iv.setBackgroundColor(getResources().getColor(R.color.completed_border));
    }

    private void setLabel(String text) {
        TextView tv = (TextView) findViewById(R.id.label_name);
        tv.setText(text);
    }

    private void setOwner(String text) {
        TextView tv = (TextView) findViewById(R.id.label_owner);
        tv.setText(Html.fromHtml(text));
    }

    private void setDetails(String text) {
        TextView tv = (TextView) findViewById(R.id.label_details);
        tv.setText(text);
    }

    private void setUrl(Item item) {
        if (mDownloadImageTask != null) {
            mDownloadImageTask.cancel(true);
        }
        final ImageView iv = (ImageView) findViewById(R.id.icon_preview);
        if (ImageStorage.getInstance().getImage(item.getItemHash()) != null) {
            iv.setImageBitmap(ImageStorage.getInstance().getImage(item.getItemHash()));
        } else {
            iv.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            mDownloadImageTask = ImageStorage.getInstance().fetchImage(item.getItemHash() + "", item.getIcon(), new ImageStorage.UrlFetchWaiter() {
                @Override
                public void onImageFetched(Bitmap bitmap) {
                    iv.setImageBitmap(bitmap);
                    invalidate();
                }
            });
        }
    }

    public void setRequireReloadDataListener(Runnable requireReloadDataListener) {
        this.requireReloadDataListener = requireReloadDataListener;
    }

    public void setIsGrayed(boolean isGrayed) {
        this.isGrayed = isGrayed;

        if (!mItem.isMoveable()) {
            findViewById(R.id.item_view_root).setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if (isGrayed) {
            findViewById(R.id.item_view_root).setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            findViewById(R.id.item_view_root).setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }
    }

    public boolean getIsGrayed() {
        return this.isGrayed;
    }
}