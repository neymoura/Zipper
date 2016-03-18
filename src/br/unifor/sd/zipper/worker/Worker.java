package br.unifor.sd.zipper.worker;

import br.unifor.sd.zipper.utils.Compactador;

import java.io.File;

/**
 * Created by ney on 3/7/16.
 */
public class Worker implements Runnable {

    private static final String TAG = "Worker";

    private long absoluteWorkTime = 0L;
    private File file;
    private int threadId;
    private WorkerListener workerListener;

    public Worker(int threadId, File file, WorkerListener workerListener){

        this.file = file;
        this.threadId = threadId;
        this.workerListener = workerListener;

    }

    @Override
    public void run() {

        if(file == null){
            return;
        }

        long startTime = System.currentTimeMillis();

        compress();

        long endTime = System.currentTimeMillis();

        absoluteWorkTime = endTime - startTime;

        System.out.println(TAG+": ["+threadId+"] Compression finished! File: "+ file.getName() +" took "+ absoluteWorkTime +" miliseconds");

        workerListener.workerFinished(this.threadId, absoluteWorkTime);

    }

    private void compress(){

        try{
            Compactador.compactarParaZip(file);
        }catch (Exception e){
            e.printStackTrace();
        }

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

}