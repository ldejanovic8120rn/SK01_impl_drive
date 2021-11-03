package com.driveimpl;

import com.storage.FileChecker;

public class DriveFileChecker extends FileChecker {

    @Override
    public boolean ckeckPath(String path) {
        return false;
    }

    @Override
    public boolean ckeckStoragePath(String path) {
        return false;
    }

    @Override
    public boolean checkNumOfFiles() {
        return false;
    }

    @Override
    public boolean checkMaxSize(long size) {
        return false;
    }

    @Override
    public boolean ckeckExtention(String extension) {
        return false;
    }
}
