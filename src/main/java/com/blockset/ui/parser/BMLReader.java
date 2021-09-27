package com.blockset.ui.parser;

import com.blockset.ui.parser.domain.BML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class BMLReader {

    public static BML read(String bmlFileName) throws FileNotFoundException {
        if (bmlFileName != null && bmlFileName.endsWith(".bml")) {
            File file = new File(bmlFileName);
            BML bml = null;
            try {
                FileReader reader = new FileReader(file);
                JAXBContext context = JAXBContext.newInstance(BML.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                bml = (BML) unmarshaller.unmarshal(reader);
                System.out.println("Read from file " + file.getName());
            } catch (JAXBException | FileNotFoundException e) {
                e.printStackTrace();
            }
            return bml;
        } else
            throw new FileNotFoundException(bmlFileName);
    }

}
