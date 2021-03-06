package br.unifor.sd.zipper.manager;

import br.unifor.sd.zipper.worker.WorkerListener;
import br.unifor.sd.zipper.worker.Worker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ney on 3/9/16.
 */
public class WorkManager implements WorkerListener{

    private static final String TAG = "WorkManager";

    public static final int SINGLE_THREAD = 1;
    public static final int MULTIPLE_THREADS = 2;

    private int threadMode = 0;

    private ArrayList<File> filesArray = new ArrayList<>();

    private int filesToCompressCount = 0;
    private int compressedFilesCount = 0;

    private long absoluteTime = 0L;

    //jobId, absoluteTime, fileType, fileCount, fileSize
    private String jobId;
    private String fileType;
    private long fileSize;
    private WorkManagerListener workManagerListener;

    public WorkManager(List<File> filesArray, WorkManagerListener workManagerListener) {
        this.filesArray = new ArrayList<>(filesArray);
        this.filesToCompressCount = this.filesArray.size();
        this.workManagerListener = workManagerListener;
    }

    public void start(int threadMode, String jobId){

        System.out.println(TAG+": ["+jobId+"] Starting workmanager..." );

        this.jobId = jobId;
        this.threadMode = threadMode;

        int threadQuantity;

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
        System.out.println(TAG+": ["+jobId+"] Jobs finished! I took " + absoluteTime + " miliseconds to compress " + filesToCompressCount + " files using " + threadQuantity + " worker(s)!" );

        if(this.workManagerListener != null){
            workManagerListener.workManagerFinished(this.jobId, absoluteTime, this.fileType, this.filesToCompressCount, this.fileSize);
        }

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
    public void workerFinished(int threadId, long absoluteTime, String fileType, long fileSize) {

        this.fileType = fileType;
        this.fileSize = fileSize;

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
