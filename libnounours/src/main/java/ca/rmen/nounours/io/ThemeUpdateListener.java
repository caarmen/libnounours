package ca.rmen.nounours.io;

public interface ThemeUpdateListener {
    public void updatedFile(String fileName, int fileNumber, int totalFiles, boolean updateOk);
}
