package com.example.lab5.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Утилиты сериализации/десериализации табулированных функций
 * в бинарном, XML и JSON форматах.
 */
public final class FunctionsIO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private FunctionsIO() {
    }

    public static void serializeBinary(TabulatedFunction function, OutputStream outputStream) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(outputStream)) {
            double[] xValues = function.getXValues();
            double[] yValues = function.getYValues();
            dos.writeInt(xValues.length);
            for (int i = 0; i < xValues.length; i++) {
                dos.writeDouble(xValues[i]);
                dos.writeDouble(yValues[i]);
            }
        }
    }

    public static TabulatedFunction deserializeBinary(InputStream inputStream) throws IOException {
        try (DataInputStream dis = new DataInputStream(inputStream)) {
            int count = dis.readInt();
            if (count < 2) {
                throw new IllegalArgumentException("Файл функции должен содержать минимум две точки");
            }
            double[] xValues = new double[count];
            double[] yValues = new double[count];
            for (int i = 0; i < count; i++) {
                xValues[i] = dis.readDouble();
                yValues[i] = dis.readDouble();
            }
            return new TabulatedFunction(xValues, yValues);
        }
    }

    public static void serializeJson(TabulatedFunction function, OutputStream outputStream) throws IOException {
        FileModel model = FileModel.from(function);
        MAPPER.writeValue(outputStream, model);
    }

    public static TabulatedFunction deserializeJson(InputStream inputStream) throws IOException {
        FileModel model = MAPPER.readValue(inputStream, FileModel.class);
        model.validate();
        return model.toFunction();
    }

    public static void serializeXml(TabulatedFunction function, OutputStream outputStream) throws IOException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("tabulatedFunction");
            document.appendChild(root);

            Element points = document.createElement("points");
            root.appendChild(points);

            double[] xValues = function.getXValues();
            double[] yValues = function.getYValues();
            for (int i = 0; i < xValues.length; i++) {
                Element point = document.createElement("point");
                Element x = document.createElement("x");
                x.setTextContent(Double.toString(xValues[i]));
                Element y = document.createElement("y");
                y.setTextContent(Double.toString(yValues[i]));
                point.appendChild(x);
                point.appendChild(y);
                points.appendChild(point);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Не удалось сериализовать функцию в XML", e);
        }
    }

    public static TabulatedFunction deserializeXml(InputStream inputStream) throws IOException {
        try {
            byte[] data = inputStream.readAllBytes();
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(data));
            NodeList pointNodes = document.getElementsByTagName("point");
            if (pointNodes.getLength() < 2) {
                throw new IllegalArgumentException("XML должен содержать как минимум две точки");
            }
            double[] xValues = new double[pointNodes.getLength()];
            double[] yValues = new double[pointNodes.getLength()];
            for (int i = 0; i < pointNodes.getLength(); i++) {
                Element point = (Element) pointNodes.item(i);
                String xText = getChildText(point, "x");
                String yText = getChildText(point, "y");
                xValues[i] = Double.parseDouble(xText);
                yValues[i] = Double.parseDouble(yText);
            }
            return new TabulatedFunction(xValues, yValues);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Не удалось прочитать XML функции", e);
        }
    }

    private static String getChildText(Element parent, String childName) {
        NodeList list = parent.getElementsByTagName(childName);
        if (list.getLength() == 0) {
            throw new IllegalArgumentException("Отсутствует обязательный элемент " + childName);
        }
        return list.item(0).getTextContent();
    }

    private record FileModel(String name, double[] xValues, double[] yValues) {
        static FileModel from(TabulatedFunction function) {
            return new FileModel(null, function.getXValues(), function.getYValues());
        }

        void validate() {
            if (xValues == null || yValues == null) {
                throw new IllegalArgumentException("Файл должен содержать массивы X и Y");
            }
            if (xValues.length != yValues.length || xValues.length < 2) {
                throw new IllegalArgumentException("Количество точек должно быть не менее двух и совпадать для X и Y");
            }
        }

        TabulatedFunction toFunction() {
            validate();
            return new TabulatedFunction(xValues, yValues);
        }
    }
}
