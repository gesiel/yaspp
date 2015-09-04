package parser;

/**
 * Created by gesiel on 04/09/15.
 */
public class ParserFactory {

    public Parser newCSVParser(OutputStreamGateway gateway) {
        return new CSVParser(gateway);
    }

}
