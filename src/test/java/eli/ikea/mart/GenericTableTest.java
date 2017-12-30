package eli.ikea.mart;

import org.junit.Test;

import eli.ikea.mart.IHeaderColumn;
import eli.ikea.mart.Table;
import eli.veritas.exception.AssertionException;

/**
 * TODO Functional Description
 * 
 * @author The Architect
 */
public class GenericTableTest
{
    public static enum Columns implements IHeaderColumn
    {
        TEST1("Test1", String.class, true);

        private final String name;
        private final Class<?> dataType;
        private final boolean nullable;

        private Columns(final String name, final Class<?> dataType, final boolean nullable)
        {
            this.name = name;
            this.dataType = dataType;
            this.nullable = nullable;
        }

        public String getName()
        {
            return name;
        }

        public Class<?> getDataType()
        {
            return dataType;
        }

        public boolean isNullable()
        {
            return nullable;
        }
    }

    @Test
    public void testTableInitialization() throws AssertionException
    {
        final Table<Columns> table = Table.<Columns>create(Columns.class);
    }
}
