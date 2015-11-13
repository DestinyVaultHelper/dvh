package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.adapters.MaterialsAdapter;
import org.swistowski.vaulthelper.util.ImageStorage;

/**
 * Created by damian on 12.11.15.
 */
public class MaterialView extends FrameLayout {
    private MaterialsAdapter.Material material;
    private ImageStorage.DownloadImageTask mDownloadImageTask;

    public MaterialView(Context context) {
        super(context);
        init();
    }

    public MaterialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.material_view, this, true);
    }

    public void setMaterial(MaterialsAdapter.Material material) {
        if (mDownloadImageTask != null) {
            mDownloadImageTask.cancel(true);
        }
        final ImageView iv = (ImageView) findViewById(R.id.icon_material);
        if (ImageStorage.getInstance().getImage(material.getHash()) != null) {
            iv.setImageBitmap(ImageStorage.getInstance().getImage(material.getHash()));
        } else {
            iv.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            mDownloadImageTask = ImageStorage.getInstance().fetchImage(material.getHash() + "", material.getUrl(), new ImageStorage.UrlFetchWaiter() {
                @Override
                public void onImageFetched(Bitmap bitmap) {
                    iv.setImageBitmap(bitmap);
                    invalidate();
                }
            });
        }
        TextView nameView = (TextView) findViewById(R.id.materialName);
        nameView.setText(material.getName());
        TextView quantityView = (TextView) findViewById(R.id.materialQuantity);
        quantityView.setText(""+material.getCount());

    }
}
