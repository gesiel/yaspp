package parser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gesiel on 02/09/15.
 */
public class Parser {

    public static final String SEMI_COLON = ";";
    public static final String NEW_LINE = "\n";
    public static final List<Class> PRIMITIVE_CLASSES = Arrays.asList(boolean.class, Boolean.class, byte.class, Byte.class, char.class,
            Character.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class, float.class,
            Float.class, double.class, Double.class);

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
        Method[] methods = pojoClass.getMethods();

        try {
            OutputStream outputStream = factory.newOutputStream();
            if (noFieldsToWrite(fields) && noMethodsGetter(methods)) {
                closeOutputStream(outputStream);
                return outputStream;
            }

            writeTitleLine(outputStream, fields, methods);
            writePropertiesValuesLines(outputStream, fields, methods, pojos);

            closeOutputStream(outputStream);
            return outputStream;
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean noMethodsGetter(Method[] methods) {
        for (Method method : methods) {
            if (isGetter(method) && isPrimitive(method.getReturnType())) {
                return false;
            }
        }
        return true;
    }

    private void writePropertiesValuesLines(OutputStream outputStream, Field[] fields, Method[] methods, Object[] pojos) throws IOException, IllegalAccessException, InvocationTargetException {
        for (Object object : pojos) {
            newLine(outputStream);

            for (Field field : fields) {
                if (isPrimitive(field.getType())) {
                    writeColumn(outputStream, field.get(object).toString());
                }
            }

            for (Method method : methods) {
                if (isGetter(method) && isPrimitive(method.getReturnType())) {
                    writeColumn(outputStream, method.invoke(object).toString());
                }
            }
        }
    }

    private boolean isPrimitive(Class clazz) {
        return PRIMITIVE_CLASSES.contains(clazz);
    }

    private void writeTitleLine(OutputStream outputStream, Field[] fields, Method[] methods) throws IOException {
        for (Field field : fields) {
            if (isPrimitive(field.getType())) {
                writeColumn(outputStream, field.getName());
            }
        }

        for (Method method : methods) {
            if (isGetter(method) && isPrimitive(method.getReturnType())) {
                writeColumn(outputStream, cleanGetterName(method));
            }
        }
    }

    private boolean isGetter(Method method) {
        String methodName = method.getName();
        if (!methodName.startsWith("get")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        if (methodName.equals("getClass")) return false;
        return true;
    }

    private String cleanGetterName(Method method) {
        String methodName = method.getName();
        methodName = methodName.substring(3);
        methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
        return methodName;
    }

    private void closeOutputStream(OutputStream outputStream) throws IOException {
        outputStream.flush();
        outputStream.close();
    }

    private boolean noFieldsToWrite(Field[] fields) {
        for (Field field : fields) {
            if (isPrimitive(field.getType())) {
                return false;
            }
        }
        return true;
    }

    private void writeColumn(OutputStream outputStream, String fieldName) throws IOException {
        outputStream.write(fieldName.getBytes());
        outputStream.write(SEMI_COLON.getBytes());
    }

    private void newLine(OutputStream outputStream) throws IOException {
        outputStream.write(NEW_LINE.getBytes());
    }
}
