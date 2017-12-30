package eli.ikea.mart;

import eli.veritas.Verifier;
import eli.veritas.exception.AssertionException;

/**
 * Interface should be implemented by a table column {@link Enum Enumeration}.
 *
 * @author The Architect
 */
public interface IHeaderColumn
{
    /**
     * @return The formatted column name [Not null or empty].
     */
    String getName();

    /**
     * @return The compatible data type for the column's data [Not null].
     */
    Class<?> getDataType();

    /**
     * @return Indicator if <code>NULL</code> values are allowed for the column.
     */
    boolean isNullable();

    /**
     * @param type The enumerated type of the desired {@link IHeaderColumn}. [Non-Null]
     * @param fieldName The case-insensitive field name. [Non-Null; Not Empty; Not Blank]
     * @return the enumerated instance of the corresponding type matching the provided field name. [Nullable]
     * @throws AssertionException If any of the parameter conditions are not met.
     */
    static <E extends Enum<E> & IHeaderColumn> E matchField(final Class<E> type, final String fieldName) throws AssertionException
    {
        Verifier.assertNotNull("The enumerated type must not be null.", type);
        Verifier.Strings.assertNotBlank("Must match on a non-null, non-empty, non-blank field name.", fieldName);

        for (final E constant : type.getEnumConstants())
        {
            if (fieldName.equalsIgnoreCase(constant.getName()))
            {
                return constant;
            }
        }

        return null;
    }
}
