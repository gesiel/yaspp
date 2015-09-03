package parser;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by gesiel on 02/09/15.
 */
public class ParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWhenThereIsntOutputStream_shouldCreateANewOne() throws Exception {
        Parser parser = new Parser();
        assertThat(parser.outputStream, is(notNullValue()));

        parser = new Parser(null);
        assertThat(parser.outputStream, is(notNullValue()));
    }

    @Test
    public void testWhenThereIsAnExistingOutputStream_parseShouldReturnIts() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        Parser parser = new Parser(outputStream);
        assertThat(parser.parse(new Object[]{new Object()}), equalTo(outputStream));
    }

    @Test
    public void testWhenNullPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be null.");

        Parser parser = new Parser();
        parser.parse(null);
    }

    @Test
    public void testWhenEmptyPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be empty.");

        Parser parser = new Parser();
        parser.parse(new Object[] {});
    }
}
