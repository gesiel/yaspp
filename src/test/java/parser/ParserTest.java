package parser;

import oracle.jrockit.jfr.StringConstantPool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by gesiel on 02/09/15.
 */
public class ParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Parser parser;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        parser = new Parser(() -> outputStream);
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
        parser.parse(new PojoWithNoProperties());
        assertThat(outputStream.toByteArray().length, is(equalTo(0)));
    }

    @Test
    public void testWhenThereIsAPojoWithOnePublicAttr_shouldCreateAnOutputStreamWithOneColumn() throws Exception {
        parser.parse(new PojoWithOnePublicProperty());
        assertThat(outputStream.toString(), is(equalTo("propertyName;\n0;")));
    }


    @Test
    public void testWhenThereIsAPojoWithMoreThanOnePublicAttr_shouldCreateAnOutputStreamWithAllColumns() throws Exception {
        parser.parse(new PojoWithMoreThenOnePublicProperty());
        assertThat(outputStream.toString(), is(equalTo("intProperty;doubleProperty;doubleObjectProperty;integerObjectProperty;\n123456789;12345.12345;54321.54321;987654321;")));
    }

    @Test
    public void testWhenThereIsPrivateAttrs_shouldNotPrintThatField() throws Exception {
        parser.parse(new PojoWithMoreThenOnePublicPropertyAndSomePrivateFields());
        assertThat(outputStream.toString(), is(equalTo("intProperty;doubleProperty;doubleObjectProperty;integerObjectProperty;\n123456789;12345.12345;54321.54321;987654321;")));
    }

    @Test
    public void testWhenThereIsPublicGetterMethod_shouldPrintThatAsAField() throws Exception {
        parser.parse(new PojoWithMoreThenOnePublicPropertyAndSomePrivateFieldsWithPublicGetterMethods());
        assertThat(outputStream.toString(), is(equalTo("intProperty;doubleProperty;doubleObjectProperty;integerObjectProperty;integerPrivateObjectProperty;\n123456789;12345.12345;54321.54321;987654321;123456;")));
    }

    private class PojoWithNoProperties {

    }

    private class PojoWithOnePublicProperty {
        public int propertyName;
    }

    private class PojoWithMoreThenOnePublicProperty {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = 987654321;
    }

    private class PojoWithMoreThenOnePublicPropertyAndSomePrivateFields {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = 987654321;
        private String stringPrivateObjectProperty = "That's it";
        private Integer integerPrivateObjectProperty = 987654321;
    }

    private class PojoWithMoreThenOnePublicPropertyAndSomePrivateFieldsWithPublicGetterMethods {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = 987654321;
        private String stringPrivateObjectProperty = "That's it";
        private Integer integerPrivateObjectProperty = 123456;

        public Integer getIntegerPrivateObjectProperty() {
            return integerPrivateObjectProperty;
        }
    }
}
