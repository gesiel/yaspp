package parser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Created by gesiel on 02/09/15.
 */
public class Parser {

    public static final String SEMI_COLON = ";";
    public static final String NEW_LINE = "\n";

    private OutputStreamFactory factory;

    public Parser(OutputStreamFactory factory) {
        this.factory = factory;
    }

    public OutputStream parse(final Object... pojos) {
        if (pojos == null) throw new IllegalArgumentException("Illegal argument: pojo array cannot be null.");
        else if (pojos.length == 0) throw new IllegalArgumentException("Illegal argument: pojo array cannot be empty.");

        Object first = pojos[0];
        Class pojoClass = first.getClass();
        Field[] fields = pojoClass.getFields();

        try {
            OutputStream outputStream = factory.newOutputStream();
            if (noFieldsToWrite(fields)) {
                closeOutputStream(outputStream);
                return outputStream;
            }

            writeTitleLine(outputStream, fields);
            writePropertiesValuesLines(outputStream, fields, pojos);

            closeOutputStream(outputStream);
            return outputStream;
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void writePropertiesValuesLines(OutputStream outputStream, Field[] fields, Object[] pojos) throws IOException, IllegalAccessException {
        for (Object object : pojos) {
            newLine(outputStream);

            for (Field field : fields) {
                writeColumn(outputStream, field.get(object).toString());
            }
        }
    }

    private void writeTitleLine(OutputStream outputStream, Field[] fields) throws IOException {
        for (Field field : fields) {
            writeColumn(outputStream, field.getName());
        }
    }

    private void closeOutputStream(OutputStream outputStream) throws IOException {
        outputStream.flush();
        outputStream.close();
    }

    private boolean noFieldsToWrite(Field[] fields) {
        return fields.length == 0;
    }

    private void writeColumn(OutputStream outputStream, String fieldName) throws IOException {
        outputStream.write(fieldName.getBytes());
        outputStream.write(SEMI_COLON.getBytes());
    }

    private void newLine(OutputStream outputStream) throws IOException {
        outputStream.write(NEW_LINE.getBytes());
    }
}
