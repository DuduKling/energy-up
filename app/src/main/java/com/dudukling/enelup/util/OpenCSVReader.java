package com.dudukling.enelup.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.dudukling.enelup.dao.lpDAO;
import com.dudukling.enelup.model.lpModel;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;


public class OpenCSVReader {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void readCSVFile(Context context, String arquivoPath) throws IOException {
        Path myPath = Paths.get(arquivoPath);
        File fileDir = new File(String.valueOf(myPath));
        String arquivoExtensao = fileDir.getAbsolutePath().substring(fileDir.getAbsolutePath().lastIndexOf("."));

        CSVParser parser;
        switch (arquivoExtensao) {
            case ".csv":
                parser = new CSVParserBuilder().withSeparator(';').build();
                break;
            case ".txt":
                parser = new CSVParserBuilder().withSeparator('\t').build();
                break;
            default:
                return;
        }

        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileDir), "UTF8"));
                CSVReader csvReader = new CSVReaderBuilder(br).withSkipLines(1).withCSVParser(parser).build()
        ){
            String[] nextRecord;
            lpDAO dao = new lpDAO(context);

            while ((nextRecord = csvReader.readNext()) != null) {
                lpModel lp = new lpModel();


                lp.setOrdem(stripAccents(nextRecord[1]));
                lp.setCliente(stripAccents(nextRecord[2]));
                lp.setEndereco(stripAccents(nextRecord[3]));

                lp.setNumero_cliente(stripAccents(nextRecord[0]));
                lp.setBairro(stripAccents(nextRecord[4]));
                lp.setDescricao_etapa(stripAccents(nextRecord[12]));
                lp.setObservacoes(stripAccents(nextRecord[14]));

                lp.setTipoOrdem(stripAccents(nextRecord[6]));
                lp.setEtapa(stripAccents(nextRecord[11]));
                lp.setLocalidade(stripAccents(nextRecord[36]));

                lp.setLatitude(stripAccents(nextRecord[37]));
                lp.setLongitude(stripAccents(nextRecord[38]));


                dao.insert(lp);
            }
            dao.close();
        }
    }

    private static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
