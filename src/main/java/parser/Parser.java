package parser;

import java.io.OutputStream;

/**
 * Created by gesiel on 04/09/15.
 */
public interface Parser {
    OutputStream parse(Object... pojos);
}
