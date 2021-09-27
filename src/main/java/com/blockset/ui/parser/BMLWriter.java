package com.blockset.ui.parser;

import com.blockset.ui.parser.domain.BML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class BMLWriter {

    public static void write(BML bml, String bmlFileName) {
        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(BML.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(bml, writer);
            String result = writer.toString();
            File fileGen = new File(bmlFileName);
            FileWriter fileReader = new FileWriter(fileGen);
            fileReader.write(result);
            fileReader.close();
            System.out.println("Wrote to file " + fileGen.getName());
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

}
