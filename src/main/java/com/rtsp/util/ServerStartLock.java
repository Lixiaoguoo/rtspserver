package com.rtsp.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

/**
 * @author Lzq
 * @date 2019/10/28
 * @since jdk1.8
 */
public class ServerStartLock {
    private File file = null;
    private FileLock fileLock = null;
    private RandomAccessFile randomAccessFile = null;

    public ServerStartLock() {
        this.file = new File(System.getProperty("user.dir") + "\\service.lock");
    }

    /**
     * 文件加锁
     *
     * @return 加锁结果
     */
    public boolean lock() {
        try {
            if (this.file.exists())
                file.createNewFile();
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock == null) {
                randomAccessFile.close();
                return false;
            }
            return fileLock.isValid();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 释放文件锁
     *
     * @return 释放结果
     */
    public boolean unlock() {
        if (!file.exists()) {
            return true;
        }
        try {
            if (fileLock != null) {
                fileLock.release();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            return file.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

}
