package parser;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by gesiel on 02/09/15.
 */
public class ParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Parser parser;

    @Before
    public void setUp() throws Exception {
        parser = new Parser();
    }

    @Test
    public void testWhenThereIsntOutputStream_shouldCreateANewOne() throws Exception {
        assertThat(parser.outputStream, is(notNullValue()));
    }

    @Test
    public void testWhenNullPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be null.");
        parser.parse(null);
    }

    @Test
    public void testWhenEmptyPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be empty.");
        parser.parse();
    }

    @Test
    public void testWhenThereIsAPojoWithNoAttr_shouldCreateAnEmptyOutputStream() throws Exception {
        parser.parse(new SimplePojo());
        assertThat(parser.outputStream.toByteArray().length, is(equalTo(0)));
    }

    @Test
    public void testWheThereIsJustOneAttr_shouldCreateAnOutputStreamWithOneColumnAndTwoLines() throws Exception {
        parser.parse(new Pojo());
        assertThat(parser.outputStream.toString(), is(equalTo("propertyName;\n0;")));
    }

    private class SimplePojo {

    }

    private class Pojo {
        public int propertyName;
    }
}
