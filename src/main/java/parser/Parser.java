package parser;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by gesiel on 02/09/15.
 */
public class Parser {

    protected OutputStream outputStream;

    public Parser() {
        outputStream = new ByteArrayOutputStream();
    }

    public Parser(OutputStream outputStream) {
        if (outputStream != null) this.outputStream = outputStream;
        else this.outputStream = new ByteArrayOutputStream();
    }

    public OutputStream parse(Object[] pojos) {
        if (pojos == null) throw new IllegalArgumentException("Illegal argument: pojo array cannot be null.");
        else if (pojos.length == 0) throw new IllegalArgumentException("Illegal argument: pojo array cannot be empty.");

        return outputStream;
    }
}
