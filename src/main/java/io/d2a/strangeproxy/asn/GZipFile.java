/*
 * Copyright (c) 2019.
 * Created at 29.12.2019
 * ---------------------------------------------
 * @author hyWse
 * @see https://hywse.eu
 * ---------------------------------------------
 * If you have any questions, please contact
 * E-Mail: admin@hywse.eu
 * Discord: hyWse#0126
 */

package io.d2a.strangeproxy.asn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GZipFile {

    private String inputFile;
    private String outputFile;

    public GZipFile(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void unzip() {

        byte[] buffer = new byte[1024];

        try {

            System.out.println("[GZip] Unzipping '" + inputFile + "' to '" + outputFile + "' ...");

            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(inputFile));

            FileOutputStream out =
                    new FileOutputStream(outputFile);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

            System.out.println("[GZip] Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
