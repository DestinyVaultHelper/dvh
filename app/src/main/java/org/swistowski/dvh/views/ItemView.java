package org.swistowski.dvh.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.swistowski.dvh.R;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.util.Data;
import org.swistowski.dvh.util.ImageStorage;


public class ItemView extends FrameLayout {
    private static final String LOG_TAG = "ItemView";
    private final String FAVORITES_LABEL = "Favorites";
    private ImageStorage.DownloadImageTask mDownloadImageTask;
    private Runnable requireReloadDataListener;

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

    public void setItem(Item item){
        String details = "";
        /*
        if(item.getBucketName()!=null){
            details += item.getBucketName()+ " ";
        }*/
        if(item.getStackSize()!=1){
            details += "Stack size: " + item.getStackSize()+" ";
        }
        if(item.getPrimaryStatValue()!=0){
            details += item.getPrimaryStatValue() + " ";
        }
        if(item.getDamageType()!=0){
            details += item.getDamageTypeName()+" ";
        }
        if(item.isEquipped()){
            details += "Equipped ";
        }
        setDetails(details);
        setLabel(item.getName());
        setUrl(item);
        setOwner(Data.getInstance().getItemOwnerName(item));
        CheckBox cb = ((CheckBox) findViewById(R.id.favorite_button));
        final long item_id = item.getInstanceId();

        boolean hasLabel = Data.getInstance().hasLabel(item_id, FAVORITES_LABEL);
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(hasLabel);
        /*
        final DB db = new DB(getContext());
        cb.setChecked(db.itemHasLabel(item.getItemHash(), FAVORITES_LABEL));
        */
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Data.getInstance().addLabel(item_id, FAVORITES_LABEL);
                } else {
                    Data.getInstance().deleteLabel(item_id, FAVORITES_LABEL);
                }
                requireReloadDataListener.run();
            }
        });


    }
    private void setLabel(String text) {
        TextView tv = (TextView) findViewById(R.id.label_name);
        tv.setText(text);
    }

    private void setOwner(String text) {
        TextView tv = (TextView) findViewById(R.id.label_owner);
        tv.setText(text);
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
        if (ImageStorage.getInstance().getImage(item.getItemHash())!=null) {
            iv.setImageBitmap(ImageStorage.getInstance().getImage(item.getItemHash()));
        } else {
            iv.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            mDownloadImageTask = ImageStorage.getInstance().fetchImage(item.getItemHash()+"", item.getIcon(), new ImageStorage.UrlFetchWaiter() {
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
}