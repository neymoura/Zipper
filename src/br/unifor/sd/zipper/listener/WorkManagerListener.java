package br.unifor.sd.zipper.listener;

/**
 * Created by ney on 3/16/16.
 */
public interface WorkManagerListener {

    void jobFinished(String jobId, long absoluteTime);

}
