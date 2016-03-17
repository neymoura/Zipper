package br.unifor.sd.zipper.worker;

/**
 * Created by ney on 3/9/16.
 */
public interface WorkerListener {

    void workerFinished(int threadId, long absoluteTime);

}
