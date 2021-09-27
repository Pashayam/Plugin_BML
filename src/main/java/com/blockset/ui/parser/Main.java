package com.blockset.ui.parser;

public class Main {

    public static void main(String[] args) {
        /*File file = new File("forum.bml");

        BML bmlUnm = null;

        try {
            FileReader reader = new FileReader(file);
            JAXBContext context = JAXBContext.newInstance(BML.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            bmlUnm = (BML) unmarshaller.unmarshal(reader);
            System.out.println("Read from file " + file.getName());
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }

        //писать результат сериализации будем в Writer(StringWriter)
        StringWriter writer = new StringWriter();

        //создание объекта Marshaller, который выполняет сериализацию
        try {
            JAXBContext context = JAXBContext.newInstance(BML.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(bmlUnm, writer);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        //преобразовываем в строку все записанное в StringWriter
        String result = writer.toString();

        File fileGen = new File("generate.bml");

        FileWriter fileReader;
        try {
            fileReader = new FileWriter(fileGen);
            fileReader.write(result);
            fileReader.close();
            System.out.println("Wrote to file " + fileGen.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
    }

}
