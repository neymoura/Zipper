package br.unifor.sd.zipper.main;

import br.unifor.sd.zipper.orchestrator.WorkOrchestrator;

public class Main {

    public static void main(String[] args) {

        WorkOrchestrator workOrchestrator = new WorkOrchestrator();
        workOrchestrator.start();

    }

}
