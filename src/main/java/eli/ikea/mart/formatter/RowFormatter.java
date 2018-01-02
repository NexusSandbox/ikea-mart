package eli.ikea.mart.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A generalized formatter that constructs a border-less row of formatted text. This row may contain multiple lines of text.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public class RowFormatter implements TextFormatter
{
    /**
     * Builder for creating new {@link RowFormatter} objects.
     */
    public static class Builder extends AbstractTestingBuilder<RowFormatter>
    {
        /**
         * @return A new non-null instance of {@link RowFormatter.Builder}.
         */
        public static Builder of()
        {
            return new RowFormatter.Builder();
        }

        /**
         * @param cellLines The {@link List} of {@link String lines} to populate the columns of the row.
         * @return A new non-null instance of {@link RowFormatter.Builder} with the provided {@link List} of {@link String lines}.
         */
        public static Builder of(final List<String> cellLines)
        {
            return of().withCells(CellFormatter.Builder.of(cellLines));
        }

        /**
         * @param cellLines The array of {@link String lines} to populate the columns of the row.
         * @return A new non-null instance of {@link RowFormatter.Builder} with the provided array of {@link String lines}.
         */
        public static Builder of(final String ... cellLines)
        {
            return of().withCells(CellFormatter.Builder.of(cellLines));
        }

        /**
         * @param cells The array of {@link CellFormatter.Builder cells} to populate the columns of the row.
         * @return A new non-null instance of {@link RowFormatter.Builder} with the provided array of {@link CellFormatter.Builder cells}.
         */
        public static Builder ofCells(final CellFormatter.Builder ... cells)
        {
            return of().withCells(cells);
        }

        /**
         * @param cells The {@link List} of {@link CellFormatter.Builder cells} to populate the columns of the row.
         * @return A new non-null instance of {@link RowFormatter.Builder} with the provided {@link List} of {@link CellFormatter.Builder cells}.
         */
        public static Builder ofCells(final List<CellFormatter.Builder> cells)
        {
            return of().withCells(cells);
        }

        private final List<CellFormatter.Builder> rowCells = Lists.newArrayListWithExpectedSize(0);

        private Builder()
        {
            super(new RowFormatter());
        }

        /**
         * @param cells The array of {@link CellFormatter.Builder cells} to append the columns of the row. (May be empty)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder addCells(final CellFormatter.Builder ... cells)
        {
            if (cells == null)
            {
                return this;
            }

            final List<CellFormatter.Builder> columnCells = Lists.newArrayList(cells);
            return addCells(columnCells);
        }

        /**
         * @param cells The {@link List} of {@link CellFormatter.Builder cells} to append the columns of the row. (May be empty)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        @SuppressWarnings("boxing")
        public Builder addCells(final List<CellFormatter.Builder> cells)
        {
            final List<CellFormatter.Builder> columnCells = TextFormatter.cleanList(cells);
            if (columnCells == null)
            {
                return this;
            }
            rowCells.addAll(columnCells);
            criteria.columnWidths.addAll(columnCells.stream().sequential().map(cell -> cell.getTotalWidth()).collect(Collectors.toList()));

            return this;
        }

        @SuppressWarnings("boxing")
        @Override
        public void build()
        {
            // Set the totalHeight if not already set
            if (criteria.totalHeight == 0)
            {
                criteria.totalHeight = getTotalHeight();
            }

            // Set the totalWidth if not already set
            if (criteria.totalWidth == 0)
            {
                criteria.totalWidth = getTotalWidth();
            }

            // Set the column widths if not already set
            if (criteria.columnWidths.isEmpty())
            {
                criteria.columnWidths.addAll(getColumnWidths());
            }

            // Set the defined width and height for each cell in the row
            IntStream.range(0, criteria.columnWidths.size()).forEach(i -> {
                final CellFormatter.Builder cell = rowCells.get(i);
                cell.withTotalSize(getTotalHeight(), criteria.columnWidths.get(i));
            });

            // Finalize each cell in the row
            final List<CellFormatter> cells = rowCells.stream().map(cell -> cell.finish()).collect(Collectors.toList());

            final List<List<String>> blocks = cells.stream().sequential().map(cell -> cell.getLines()).collect(Collectors.toList());

            criteria.textLines.addAll(IntStream.range(0, criteria.totalHeight)
                                               .boxed()
                                               .map(i -> blocks.stream()
                                                               .sequential()
                                                               .map(lines -> lines.get(i))
                                                               .collect(Collectors.joining(String.valueOf(criteria.joinerDivider))))
                                               .collect(Collectors.toList()));
        }

        /**
         * @return The non-null, non-empty {@link ImmutableList} of {@link CellFormatter.Builder cells} for the row.
         */
        public List<CellFormatter.Builder> getCells()
        {
            return ImmutableList.copyOf(rowCells);
        }

        /**
         * @return The positive total number of columns in the row.
         */
        public int getColumnCount()
        {
            return rowCells.size();
        }

        /**
         * @return The {@link List} of positive character widths for each column.
         */
        @SuppressWarnings("boxing")
        public List<Integer> getColumnWidths()
        {
            return rowCells.stream().sequential().map(cell -> cell.getTotalWidth()).collect(Collectors.toList());
        }

        /**
         * @return The positive total number of lines for the row.
         */
        public int getTotalHeight()
        {
            return criteria.totalHeight > 0 ? criteria.totalHeight : rowCells.stream().mapToInt(cell -> cell.getTotalHeight()).max().getAsInt();
        }

        /**
         * @return The positive total character width of the row.
         */
        public int getTotalWidth()
        {
            return criteria.totalWidth > 0 ? criteria.totalWidth : rowCells.stream()
                                                                           .mapToInt(cell -> cell.getTotalWidth())
                                                                           .sum() + getColumnCount() - 1;
        }

        @Override
        public void validate()
        {
            /**
             * <pre>
             * Verifier.verifyNotEmpty(rowCells, "Unable to create a row with no columns.");
             * </pre>
             */

            // Validate columnWidths and counts match
            /**
             * <pre>
             * Verifier.verifyTrue(criteria.columnWidths.isEmpty() || rowCells.size() == criteria.columnWidths.size(),
             *                     "Unable to align cell columns with defined column widths.");
             * </pre>
             */

            if (!criteria.columnWidths.isEmpty())
            {
                /**
                 * <pre>
                 * IntStream.range(0, criteria.columnWidths.size())
                 *          .forEach(i -> Verifier.verifyTrue(criteria.columnWidths.get(i) >= rowCells.get(i).getTotalWidth(),
                 *                                            MessageFormat.format("Unable to fit < cellIndex={0} > text with < cellWidth={1} > into < columnWidth={2} >.",
                 *                                                                 i,
                 *                                                                 rowCells.get(i).getTotalWidth(),
                 *                                                                 criteria.columnWidths.get(i))));
                 * </pre>
                 */
            }

            // Validate cell heights
            /**
             * <pre>
             * final int maxHeight = rowCells.stream().mapToInt(cell -> cell.getTotalHeight()).max().getAsInt();
             * Verifier.verifyTrue(criteria.totalHeight == 0 || criteria.totalHeight >= maxHeight,
             *                     MessageFormat.format("Unable to fit text with < maxHeight={0} > and < totalHeight={1} >.",
             *                                          maxHeight,
             *                                          criteria.totalHeight));
             * </pre>
             */

            // Validate cell widths
            /**
             * <pre>
             * final int maxWidth = rowCells.stream().mapToInt(cell -> cell.getTotalWidth()).sum() + rowCells.size() - 1;
             * Verifier.verifyTrue(criteria.totalWidth == 0 || criteria.totalWidth >= maxWidth,
             *                     MessageFormat.format("Unable to fit text with < maxWidth={0} > and < totalWidth={1} >.",
             *                                          maxWidth,
             *                                          criteria.totalWidth));
             * </pre>
             */
        }

        /**
         * @param align The character {@link TextFormatter.Alignment} for all cell's text in the row. (Cannot be null)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withAlignment(final Alignment align)
        {
            rowCells.stream().forEach(cell -> cell.withAlignment(align));

            return this;
        }

        /**
         * @param cells The array of {@link CellFormatter.Builder cells} to populate the columns of the row.
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withCells(final CellFormatter.Builder ... cells)
        {
            if (cells == null || cells.length == 0)
            {
                return this;
            }

            final List<CellFormatter.Builder> columnCells = Lists.newArrayList(cells);
            return withCells(columnCells);
        }

        /**
         * @param cells The {@link List} of {@link CellFormatter.Builder cells} to populate the columns of the row.
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withCells(final List<CellFormatter.Builder> cells)
        {
            final List<CellFormatter.Builder> columnCells = TextFormatter.cleanList(cells);
            if (columnCells == null || columnCells.isEmpty())
            {
                return this;
            }
            rowCells.clear();
            criteria.columnWidths.clear();

            return addCells(columnCells);
        }

        /**
         * @param widths The {@link List} of character widths for each column. (Each value must be positive; Must match the total number of columns)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withColumnWidths(final List<Integer> widths)
        {
            criteria.columnWidths.clear();
            criteria.columnWidths.addAll(widths);

            return this;
        }

        /**
         * @param horizontalCharacterCount The minimum number of empty columns to space between vertical cell borders for all cells in the row. This
         *            will fill with the character columns with the {@link CellFormatter.Builder#withPaddingSpacer(char) padding spacer character}.
         *            Default ' '. (Must be non-negative)
         * @param verticalLineCount The minimum number of empty rows to space between horizontal cell borders for all cells in the row. This will fill
         *            the lines with the {@link CellFormatter.Builder#withPaddingSpacer(char) padding spacer character}. Default ' '. (Must be
         *            non-negative)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withPadding(final int horizontalCharacterCount, final int verticalLineCount)
        {
            rowCells.stream().forEach(cell -> cell.withPadding(horizontalCharacterCount, verticalLineCount));

            return this;
        }

        /**
         * @param joiner The joiner {@link Character} used for separating the row's vertical borders.
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withJoinerDivider(final char joiner)
        {
            criteria.joinerDivider = joiner;

            return this;
        }

        /**
         * @param height The total number of lines for the row. (Must be positive)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withTotalHeight(final int height)
        {
            criteria.totalHeight = height;

            return this;
        }

        /**
         * @param width The total character width of the row. (Must be positive)
         * @return This non-null {@link RowFormatter.Builder}.
         */
        public Builder withTotalWidth(final int width)
        {
            criteria.totalWidth = width;

            return this;
        }
    }

    /**
     * The {@link List} of pre-formatted lines of text for the entrie row.
     */
    protected final List<String> textLines = new ArrayList<>(0);

    /**
     * The {@link List} of positive character widths for each column.
     */
    protected final List<Integer> columnWidths = new ArrayList<>(0);
    /**
     * The positive total number of lines for the row.
     */
    protected int                 totalHeight  = 0;
    /**
     * The positive total character width of the row.
     */
    protected int                 totalWidth   = 0;

    /**
     * The joiner {@link Character} used for separating the row's vertical borders.
     */
    protected char joinerDivider = '|';

    /**
     * @return The {@link List} of positive character widths for each column.
     */
    public List<Integer> getColumnWidths()
    {
        return ImmutableList.copyOf(columnWidths);
    }

    @Override
    public List<String> getLines()
    {
        return ImmutableList.copyOf(textLines);
    }

    /**
     * @return The positive total number of lines for the row.
     */
    public int getTotalHeight()
    {
        return totalHeight;
    }

    /**
     * @return The positive total character width of the row.
     */
    public int getTotalWidth()
    {
        return totalWidth;
    }
}
