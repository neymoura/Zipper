package br.unifor.sd.zipper.manager;

import br.unifor.sd.zipper.listener.WorkerListener;
import br.unifor.sd.zipper.thread.Worker;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ney on 3/9/16.
 */
public class WorkManager implements WorkerListener{

    public static final int SINGLE_THREAD = 1;
    public static final int MULTIPLE_THREADS = 2;

    private int threadMode = 0;

    private ArrayList<File> filesArray = new ArrayList<>();

    private int filesToCompressCount = 0;
    private int compressedFilesCount = 0;

    private long absoluteTime = 0l;

    public WorkManager(ArrayList<File> filesArray) {
        this.filesArray = filesArray;

        this.filesToCompressCount = this.filesArray.size();

    }

    public void start(int threadMode){

        this.threadMode = threadMode;

        int threadQuantity = 0;

        switch (threadMode) {
            case SINGLE_THREAD:
                threadQuantity = 1;
                break;

            case MULTIPLE_THREADS:
                threadQuantity = filesToCompressCount;
                break;

            default:
                return;
        }

        //inicia threads
        for (int i = 0; i < threadQuantity; i++) {
            startThread(i, pickNextFile());
        }

        //espera todos arquivos serem comprimidos
        while (compressedFilesCount < filesToCompressCount) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //compressao terminou, contabilize o tempo
        System.out.print("Jobs done! I took " + absoluteTime/1000 + " seconds to compress " + filesToCompressCount + " files using " + threadQuantity + " thread(s)!");

    }

    private synchronized File pickNextFile() {

        try{
            return filesArray.remove(0);
        }catch (Exception e){
//            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void workerFinished(int threadId, long absoluteTime) {

        this.compressedFilesCount++;

        switch (threadMode) {
            case SINGLE_THREAD:
                this.absoluteTime += absoluteTime;
                break;

            case MULTIPLE_THREADS:
                if(absoluteTime > this.absoluteTime){
                    this.absoluteTime = absoluteTime;
                }
                break;

            default:
                return;
        }

        File nextFile = pickNextFile();

        startThread(threadId, nextFile);

    }

    private void startThread(int threadId, File nextFile) {
        if (nextFile != null){
            Worker worker = new Worker(threadId, nextFile, this);
            new Thread(worker).start();
        }
    }

}
