package br.unifor.sd.zipper.main;

import br.unifor.sd.zipper.listener.WorkManagerListener;
import br.unifor.sd.zipper.manager.WorkManager;

import java.io.File;
import java.util.ArrayList;

public class Main implements WorkManagerListener {

    private static ArrayList<File> filesArray = new ArrayList<>();

    public void main(String[] args) {

        File[] files = pickFiles(args);

        if (files == null || files.length == 0) {
            System.err.print("No files available!");
            return;
        }

        for (File file : files) {
            filesArray.add(file);
        }

        WorkManager manager = new WorkManager(filesArray, this);

        manager.start(WorkManager.MULTIPLE_THREADS, "general_job");

    }

    public static File[] pickFiles(String[] paths) {

//        JFileChooser f = new JFileChooser();
//        f.setCurrentDirectory(new File("."));
//        f.setMultiSelectionEnabled(true);
//        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        f.showSaveDialog(null);
//
//        return f.getSelectedFiles();

        File[] files = new File(".").listFiles();

        for (File file : files) {
            System.out.println("-> " + file.getName() + " and " + (file.isDirectory()?"is a directory":"is a file"));
        }

        return new File[0];

    }

    @Override
    public void jobFinished(String jobId, long absoluteTime) {
        
    }

}
