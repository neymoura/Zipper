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

    private List<File> dirList = null;

    private List<File> toCleanDirs = null;

    public WorkOrchestrator() {
        this.dirList = new ArrayList<>();
        this.toCleanDirs = new ArrayList<>();
    }

    public void start() {

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
        System.exit(0);

    }

    private void runManager(File directory) {

        List<File> files = Arrays.asList(directory.listFiles());

        WorkManager workManager = new WorkManager(files, this);

        workManager.start(WorkManager.MULTIPLE_THREADS, directory.getPath());

    }

    @Override
    public void jobFinished(String jobId, long absoluteTime) {

        //Guarda resultados em um arquivo
        armazenaResultado(jobId, absoluteTime);

        //Vai para próximo diretório
        runManager(dequeDirectory());

    }

    private void armazenaResultado(String jobId, long absoluteTime) {

        int coresAvailable = Runtime.getRuntime().availableProcessors();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm");

        File resultsFile = new File("results.txt");

        //Verifica se arquivo de log já existe
        if (!resultsFile.exists()) {
            try {
                resultsFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Falha ao criar arquivo de log : " + e.getMessage());
                e.printStackTrace();
            }
        }

        //Verifica o arquivo de log pode ser escrito
        if (resultsFile.exists() && resultsFile.canWrite()) {

            try {

                BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile, true));

                out.newLine();
                out.append(sdf.format(new Date()) + "\t" + coresAvailable + "\t" + jobId + "\t" + absoluteTime);

                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("Não consigo escrever no arquivo de log ou o arquivo não existe ='(");
        }

        //printa log no console
        System.out.println(sdf.format(new Date()) + "\t" + coresAvailable + "\t" + jobId + "\t" + absoluteTime);


    }

}
