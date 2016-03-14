package br.unifor.sd.zipper.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ney on 3/10/16.
 */
public class Compactador {

    //Constantes
    static final int TAMANHO_BUFFER = 8192; // 8kb

    //método para compactar arquivo
    public static void compactarParaZip(File arqEntrada) throws IOException {

        int cont;
        byte[] dados = new byte[TAMANHO_BUFFER];
        BufferedInputStream origem = null;
        FileInputStream streamDeEntrada = null;
        FileOutputStream destino = null;
        ZipOutputStream saida = null;
        ZipEntry entry = null;

        try {

            destino = new FileOutputStream(new File(arqEntrada.getAbsoluteFile() + ".zip"));
            saida = new ZipOutputStream(new BufferedOutputStream(destino));
            streamDeEntrada = new FileInputStream(arqEntrada);
            origem = new BufferedInputStream(streamDeEntrada, TAMANHO_BUFFER);
            entry = new ZipEntry(arqEntrada.getName());
            saida.putNextEntry(entry);

            while ((cont = origem.read(dados, 0, TAMANHO_BUFFER)) != -1) {
                saida.write(dados, 0, cont);
            }
            origem.close();
            saida.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

}