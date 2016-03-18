package br.unifor.sd.zipper.orchestrator;

import br.unifor.sd.zipper.manager.WorkManagerListener;
import br.unifor.sd.zipper.manager.WorkManager;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Ney Moura
 * @version 1.0
 * @since 17/03/2016
 * <p>
 * A classe WorkOrchestrator tem a função de automatizar a realização dos testes de compressão
 * através do gerenciamento do objeto WorkManager, que realiza a compressão de um dado conjunto
 * de arquivos.
 */
public class WorkOrchestrator implements WorkManagerListener {

    private static final String TAG = "WorkOrchestrator";

    private final WorkOrchestratorListener workOrchestratorListener;

    private List<File> dirList = null;

    private List<File> toCleanDirs = null;

    private int threadMode = WorkManager.MULTIPLE_THREADS;

    public WorkOrchestrator(WorkOrchestratorListener workOrchestratorListener) {
        this.dirList = new ArrayList<>();
        this.toCleanDirs = new ArrayList<>();
        this.workOrchestratorListener = workOrchestratorListener;
    }

    public void start(int threadMode) {

        this.threadMode = threadMode;

        //1. Localizar diretorios
        //2. Rodar WorkManager com todos arquivos de um diretorio
        //3. Guardar em um arquivo o tempo de compressao do diretório
        //4. Ir para próximo diretório

//        JFileChooser f = new JFileChooser();
//        f.setCurrentDirectory(new File("."));
//        f.setMultiSelectionEnabled(true);
//        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        f.showSaveDialog(null);
//
//        return f.getSelectedFiles();

        System.out.println(TAG + ": Starting orchestrator...");

        //limpa objetos, necessário caso o start esteja sendo invocado novamente
        this.dirList = new ArrayList<>();
        this.toCleanDirs = new ArrayList<>();

        //Procurando diretórios
        File[] files = new File(".").listFiles();

        for (File file : files) {

            //checa se diretorio
            if (file.isDirectory()) {
                dirList.add(file);
            }

        }

        //inicia work manager
        runManager(dequeDirectory());

    }

    private File dequeDirectory() {
        if (dirList.isEmpty()) {

            //No more directories/files to compress
            //finish
            finish();
            return null;

        } else {
            File dir = dirList.remove(0);
            toCleanDirs.add(dir);
            return dir;
        }
    }

    /**
     * Clean up the zip files generated
     */
    private void cleanUp() {

        for (File dir : toCleanDirs) {

            for (File file : dir.listFiles()) {
                if(file.getName().endsWith(".zip")){
                    file.delete();
                }
            }

        }

    }

    private void finish() {

        cleanUp();

        //finish orchestrator
        System.out.println(TAG + ": Finishing orchestrator...");

        workOrchestratorListener.workOrchestrationFinished();

    }

    private void runManager(File directory) {

        if(directory == null){
            return;
        }

        List<File> files = Arrays.asList(directory.listFiles());

        WorkManager workManager = new WorkManager(files, this);

        workManager.start(threadMode, directory.getPath());

    }

    @Override
    public void workManagerFinished(String jobId, long absoluteTime) {

        //Guarda resultados em um arquivo
        saveResultsToFile(jobId, absoluteTime);

        //Vai para próximo diretório
        runManager(dequeDirectory());

    }

    private void saveResultsToFile(String jobId, long absoluteTime) {

        int coresAvailable = Runtime.getRuntime().availableProcessors();
        long maxMemory = Runtime.getRuntime().maxMemory();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm");

        File resultsFile = new File("results.txt");

        //Verifica se arquivo de log já existe
        if (!resultsFile.exists()) {
            try {
                resultsFile.createNewFile();

                if(resultsFile.exists() && resultsFile.canWrite()){
                    BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile, true));

                    out.append("Date/Time" + "\t" + "System Cores" + "\t" + "JVM RAM" + "\t" + "Folder" + "\t" + "Time (milliseconds)");

                    out.flush();
                    out.close();
                }

            } catch (Exception e) {
                System.err.println("Falha ao criar arquivo de log : " + e.getMessage());
                e.printStackTrace();
            }
        }

        String line = sdf.format(new Date()) + "\t" + coresAvailable + "\t" + (maxMemory == Long.MAX_VALUE ? "0" : humanReadableByteCount(maxMemory, true)) + "\t" + jobId + "\t" + absoluteTime;

        //Verifica o arquivo de log pode ser escrito
        if (resultsFile.exists() && resultsFile.canWrite()) {

            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile, true));

                out.newLine();
                out.append(line);

                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("Não consigo escrever no arquivo de log ou o arquivo não existe ='(");
        }

        //printa log no console
        System.out.println(line);


    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
