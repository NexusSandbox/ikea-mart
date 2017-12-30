package eli.ikea.mart;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import eli.veritas.Verifier;
import eli.veritas.exception.AssertionException;

/**
 * The base generic abstract table that provides a framework for translating new tables from a data source. New instances of this class require
 * creating a new enumeration that implements {@link IHeaderColumn} that includes all columns in the expected sequence.
 *
 * @author The Architect
 */
public class Table<COLUMNS extends Enum<COLUMNS> & IHeaderColumn>
{
    protected final Class<COLUMNS>     columnsType;
    protected final Header             header;
    protected final ReferenceSequencer referenceSequencer;
    protected final Map<Long, Row>     rows;

    protected Table(final Class<COLUMNS> columnsType) throws AssertionException
    {
        this.columnsType = columnsType;
        this.header = new Header();
        this.referenceSequencer = ReferenceSequencer.create(100);
        rows = new HashMap<>();
    }

    public static <COLUMNS extends Enum<COLUMNS> & IHeaderColumn> Table<COLUMNS> create(final Class<COLUMNS> columnsType) throws AssertionException
    {
        Verifier.assertNotNull("Must specify a defined column enumeration.", columnsType);

        return new Table<>(columnsType);
    }

    public long insertRow(final Object[] columns) throws AssertionException
    {
        Verifier.Equality.assertEqual("Row column count must match header column count.", columns.length, header.getHeaderSize());

        final Row row = new Row(columns);
        final long referenceIdentifier = referenceSequencer.getNextReferenceIdentifier();
        rows.put(referenceIdentifier, row);

        return referenceIdentifier;
    }

    public class Header
    {
        protected final Map<COLUMNS, Integer> columnIndexByColumn;

        protected Header()
        {
            final COLUMNS[] columns = columnsType.getEnumConstants();
            columnIndexByColumn = new HashMap<>(columns.length);
            int index = 0;
            for (final COLUMNS column : columns)
            {
                columnIndexByColumn.put(column, index);
                index++;
            }
        }

        public int getHeaderSize()
        {
            return columnIndexByColumn.size();
        }

        public int getColumnIndex(final COLUMNS column) throws AssertionException
        {
            Verifier.assertNotNull("Must specify a valid column in order assign/retrieve a value to/from it.", column);

            return columnIndexByColumn.get(column);
        }
    }

    public class Row
    {
        protected final Object[] columnValues;

        protected Row(final Object[] columnValues) throws AssertionException
        {
            final COLUMNS[] columns = columnsType.getEnumConstants();
            int index = 0;
            for (final Object columnValue : columnValues)
            {
                validateColumnAssignment(columns[index], columnValue);
                index++;
            }

            this.columnValues = columnValues;
        }

        protected Row()
        {
            this.columnValues = new Object[header.getHeaderSize()];
        }

        @SuppressWarnings("unchecked")
        public <T> T getColumnValue(final COLUMNS column) throws AssertionException
        {
            final int columnIndex = header.getColumnIndex(column);

            return (T) columnValues[columnIndex];
        }

        public void setColumnValue(final COLUMNS column, final Object value) throws AssertionException
        {

            final int columnIndex = header.getColumnIndex(column);
            validateColumnAssignment(column, value);
            columnValues[columnIndex] = value;
        }

        private void validateColumnAssignment(final COLUMNS column, final Object columnValue) throws AssertionException
        {
            if (columnValue != null)
            {
                Verifier.Equality.assertEqual(MessageFormat.format("The non-null value's data type <{0}> must match the column's <{1}> data type <{2}>.",
                                                                   columnValue.getClass(),
                                                                   column,
                                                                   column.getDataType()),
                                              column.getDataType().isInstance(columnValue),
                                              true);
            }
            else
            {
                Verifier.Equality.assertEqual(MessageFormat.format("The column <{0}> must be nullable in order to assign a null value.", column),
                                              column.isNullable(),
                                              true);
            }
        }
    }
}
