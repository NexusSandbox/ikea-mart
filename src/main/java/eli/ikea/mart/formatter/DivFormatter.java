package eli.ikea.mart.formatter;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * A generalized formatter that constructs a border-less divider row of formatted text. This row will only contain a single line of text.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public class DivFormatter extends RowFormatter
{
    /**
     * Builder for creating new {@link DivFormatter} objects.
     */
    public static class Builder extends AbstractTestingBuilder<DivFormatter>
    {
        /**
         * @return A new non-null instance of {@link DivFormatter.Builder}.
         */
        public static Builder of()
        {
            return new DivFormatter.Builder();
        }

        private Builder()
        {
            super(new DivFormatter());
        }

        @SuppressWarnings("boxing")
        @Override
        public void build()
        {
            criteria.totalHeight = 1;

            if (criteria.totalWidth == 0)
            {
                criteria.totalWidth = getTotalWidth();
            }

            if (criteria.columnWidths.isEmpty())
            {
                criteria.columnWidths.addAll(getColumnWidths());
            }

            criteria.textLine = criteria.columnWidths.stream()
                                                     .map(width -> Strings.padEnd("", width, criteria.divider))
                                                     .collect(Collectors.joining(String.valueOf(criteria.joinerDivider)));
        }

        /**
         * @return The positive total number of columns in the row.
         */
        public int getColumnCount()
        {
            return criteria.columnWidths.size();
        }

        /**
         * @return The {@link List} of positive character widths for each column.
         */
        public List<Integer> getColumnWidths()
        {
            return ImmutableList.copyOf(criteria.columnWidths);
        }

        /**
         * @return The positive total number of lines for the row. Always 1 for divider rows.
         */
        public int getTotalHeight()
        {
            return 1;
        }

        /**
         * @return The positive total character width of the row.
         */
        public int getTotalWidth()
        {
            return criteria.totalWidth > 0 ? criteria.totalWidth : criteria.columnWidths.stream()
                                                                                        .mapToInt(Integer::intValue)
                                                                                        .sum() + getColumnCount() - 1;
        }

        @Override
        public void validate()
        {
            /**
             * <pre>
             * Verifier.verifyNotEmpty(criteria.columnWidths, "Unable to create a 0 width divider.");
             * </pre>
             */
        }

        /**
         * @param widths The {@link List} of character widths for each column. (Each value must be positive; Must match the total number of columns)
         * @return This non-null {@link DivFormatter.Builder}.
         */
        public Builder withColumnWidths(final List<Integer> widths)
        {
            criteria.columnWidths.clear();
            criteria.columnWidths.addAll(widths);

            return this;
        }

        /**
         * @param div The divider {@link Character} used for separating the row's horizontal borders.
         * @return This non-null {@link DivFormatter.Builder}.
         */
        public Builder withDivider(final char div)
        {
            criteria.divider = div;

            return this;
        }

        /**
         * @param joiner The joiner {@link Character} used for separating the row's vertical borders.
         * @return This non-null {@link DivFormatter.Builder}.
         */
        public Builder withJoinerDivider(final char joiner)
        {
            criteria.joinerDivider = joiner;

            return this;
        }

        /**
         * @param width The total character width of the row. (Must be positive)
         * @return This non-null {@link DivFormatter.Builder}.
         */
        public Builder withTotalWidth(final int width)
        {
            criteria.totalWidth = width;

            return this;
        }
    }

    private char divider = '-';

    private String textLine;

    private DivFormatter()
    {
        joinerDivider = '+';
        totalHeight = 1;
    }

    @Override
    public List<String> getLines()
    {
        return ImmutableList.of(textLine);
    }
}
