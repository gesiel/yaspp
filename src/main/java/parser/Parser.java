package parser;

import java.io.OutputStream;

/**
 * Created by gesiel on 02/09/15.
 */
public class Parser {

    private OutputStream outputStream;

    public Parser() {

    }

    public Parser(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream parse(Object[] pojos) {
        if (pojos == null) throw new IllegalArgumentException("Illegal argument: pojo array cannot be null.");
        else if (pojos.length == 0) throw new IllegalArgumentException("Illegal argument: pojo array cannot be empty.");

        return outputStream;
    }
}
