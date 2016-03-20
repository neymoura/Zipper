package br.unifor.sd.zipper.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compactador {

    //Constantes
    static final int BUFFER = 2048;

    //método para compactar arquivo
    public static void compactarParaZip(File arqEntrada) throws IOException {

//        int cont;
//        byte[] dados = new byte[BUFFER];
//
//        BufferedInputStream origem = null;
//        FileInputStream streamDeEntrada = null;
//        FileOutputStream destino = null;
//        ZipOutputStream saida = null;
//        ZipEntry entry = null;
//
//        try {
//
//            destino = new FileOutputStream(new File(arqEntrada.getAbsoluteFile() + ".zip"));
//            saida = new ZipOutputStream(new BufferedOutputStream(destino));
//
//            streamDeEntrada = new FileInputStream(arqEntrada);
//            origem = new BufferedInputStream(streamDeEntrada, BUFFER);
//
//            entry = new ZipEntry(arqEntrada.getName());
//            saida.putNextEntry(entry);
//
//            while ((cont = origem.read(dados, 0, BUFFER)) != -1) {
//                saida.write(dados, 0, cont);
//            }
//
//        } catch (IOException e) {
//            throw new IOException(e.getMessage());
//        }

        try {

            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(new File(arqEntrada.getAbsoluteFile() + ".zip"));
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(arqEntrada);

            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry(arqEntrada.getName());

            out.putNextEntry(entry);

            int count;

            while((count = origin.read(data, 0,
                    BUFFER)) != -1) {
                out.write(data, 0, count);
            }

            origin.close();

            out.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}