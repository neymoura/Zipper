package br.unifor.sd.zipper.main;

import br.unifor.sd.zipper.manager.WorkManager;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Main {

    private static ArrayList<File> filesArray = new ArrayList<>();

    public static void main(String[] args) {

        File[] files = pickFiles(args);

        if (files == null || files.length == 0) {
            System.err.print("No files selected!");
            return;
        }

        for (File file : files) {
            filesArray.add(file);
        }

        WorkManager manager = new WorkManager(filesArray);

        manager.start(WorkManager.MULTIPLE_THREADS);

    }

    public static File[] pickFiles(String[] paths) {

        JFileChooser f = new JFileChooser();
        f.setCurrentDirectory(new File("."));
        f.setMultiSelectionEnabled(true);
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        f.showSaveDialog(null);

        return f.getSelectedFiles();

    }

}
