package com.example.lab5.manual.functions.io;

import com.example.lab5.manual.functions.tabulated.TabulatedFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Сериализация и десериализация табулированных функций.
 */
public final class FunctionsIO {
    private FunctionsIO() {
    }

    public static void serialize(TabulatedFunction function, OutputStream outputStream) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(function);
        }
    }

    public static TabulatedFunction deserialize(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            Object obj = ois.readObject();
            if (obj instanceof TabulatedFunction) {
                return (TabulatedFunction) obj;
            }
            throw new IOException("Некорректный формат файла с табулированной функцией");
        }
    }
}
