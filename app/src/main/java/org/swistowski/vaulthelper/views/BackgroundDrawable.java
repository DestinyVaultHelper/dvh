package org.swistowski.vaulthelper.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by damian on 23.06.15.
 */
public class BackgroundDrawable extends Drawable {
    private Paint mPaint;
    private final String mEmblemHash;
    private final String mBackgroupdHash;

    public BackgroundDrawable(String emblemPath, String backgroundPath) {
        mPaint = new Paint();

        mEmblemHash = emblemPath.replace('/', '-');
        mBackgroupdHash = backgroundPath.replace('/', '-');
        if (ImageStorage.getInstance().getImage(mEmblemHash) == null) {
            ImageStorage.getInstance().fetchImage(mEmblemHash, emblemPath, new ImageStorage.UrlFetchWaiter() {
                @Override
                public void onImageFetched(Bitmap bitmap) {
                    invalidateSelf();
                }
            });
        }
        if (ImageStorage.getInstance().getImage(mBackgroupdHash) == null) {
            ImageStorage.getInstance().fetchImage(mBackgroupdHash, backgroundPath, new ImageStorage.UrlFetchWaiter() {
                @Override
                public void onImageFetched(Bitmap bitmap) {
                    invalidateSelf();
                }
            });
        }
    }

    @Override
    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Bitmap emblem = ImageStorage.getInstance().getImage(mEmblemHash);
        Bitmap background = ImageStorage.getInstance().getImage(mBackgroupdHash);
        if (background != null) {
            canvas.drawBitmap(background, new Rect(0, 0, background.getWidth(), background.getHeight()), new Rect(0, 0, width, height), mPaint);
        }
        if (emblem != null) {
            int margin = (height - emblem.getHeight()) / 2;
            canvas.drawBitmap(emblem, margin, margin, mPaint);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
