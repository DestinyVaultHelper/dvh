package org.swistowski.dvh.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.swistowski.dvh.util.Database;
import org.swistowski.dvh.R;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.util.ImageStorage;


public class ItemView extends FrameLayout {
    private ImageStorage.DownloadImageTask mDownloadImageTask;

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
        setOwner(Database.getInstance().getItemOwnerName(item));

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
}