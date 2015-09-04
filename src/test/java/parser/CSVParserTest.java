package parser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by gesiel on 02/09/15.
 */
public class CSVParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Parser CSVParser;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        CSVParser = new CSVParser(() -> outputStream);
    }

    @Test
    public void testWhenNullPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be null.");
        CSVParser.parse(null);
    }

    @Test
    public void testWhenEmptyPojoArray_shouldThrowIllegalArgumentException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal argument: pojo array cannot be empty.");
        CSVParser.parse();
    }

    @Test
    public void testWhenAPojoWithNoAttr_shouldCreateAnEmptyOutputStream() throws Exception {
        CSVParser.parse(new PojoWithNoProperties());
        assertThat(outputStream.toByteArray().length, is(equalTo(0)));
        assertThat(outputStream.toString(), is(equalTo("")));
    }

    @Test
    public void testWhenAPojoWithOnePublicAttr_shouldCreateAnOutputStreamWithOneColumn() throws Exception {
        CSVParser.parse(new PojoWithOnePublicProperty());
        assertThat(outputStream.toString(), is(equalTo("propertyName,\n0,")));
    }

    @Test
    public void testWhenAPojoWithMoreThanOnePublicAttr_shouldCreateAnOutputStreamWithAllColumns() throws Exception {
        CSVParser.parse(new PojoWithMoreThenOnePublicProperty());
        assertThat(outputStream.toString(), is(equalTo("intProperty,doubleProperty,doubleObjectProperty,integerObjectProperty,\n123456789,12345.12345,54321.54321,987654321,")));
    }

    @Test
    public void testWhenPrivateAttrs_shouldNotPrintThatField() throws Exception {
        CSVParser.parse(new PojoWithPrivateFields());
        assertThat(outputStream.toString(), is(equalTo("intProperty,doubleProperty,doubleObjectProperty,integerObjectProperty,\n123456789,12345.12345,54321.54321,987654321,")));
    }

    @Test
    public void testWhenPublicGetterMethod_shouldPrintThatAsAField() throws Exception {
        CSVParser.parse(new PojoWithPublicGetterMethod());
        assertThat(outputStream.toString(), is(equalTo("intProperty,doubleProperty,doubleObjectProperty,integerObjectProperty,integerPrivateObjectProperty,\n123456789,12345.12345,54321.54321,987654321,123456,")));
    }

    @Test
    public void testWhenPrivateGetterMethod_shouldNotPrintThat() throws Exception {
        CSVParser.parse(new PojoWithPrivateGetterMethod());
        assertThat(outputStream.toString(), is(equalTo("intProperty,doubleProperty,doubleObjectProperty,integerObjectProperty,integerPrivateObjectProperty,\n123456789,12345.12345,54321.54321,987654321,123456,")));
    }

    @Test
    public void testWhenPrivateComplexObjects_shouldNotPrintThat() throws Exception {
        CSVParser.parse(new PojoWithComplexType());
        assertThat(outputStream.toString(), is(equalTo("longPublicProperty,\n124,")));
    }

    @Test
    public void testWhenPrivateComplexGetter_shouldNotPrintThat() throws Exception {
        CSVParser.parse(new PojoWithGetterComplexType());
        assertThat(outputStream.toString(), is(equalTo("longPublicProperty,\n124,")));
    }

    @Test
    public void testWhenVoidMethodsLikeGetters_shouldNotPrintThat() throws Exception {
        CSVParser.parse(new PojoWithVoidMethods());
        assertThat(outputStream.toString(), is(equalTo("char,\nY,")));
    }

    @Test
    public void testWhenNoValidDataToPrint_shouldPrintNothing() throws Exception {
        CSVParser.parse(new PojoWithNoDataToPrint());
        assertThat(outputStream.toByteArray().length, is(equalTo(0)));
        assertThat(outputStream.toString(), is(equalTo("")));
    }

    @Test
    public void testWhenNullValues_shouldPrintNullWord() throws Exception {
        CSVParser.parse(new PojoWithNullValues());
        assertThat(outputStream.toString(), is(equalTo("intProperty,doubleProperty,doubleObjectProperty,integerObjectProperty,integerPrivateObjectProperty,stringPrivateObjectProperty,\n123456789,12345.12345,54321.54321,null,123456,null,")));
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

    private class PojoWithPrivateFields {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = 987654321;
        private String stringPrivateObjectProperty = "That's it";
        private Integer integerPrivateObjectProperty = 987654321;
    }

    private class PojoWithPublicGetterMethod {
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

    private class PojoWithPrivateGetterMethod {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = 987654321;
        private String stringPrivateObjectProperty = "That's it";
        private Integer integerPrivateObjectProperty = 123456;

        public Integer getIntegerPrivateObjectProperty() {
            return integerPrivateObjectProperty;
        }

        private String getStringPrivateObjectProperty() {
            return stringPrivateObjectProperty;
        }
    }

    private class PojoWithComplexType {

        public PojoWithNoProperties pojo;
        public Long longPublicProperty = 124l;

    }

    private class PojoWithGetterComplexType {

        private PojoWithNoProperties pojo;
        public Long longPublicProperty = 124l;

        public PojoWithNoProperties getPojo() {
            return pojo;
        }
    }

    private class PojoWithVoidMethods {
        public void someMethod() {

        }

        public void getSomething() {

        }

        public char getChar() {
            return 'Y';
        }
    }

    private class PojoWithNoDataToPrint {
        private char charAttr;

        private char getCharAttr() {
            return charAttr;
        }

        public char[] charArray;
    }

    private class PojoWithNullValues {
        public int intProperty = 123456789;
        public double doubleProperty = 12345.12345;
        public Double doubleObjectProperty = 54321.54321;
        public Integer integerObjectProperty = null;

        private String stringPrivateObjectProperty = null;
        private Integer integerPrivateObjectProperty = 123456;

        public Integer getIntegerPrivateObjectProperty() {
            return integerPrivateObjectProperty;
        }

        public String getStringPrivateObjectProperty() {
            return stringPrivateObjectProperty;
        }
    }
}
