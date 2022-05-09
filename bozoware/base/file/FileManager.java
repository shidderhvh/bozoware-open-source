package bozoware.base.file;

import bozoware.base.util.Wrapper;

import java.io.File;

public class FileManager {

    private final String clientDirectory = Wrapper.getMinecraft().mcDataDir.getPath() + "/BozoWare";

    public FileManager() {
        final File clientDirectoryFolder = new File(clientDirectory);
        if (!clientDirectoryFolder.exists()) {
            if (clientDirectoryFolder.mkdirs())
                System.out.println("Created client directory...");
        }
    }

    public void addSubDirectory(String directoryName) {
        final File newDirectory = new File(String.format("%s/%s", clientDirectory, directoryName));
        if (!newDirectory.exists())
            if (newDirectory.mkdirs())
                System.out.println("Created sub directory: " + directoryName);
    }

    public String getClientDirectory() {
        return clientDirectory;
    }
}
