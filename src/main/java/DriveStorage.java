import com.storage.Storage;
import com.utils.Privilege;

import java.io.File;
import java.util.List;

public class DriveStorage extends Storage {

    @Override
    public File getConfig(String path) {
        return null;
    }

    @Override
    public File getUsers(String path) {
        return null;
    }

    @Override
    public void createStorage(String path, String storageName, String adminName, String adminPsw) throws Exception {

    }

    @Override
    public void editConfig(String path, String maxSize, String maxNumOfFiles, List<String> unsupportedFiles) {

    }

    @Override
    public void editUsers(String path, String name, String password, Privilege privilege) {

    }
}
