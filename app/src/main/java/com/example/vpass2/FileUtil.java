package com.example.vpass2;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/*
 * All things file handling
 */

public class FileUtil {
    private ContentResolver contentResolver;

    public FileUtil(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void saveFile(Uri sourceUri, File destinationFile)
    {
        String destinationFilename = destinationFile.getAbsolutePath();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(contentResolver.openInputStream(sourceUri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Very dirty hack, a lot of assumptions
    public void promote2to1(File applicationCacheDirPath) {
        File old = new File(applicationCacheDirPath + "/1.png");
        cleanDelete(old);

        File old2 = new File(applicationCacheDirPath + "/2.png");
        File new2 = new File(applicationCacheDirPath + "/1.png");
        old2.renameTo(new2);
    }

    public void deleteAll(File[] list) {
        for (File file : list) {
            cleanDelete(file);
        }
    }

    private boolean cleanDelete(File file) {
        file.delete();

        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if(outChannel != null)
                outChannel.close();
        }
    }
}
