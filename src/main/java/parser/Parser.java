package parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by gesiel on 02/09/15.
 */
public class Parser {

    protected ByteArrayOutputStream outputStream;

    public Parser() {
        outputStream = new ByteArrayOutputStream();
    }

    public OutputStream parse(Object[] pojos) {
        if (pojos == null) throw new IllegalArgumentException("Illegal argument: pojo array cannot be null.");
        else if (pojos.length == 0) throw new IllegalArgumentException("Illegal argument: pojo array cannot be empty.");

        Object first = pojos[0];
        Class pojoClass = first.getClass();
        Field[] fields = pojoClass.getFields();

        if (fields.length == 0) return outputStream;

        try {
            outputStream.write((fields[0].getName() + ";").getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write((fields[0].get(first) + ";").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        return outputStream;
    }
}
