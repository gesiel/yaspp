package parser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by gesiel on 02/09/15.
 */
class CSVParser implements Parser {

    private static final Logger LOGGER = Logger.getLogger(CSVParser.class.getName());

    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";
    private static final List<Class> PRIMITIVE_CLASSES = Arrays.asList(boolean.class, Boolean.class, byte.class, Byte.class, char.class,
            Character.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class, float.class,
            Float.class, double.class, Double.class, String.class);
    
    private OutputStreamGateway gateway;

    public CSVParser(OutputStreamGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public final <T> OutputStream parse(final T... pojos) {
        validate(pojos);

        T first = pojos[0];
        Class pojoClass = first.getClass();
        Field[] fields = pojoClass.getFields();
        Method[] methods = pojoClass.getMethods();

        OutputStream outputStream = gateway.getOutputStream();
        if (noFieldsToWrite(fields) && noMethodsGetter(methods)) {
            closeOutputStream(outputStream);
            return outputStream;
        }

        writeTitleLine(outputStream, fields, methods);
        writePropertiesValuesLines(outputStream, fields, methods, pojos);

        closeOutputStream(outputStream);
        return outputStream;
    }

    private void validate(Object[] pojos) {
        LOGGER.info("Validating pojo objects");
        if (pojos == null) throw new IllegalArgumentException("Illegal argument: pojo array cannot be null.");
        else if (pojos.length == 0) throw new IllegalArgumentException("Illegal argument: pojo array cannot be empty.");
    }

    private boolean noMethodsGetter(Method[] methods) {
        LOGGER.info("checking getters methods of primitives");
        for (Method method : methods) {
            if (isGetter(method) && isPrimitive(method.getReturnType())) {
                return false;
            }
        }
        return true;
    }

    private void writePropertiesValuesLines(OutputStream outputStream, Field[] fields, Method[] methods, Object[] pojos) {
        LOGGER.info("writing fields and methods values");
        for (Object object : pojos) {
            newLine(outputStream);

            for (Field field : fields) {
                if (isPrimitive(field.getType())) {
                    writeColumn(outputStream, getFieldValue(field, object));
                }
            }

            for (Method method : methods) {
                if (isGetter(method) && isPrimitive(method.getReturnType())) {
                    writeColumn(outputStream, invokeMethod(method, object));
                }
            }
        }
    }

    private String invokeMethod(Method method, Object object) {
        try {
            Object returnValue = method.invoke(object);
            return returnValue == null ? "null" : returnValue.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFieldValue(Field field, Object object) {
        try {
            Object value = field.get(object);
            return value == null ? "null" : value.toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isPrimitive(Class clazz) {
        return PRIMITIVE_CLASSES.contains(clazz);
    }

    private void writeTitleLine(OutputStream outputStream, Field[] fields, Method[] methods) {
        LOGGER.info("writing fields names");
        for (Field field : fields) {
            if (isPrimitive(field.getType())) {
                writeColumn(outputStream, field.getName());
            }
        }

        LOGGER.info("writing getters methods names");
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

    private void closeOutputStream(OutputStream outputStream) {
        try {
            LOGGER.info("flushing and closing output stream");
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throwRuntime(e);
        }
    }

    private boolean noFieldsToWrite(Field[] fields) {
        LOGGER.info("checking public and primitives fields to write");
        for (Field field : fields) {
            if (isPrimitive(field.getType())) {
                return false;
            }
        }
        return true;
    }

    private void writeColumn(OutputStream outputStream, String fieldName) {
            write(outputStream, fieldName);
            write(outputStream, COMMA);
    }

    private void newLine(OutputStream outputStream) {
            write(outputStream, NEW_LINE);
    }

    private void write(OutputStream outputStream, String data)  {
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            throwRuntime(e);
        }
    }

    private void throwRuntime(IOException e) {
        throw new RuntimeException(e);
    }
}
