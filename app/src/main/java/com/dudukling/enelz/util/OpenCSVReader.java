package com.dudukling.enelz.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.dudukling.enelz.dao.lpDAO;
import com.dudukling.enelz.ligProvActivity;
import com.dudukling.enelz.model.lpModel;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class OpenCSVReader {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void readCSVFile(Context context, String arquivoPath) throws IOException {

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        Path myPath = Paths.get(arquivoPath);
        try (
//                Reader reader = Files.newBufferedReader(Paths.get(arquivoPath));
//                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()

//                Reader reader = Files.newBufferedReader(Paths.get(arquivoPath));
//                CSVReader csvReader = new CSVReader(reader)

                BufferedReader br = Files.newBufferedReader(myPath,StandardCharsets.UTF_8);
                CSVReader csvReader = new CSVReaderBuilder(br).withSkipLines(1).withCSVParser(parser).build()
        ){
            String[] nextRecord;
            lpDAO dao = new lpDAO(context);

//            lpModel lp1 = new lpModel();
//            lp1.setOrdem("1231231231231");
//            lp1.setCliente("Erick lindo bla bla bla");
//            lp1.setEndereco("Rua da puta queu pariu que nao funciona nessa merda");
//            dao.insert(lp1);

            while ((nextRecord = csvReader.readNext()) != null) {
                lpModel lp = new lpModel();

                lp.setOrdem(nextRecord[1]);
                lp.setCliente(nextRecord[2]);
                lp.setEndereco(nextRecord[3]);

                dao.insert(lp);
            }
            dao.close();
        }
    }
}
