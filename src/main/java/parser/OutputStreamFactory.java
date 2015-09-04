package parser;

import java.io.OutputStream;

/**
 * Created by gesiel on 03/09/15.
 */
public interface OutputStreamFactory {
    OutputStream newOutputStream();
}
