package com.iyuba.core.util;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ACache {
    public static final int TIME_HOUR = 3600;
    public static final int TIME_DAY = 86400;
    private static final int MAX_SIZE = 50000000;
    private static final int MAX_COUNT = 2147483647;
    private static Map<String, ACache> mInstanceMap = new HashMap();
    private ACache.ACacheManager mCache;

    public static ACache get(Context ctx) {
        return get(ctx, "ACache");
    }

    public static ACache get(Context ctx, String cacheName) {
        File f = new File(ctx.getCacheDir(), cacheName);
        return get(f, 50000000L, 2147483647);
    }

    public static ACache get(File cacheDir) {
        return get(cacheDir, 50000000L, 2147483647);
    }

    public static ACache get(Context ctx, long max_zise, int max_count) {
        File f = new File(ctx.getCacheDir(), "ACache");
        return get(f, max_zise, max_count);
    }

    public static ACache get(File cacheDir, long max_zise, int max_count) {
        ACache manager = (ACache) mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACache(cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }

        return manager;
    }

    private static String myPid() {
        return "_" + Process.myPid();
    }

    private ACache(File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        } else {
            this.mCache = new ACache.ACacheManager(cacheDir, max_size, max_count);
        }
    }

    public void put(String key, String value) {
        File file = this.mCache.newFile(key);
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

            this.mCache.put(file);
        }

    }

    public void put(String key, String value, int saveTime) {
        this.put(key, ACache.Utils.newStringWithDateInfo(saveTime, value));
    }

    public String getAsString(String key) {
        File file = this.mCache.get(key);
        if (!file.exists()) {
            return null;
        } else {
            boolean removeFile = false;
            BufferedReader in = null;

            String var7;
            try {
                String currentLine;
                try {
                    in = new BufferedReader(new FileReader(file));

                    String readString;
                    for (readString = ""; (currentLine = in.readLine()) != null; readString = readString + currentLine) {
                    }

                    if (ACache.Utils.isDue(readString)) {
                        removeFile = true;
                        var7 = null;
                        return var7;
                    }

                    var7 = ACache.Utils.clearDateInfo(readString);
                } catch (IOException var18) {
                    var18.printStackTrace();
                    currentLine = null;
                    return currentLine;
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException var17) {
                        var17.printStackTrace();
                    }
                }

                if (removeFile) {
                    this.remove(key);
                }

            }

            return var7;
        }
    }

    public void put(String key, JSONObject value) {
        this.put(key, value.toString());
    }

    public void put(String key, JSONObject value, int saveTime) {
        this.put(key, value.toString(), saveTime);
    }

    public JSONObject getAsJSONObject(String key) {
        String JSONString = this.getAsString(key);

        try {
            JSONObject obj = new JSONObject(JSONString);
            return obj;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public void put(String key, JSONArray value) {
        this.put(key, value.toString());
    }

    public void put(String key, JSONArray value, int saveTime) {
        this.put(key, value.toString(), saveTime);
    }

    public JSONArray getAsJSONArray(String key) {
        String JSONString = this.getAsString(key);

        try {
            JSONArray obj = new JSONArray(JSONString);
            return obj;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public void put(String key, byte[] value) {
        File file = this.mCache.newFile(key);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

            this.mCache.put(file);
        }

    }

    public OutputStream put(String key) throws FileNotFoundException {
        return new ACache.xFileOutputStream(this.mCache.newFile(key));
    }

    public InputStream get(String key) throws FileNotFoundException {
        File file = this.mCache.get(key);
        return !file.exists() ? null : new FileInputStream(file);
    }

    public void put(String key, byte[] value, int saveTime) {
        this.put(key, ACache.Utils.newByteArrayWithDateInfo(saveTime, value));
    }

    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;

        Object var5;
        try {
            File file = this.mCache.get(key);
            if (!file.exists()) {
                var5 = null;
                return (byte[]) var5;
            }

            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            if (!ACache.Utils.isDue(byteArray)) {
                byte[] var21 = ACache.Utils.clearDateInfo(byteArray);
                return var21;
            }

            removeFile = true;
            Object var6 = null;
            return (byte[]) var6;
        } catch (Exception var18) {
            var18.printStackTrace();
            var5 = null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

            if (removeFile) {
                this.remove(key);
            }

        }

        return (byte[]) var5;
    }

    public void put(String key, Serializable value) {
        this.put(key, (Serializable) value, -1);
    }

    public void put(String key, Serializable value, int saveTime) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] data = baos.toByteArray();
            if (saveTime != -1) {
                this.put(key, data, saveTime);
            } else {
                this.put(key, data);
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

    }

    public Object getAsObject(String key) {
        byte[] data = this.getAsBinary(key);
        if (data != null) {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;

            Object var6;
            try {
                bais = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bais);
                Object reObject = ois.readObject();
                var6 = reObject;
                return var6;
            } catch (Exception var20) {
                var20.printStackTrace();
                var6 = null;
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                } catch (IOException var19) {
                    var19.printStackTrace();
                }

                try {
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException var18) {
                    var18.printStackTrace();
                }

            }

            return var6;
        } else {
            return null;
        }
    }

    public void put(String key, Bitmap value) {
        this.put(key, ACache.Utils.Bitmap2Bytes(value));
    }

    public void put(String key, Bitmap value, int saveTime) {
        this.put(key, ACache.Utils.Bitmap2Bytes(value), saveTime);
    }

    public Bitmap getAsBitmap(String key) {
        return this.getAsBinary(key) == null ? null : ACache.Utils.Bytes2Bimap(this.getAsBinary(key));
    }

    public void put(String key, Drawable value) {
        this.put(key, ACache.Utils.drawable2Bitmap(value));
    }

    public void put(String key, Drawable value, int saveTime) {
        this.put(key, ACache.Utils.drawable2Bitmap(value), saveTime);
    }

    public Drawable getAsDrawable(String key) {
        return this.getAsBinary(key) == null ? null : ACache.Utils.bitmap2Drawable(ACache.Utils.Bytes2Bimap(this.getAsBinary(key)));
    }

    public File file(String key) {
        File f = this.mCache.newFile(key);
        return f.exists() ? f : null;
    }

    public boolean remove(String key) {
        return this.mCache.remove(key);
    }

    public void clear() {
        this.mCache.clear();
    }

    private static class Utils {
        private static final char mSeparator = ' ';

        private Utils() {
        }

        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr;
                for (saveTimeStr = strs[0]; saveTimeStr.startsWith("0"); saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length())) {
                }

                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                return System.currentTimeMillis() > saveTime + deleteAfter * 1000L;
            } else {
                return false;
            }
        }

        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(32) + 1, strInfo.length());
            }

            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            return hasDateInfo(data) ? copyOfRange(data, indexOf(data, ' ') + 1, data.length) : data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == 45 && indexOf(data, ' ') > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, ' ')));
                return new String[]{saveDate, deleteAfter};
            } else {
                return null;
            }
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; ++i) {
                if (data[i] == c) {
                    return i;
                }
            }

            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0) {
                throw new IllegalArgumentException(from + " > " + to);
            } else {
                byte[] copy = new byte[newLength];
                System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
                return copy;
            }
        }

        private static String createDateInfo(int second) {
            String currentTime;
            for (currentTime = System.currentTimeMillis() + ""; currentTime.length() < 13; currentTime = "0" + currentTime) {
            }

            return currentTime + "-" + second + ' ';
        }

        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(CompressFormat.PNG, 100, baos);
                return baos.toByteArray();
            }
        }

        private static Bitmap Bytes2Bimap(byte[] b) {
            return b.length == 0 ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            } else {
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                @SuppressLint("WrongConstant") Config config = drawable.getOpacity() != -1 ? Config.ARGB_8888 : Config.RGB_565;
                Bitmap bitmap = Bitmap.createBitmap(w, h, config);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, w, h);
                drawable.draw(canvas);
                return bitmap;
            }
        }

        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            } else {
                BitmapDrawable bd = new BitmapDrawable(bm);
                bd.setTargetDensity(bm.getDensity());
                return new BitmapDrawable(bm);
            }
        }
    }

    public class ACacheManager {
        private final AtomicLong cacheSize;
        private final AtomicInteger cacheCount;
        private final long sizeLimit;
        private final int countLimit;
        private final Map<File, Long> lastUsageDates;
        protected File cacheDir;

        private ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.lastUsageDates = Collections.synchronizedMap(new HashMap());
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            this.cacheSize = new AtomicLong();
            this.cacheCount = new AtomicInteger();
            this.calculateCacheSizeAndCacheCount();
        }

        private void calculateCacheSizeAndCacheCount() {
            (new Thread(new Runnable() {
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cachedFiles = ACache.ACacheManager.this.cacheDir.listFiles();
                    if (cachedFiles != null) {
                        File[] var4 = cachedFiles;
                        int var5 = cachedFiles.length;

                        for (int var6 = 0; var6 < var5; ++var6) {
                            File cachedFile = var4[var6];
                            size = (int) ((long) size + ACache.ACacheManager.this.calculateSize(cachedFile));
                            ++count;
                            ACache.ACacheManager.this.lastUsageDates.put(cachedFile, cachedFile.lastModified());
                        }

                        ACache.ACacheManager.this.cacheSize.set((long) size);
                        ACache.ACacheManager.this.cacheCount.set(count);
                    }

                }
            })).start();
        }

        private void put(File file) {
            long valueSize;
            for (int curCacheCount = this.cacheCount.get(); curCacheCount + 1 > this.countLimit; curCacheCount = this.cacheCount.addAndGet(-1)) {
                valueSize = this.removeNext();
                this.cacheSize.addAndGet(-valueSize);
            }

            this.cacheCount.addAndGet(1);
            valueSize = this.calculateSize(file);

            long freedSize;
            for (long curCacheSize = this.cacheSize.get(); curCacheSize + valueSize > this.sizeLimit; curCacheSize = this.cacheSize.addAndGet(-freedSize)) {
                freedSize = this.removeNext();
            }

            this.cacheSize.addAndGet(valueSize);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            this.lastUsageDates.put(file, currentTime);
        }

        private File get(String key) {
            File file = this.newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            this.lastUsageDates.put(file, currentTime);
            return file;
        }

        private File newFile(String key) {
            return new File(this.cacheDir, key.hashCode() + "");
        }

        private boolean remove(String key) {
            File image = this.get(key);
            return image.delete();
        }

        private void clear() {
            this.lastUsageDates.clear();
            this.cacheSize.set(0L);
            File[] files = this.cacheDir.listFiles();
            if (files != null) {
                File[] var2 = files;
                int var3 = files.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    File f = var2[var4];
                    f.delete();
                }
            }

        }

        private long removeNext() {
            if (this.lastUsageDates.isEmpty()) {
                return 0L;
            } else {
                Long oldestUsage = null;
                File mostLongUsedFile = null;
                Set<Entry<File, Long>> entries = this.lastUsageDates.entrySet();
                synchronized (this.lastUsageDates) {
                    Iterator var5 = entries.iterator();

                    while (true) {
                        if (!var5.hasNext()) {
                            break;
                        }

                        Entry<File, Long> entry = (Entry) var5.next();
                        if (mostLongUsedFile == null) {
                            mostLongUsedFile = (File) entry.getKey();
                            oldestUsage = (Long) entry.getValue();
                        } else {
                            Long lastValueUsage = (Long) entry.getValue();
                            if (lastValueUsage < oldestUsage) {
                                oldestUsage = lastValueUsage;
                                mostLongUsedFile = (File) entry.getKey();
                            }
                        }
                    }
                }

                long fileSize = this.calculateSize(mostLongUsedFile);
                if (mostLongUsedFile.delete()) {
                    this.lastUsageDates.remove(mostLongUsedFile);
                }

                return fileSize;
            }
        }

        private long calculateSize(File file) {
            return file.length();
        }
    }

    class xFileOutputStream extends FileOutputStream {
        File file;

        public xFileOutputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        public void close() throws IOException {
            super.close();
            ACache.this.mCache.put(this.file);
        }
    }
}
