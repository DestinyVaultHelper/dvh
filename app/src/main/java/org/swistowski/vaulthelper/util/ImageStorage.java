package org.swistowski.vaulthelper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageStorage {
    private static final ImageStorage ourInstance = new ImageStorage();
    // private final HashMap<String, Bitmap> cachedImages = new HashMap<String, Bitmap>();
    private Context mContext;

    private ImageStorage() {
    }

    public static ImageStorage getInstance() {
        return ourInstance;
    }
    /*
    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    */

    /*
    public boolean hasImage(long url) {
        if (!cachedImages.containsKey(url)) {
            try {
                FileInputStream fis = mContext.openFileInput(md5(url));
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                cachedImages.put(url, bitmap);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
        return cachedImages.containsKey(url);
    }
    */

    public Bitmap getImage(long itemHash) {
        return getImage(itemHash+"");
    }

    public Bitmap getImage(String itemHash){
        try{
            FileInputStream fis = mContext.openFileInput(itemHash+".png");
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e){
            return null;
        }
    }

    private void saveImage(String itemHash, Bitmap bitmap) {
            try  {
                FileOutputStream fos = mContext.openFileOutput(itemHash+".png", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public DownloadImageTask fetchImage(final String itemHash, final String url, final UrlFetchWaiter ufw) {
        Bitmap bmp = getImage(itemHash);
        if(bmp!=null){
            ufw.onImageFetched(bmp);
        } else {
            DownloadImageTask dit = new DownloadImageTask(new UrlFetchWaiter() {
                @Override
                public void onImageFetched(Bitmap bitmap) {
                    saveImage(itemHash, bitmap);
                    ufw.onImageFetched(bitmap);
                }
            });
            dit.execute(url);
            return dit;
        }
        return null;
    }

    public interface UrlFetchWaiter {
        public void onImageFetched(Bitmap bitmap);
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final UrlFetchWaiter ufw;

        public DownloadImageTask(UrlFetchWaiter ufw) {
            this.ufw = ufw;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://www.bungie.net" + urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ufw.onImageFetched(result);
            }
        }
    }

}
