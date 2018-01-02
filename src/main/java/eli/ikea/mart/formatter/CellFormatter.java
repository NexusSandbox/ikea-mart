package eli.ikea.mart.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A generalized formatter that constructs a block of formatted text. This block may optionally be padded with an arbitrary number of empty rows or
 * columns of text.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public class CellFormatter implements TextFormatter
{
    /**
     * Builder for creating new {@link CellFormatter} objects.
     */
    public static class Builder extends AbstractTestingBuilder<CellFormatter>
    {
        /**
         * @return A new non-null instance of {@link CellFormatter.Builder}.
         */
        public static Builder of()
        {
            return new CellFormatter.Builder();
        }

        /**
         * @param lines The {@link List} of {@link String lines} of text to populate a new {@link List} of {@link CellFormatter.Builder cells}.
         * @return A new non-null instance of {@link CellFormatter.Builder}.
         */
        public static List<Builder> of(final List<String> lines)
        {
            /**
             * <pre>
             * Verifier.verifyNotNull(lines, "lines: null.");
             * Verifier.verifyNoNullValues(lines, "lines: Contains null values.");
             * </pre>
             */

            return lines.stream().map(s -> of(s)).collect(Collectors.toList());
        }

        /**
         * @param line The {@link String line} of text to populate a new {@link CellFormatter.Builder cell}.
         * @return A new non-null instance of {@link CellFormatter.Builder}.
         */
        public static Builder of(final String line)
        {
            /**
             * <pre>
             * Verifier.verifyNotNull(line, "line: null.");
             * </pre>
             */

            return of().withLines(line);
        }

        /**
         * @param lines The array of {@link String lines} of text to populate a new {@link List} of {@link CellFormatter.Builder cells}.
         * @return A new non-null, non-empty {@link List} of {@link CellFormatter.Builder cells}.
         */
        public static List<Builder> of(final String ... lines)
        {
            /**
             * <pre>
             * Verifier.verifyNotNull(lines, "lines: null.");
             * </pre>
             */

            return of(Lists.newArrayList(lines));
        }

        private Builder()
        {
            super(new CellFormatter());
        }

        /**
         * @param lines The {@link List} of {@link String lines} to append the rows of the {@link CellFormatter}.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder addLines(final List<String> lines)
        {
            final List<String> cellLines = TextFormatter.cleanList(lines);
            if (cellLines == null)
            {
                return this;
            }
            for (final String cellLine : cellLines)
            {
                final List<String> rowLines = TextFormatter.sanitize(cellLine);
                criteria.textLines.addAll(rowLines);
                criteria.adjustTotalLineWidth(rowLines);
            }

            return this;
        }

        /**
         * @param lines The array of {@link String lines} to append the rows of the {@link CellFormatter}.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder addLines(final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> cellLines = Lists.newArrayList(lines);
            return addLines(cellLines);
        }

        @Override
        public void build()
        {
            if (criteria.totalHeight == 0)
            {
                criteria.totalHeight = getTotalHeight();
            }
            if (criteria.totalWidth == 0)
            {
                criteria.totalWidth = getTotalWidth();
            }

            final List<String> lines = new ArrayList<>(criteria.totalHeight);

            // Create padding for vertical pad lines
            final String padLine = Strings.repeat(String.valueOf(criteria.paddingSpacer), criteria.totalWidth);

            // Add vertical padding
            lines.addAll(IntStream.range(0, criteria.verticalPaddingCount).boxed().map(i -> padLine).collect(Collectors.toList()));

            // Create padding for horizontal pad columns
            final String horizontalPad = Strings.repeat(String.valueOf(criteria.paddingSpacer), criteria.horizontalPaddingCount);

            //
            final int fillerWidth = criteria.totalWidth - 2 * criteria.horizontalPaddingCount;
            for (final String line : criteria.textLines)
            {
                final String middleSegment = criteria.align.paddify(line, fillerWidth, criteria.fillingSpacer);
                lines.add(String.format("%s%s%s", horizontalPad, middleSegment, horizontalPad));
            }

            // Fill in any remaining empty lines if height is larger than the total count of lines
            final int lineBuffer = criteria.totalHeight - criteria.textLines.size() - 2 * criteria.verticalPaddingCount;
            final String emptyPadLine = String.format("%s%s%s",
                                                      horizontalPad,
                                                      Strings.padEnd("", fillerWidth, criteria.fillingSpacer),
                                                      horizontalPad);
            lines.addAll(IntStream.range(0, lineBuffer).boxed().map(i -> emptyPadLine).collect(Collectors.toList()));

            // Add vertical padding
            lines.addAll(IntStream.range(0, criteria.verticalPaddingCount).boxed().map(i -> padLine).collect(Collectors.toList()));

            // Reset and re-initialize cell text lines
            criteria.textLines.clear();
            criteria.textLines.addAll(lines);
        }

        /**
         * @return The positive total number of lines for the cell.
         */
        public int getTotalHeight()
        {
            return criteria.textLines.size() + 2 * criteria.verticalPaddingCount;
        }

        /**
         * @return The positive total character width of the cell.
         */
        public int getTotalWidth()
        {
            return criteria.lineWidth + 2 * criteria.horizontalPaddingCount;
        }

        @Override
        public void validate()
        {
            // Verify text can fit in cell
            /**
             * <pre>
             * Verifier.verifyTrue(criteria.totalHeight == 0 || criteria.totalHeight >= criteria.textLines.size() + 2 * criteria.verticalPaddingCount,
             *                     MessageFormat.format("Unable to fit text with < totalLineCount={0} > and < verticalPadding={1} > into a < totalHeight={2} >.",
             *                                          criteria.textLines.size(),
             *                                          criteria.verticalPaddingCount,
             *                                          criteria.totalHeight));
             * Verifier.verifyTrue(criteria.totalWidth == 0 || criteria.totalWidth >= criteria.lineWidth + 2 * criteria.horizontalPaddingCount,
             *                     MessageFormat.format("Unable to fit text with < totalCharacterWidth={0} > and < horizontalPadding={1} > into a < totalWidth={2} >.",
             *                                          criteria.lineWidth,
             *                                          criteria.horizontalPaddingCount,
             *                                          criteria.totalWidth));
             * </pre>
             */
        }

        /**
         * @param align The character {@link TextFormatter.Alignment} for the cell's text. (Cannot be null)
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withAlignment(final Alignment align)
        {
            criteria.align = align;

            return this;
        }

        /**
         * @param filler The {@link Character} used to fill the spacing between the raw-text and the cell padding.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withFillingSpacer(final char filler)
        {
            criteria.fillingSpacer = filler;

            return this;
        }

        /**
         * @param horizontalCharacterCount The minimum number of empty columns to space between vertical cell borders. This will fill with the
         *            character columns with the {@link #withPaddingSpacer(char) padding spacer character}. Default ' '. (Must be non-negative)
         * @param verticalLineCount The minimum number of empty rows to space between horizontal cell borders. This will fill the lines with the
         *            {@link #withPaddingSpacer(char) padding spacer character}. Default ' '. (Must be non-negative)
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withPadding(final int horizontalCharacterCount, final int verticalLineCount)
        {
            criteria.horizontalPaddingCount = horizontalCharacterCount;
            criteria.verticalPaddingCount = verticalLineCount;

            return this;
        }

        /**
         * @param lines The {@link List} of {@link String lines} to populate the rows of the {@link CellFormatter}.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withLines(final List<String> lines)
        {
            final List<String> cellLines = TextFormatter.cleanList(lines);
            if (cellLines == null)
            {
                return this;
            }

            criteria.textLines.clear();
            return addLines(cellLines);
        }

        /**
         * @param lines The array of {@link String lines} to populate the rows of the {@link CellFormatter}.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withLines(final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> cellLines = Lists.newArrayList(lines);
            return withLines(cellLines);
        }

        /**
         * @param padding The {@link Character} used to pad the spacing between the block text and the cell borders. Default: ' '.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withPaddingSpacer(final char padding)
        {
            criteria.paddingSpacer = padding;

            return this;
        }

        /**
         * @param height The positive total number of lines for the cell.
         * @param width The positive total character width of the cell.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withTotalSize(final int height, final int width)
        {
            criteria.totalHeight = height;
            criteria.totalWidth = width;

            return this;
        }

    }

    private Alignment align                  = Alignment.LEFT;
    private char      fillingSpacer          = ' ';
    private int       horizontalPaddingCount = 0;
    private int       lineWidth              = 0;
    private char      paddingSpacer          = ' ';
    private int       totalHeight            = 0;
    private int       totalWidth             = 0;
    private int       verticalPaddingCount   = 0;

    private final List<String> textLines = new ArrayList<>(0);

    private void adjustTotalLineWidth(final List<String> lines)
    {
        final int maxWidth = TextFormatter.maxWidth(lines);
        if (maxWidth > lineWidth)
        {
            lineWidth = maxWidth;
        }
    }

    /**
     * @return The character {@link TextFormatter.Alignment} for the cell's text.
     */
    public Alignment getAlignment()
    {
        return align;
    }

    @Override
    public List<String> getLines()
    {
        return ImmutableList.copyOf(textLines);
    }

    /**
     * @return The positive total number of lines for the cell.
     */
    public int getTotalHeight()
    {
        return totalHeight;
    }

    /**
     * @return The positive total character width of the cell.
     */
    public int getTotalWidth()
    {
        return totalWidth;
    }
}
