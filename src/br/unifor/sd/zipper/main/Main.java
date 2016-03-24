package br.unifor.sd.zipper.main;

import br.unifor.sd.zipper.manager.WorkManager;
import br.unifor.sd.zipper.orchestrator.WorkOrchestrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Easter egg
 */
public class Main {

    private static final String TAG = "Zipper";

    private static final String IGNORE_DOWNLOAD_PARAMETER = "--ignore-download";
    private static final String THREAD_MODE_PARAMETER = "--thread-mode";
    private static final String NUMBER_OF_RUNS_PARAMETER = "--number-of-runs";

    private static final String SINGLE_THREAD_VALUE = "single";
    private static final String MULTIPLE_THREAD_VALUE = "multiple";

    private static Boolean ignoreDownload = false;
    private static String threadMode = MULTIPLE_THREAD_VALUE;
    private static int numberOfRuns = 1;

    private static WorkOrchestrator workOrchestrator;

    public static void main(String[] args) {

        HashMap<String, String> parsedArgs = parseArgs(args);

        ignoreDownload = (parsedArgs.containsKey(IGNORE_DOWNLOAD_PARAMETER)?Boolean.parseBoolean(parsedArgs.get(IGNORE_DOWNLOAD_PARAMETER).toLowerCase()):false);
        threadMode = (parsedArgs.containsKey(THREAD_MODE_PARAMETER)?parsedArgs.get(THREAD_MODE_PARAMETER).toLowerCase():MULTIPLE_THREAD_VALUE);
        numberOfRuns = (parsedArgs.containsKey(NUMBER_OF_RUNS_PARAMETER)?Integer.parseInt(parsedArgs.get(NUMBER_OF_RUNS_PARAMETER)):1);

        if(!ignoreDownload){
            runSetupFilesScript();
        }

        workOrchestrator = new WorkOrchestrator(() -> runOrchestrator());

        runOrchestrator();

    }

    public static HashMap<String, String> parseArgs(String[] crudeArgs){

        HashMap<String, String> bakedArgs = new HashMap<>();

        for (int i=0; i<crudeArgs.length; i++) {

            String arg = crudeArgs[i].substring(0, crudeArgs[i].indexOf("="));

            String value = crudeArgs[i].substring(crudeArgs[i].indexOf("=") + 1);

            bakedArgs.put(arg, value);
            
        }

        return bakedArgs;

    }

    private static void runSetupFilesScript() {

        try{

            ProcessBuilder pb = new ProcessBuilder("./smart-downloader.sh");
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = null;

            while ((line = reader.readLine()) != null)
            {
                System.out.println("[" + TAG + "] " + line);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void runOrchestrator(){

        if(numberOfRuns > 0){
            numberOfRuns--;
            workOrchestrator.start((threadMode.equalsIgnoreCase(SINGLE_THREAD_VALUE) ? WorkManager.SINGLE_THREAD : WorkManager.MULTIPLE_THREADS));
        }else{
            System.out.println("[" + TAG + "] no more runs to go! Zipper finished!");
        }

    }

}
