package eli.ikea.mart.formatter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A generalized formatter that constructs a bordered table of formatted text. This table will align each of the columns to the maximum character
 * width, and each of the rows to the maximum line count.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public class TableFormatter implements TextFormatter
{
    /**
     * Builder for creating new {@link TableFormatter} objects.
     */
    public static class Builder extends AbstractTestingBuilder<TableFormatter>
    {
        /**
         * @return A new non-null instance of {@link TableFormatter.Builder}.
         */
        public static Builder of()
        {
            return new TableFormatter.Builder();
        }

        /**
         * @param bodyRows The {@link List} of {@link RowFormatter.Builder rows} to populate the body of the table. (May be empty)
         * @return A new non-null instance of {@link TableFormatter.Builder} with the provided {@link List} of {@link RowFormatter.Builder rows}.
         */
        public static Builder of(final List<RowFormatter.Builder> bodyRows)
        {
            return of().withBody(bodyRows);
        }

        /**
         * @param bodyRows The array of {@link RowFormatter.Builder rows} to populate the body of the table. (May be empty)
         * @return A new non-null instance of {@link TableFormatter.Builder} with the provided array of {@link RowFormatter.Builder rows}.
         */
        public static Builder of(final RowFormatter.Builder ... bodyRows)
        {
            return of().withBody(bodyRows);
        }

        private final String LINE_FORMAT = "{0}{1}{0}";

        private final List<RowFormatter.Builder> bodyRows     = new ArrayList<>(0);
        private final List<String>               captionLines = new ArrayList<>(0);
        private final List<RowFormatter.Builder> footerRows   = new ArrayList<>(0);
        private final List<RowFormatter.Builder> headerRows   = new ArrayList<>(0);
        private final List<String>               titleLines   = new ArrayList<>(0);

        private final DivFormatter.Builder horizontalBorder = DivFormatter.Builder.of();
        private final DivFormatter.Builder sectionBorder    = DivFormatter.Builder.of();

        private Builder()
        {
            super(new TableFormatter());
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to append the body of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addBody(final List<RowFormatter.Builder> rows)
        {
            final List<RowFormatter.Builder> cleanRows = TextFormatter.cleanList(rows);
            if (cleanRows == null)
            {
                return this;
            }

            bodyRows.addAll(rows);
            return this;
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to append the body of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addBody(final RowFormatter.Builder ... rows)
        {
            if (rows == null)
            {
                return this;
            }

            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return addBody(rowLines);
        }

        /**
         * @param lines The {@link List} of {@link String lines} to append the caption of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addCaption(final List<String> lines)
        {
            final List<String> cleanLines = TextFormatter.cleanList(lines);
            if (cleanLines == null)
            {
                return this;
            }
            for (final String captionLine : cleanLines)
            {
                final List<String> rowLines = TextFormatter.sanitize(captionLine);
                captionLines.addAll(rowLines);
            }

            return this;
        }

        /**
         * @param lines The array of {@link String lines} to append the caption of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addCaption(final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> rowLines = Lists.newArrayList(lines);
            return addCaption(rowLines);
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to append the footer of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addFooter(final List<RowFormatter.Builder> rows)
        {
            final List<RowFormatter.Builder> cleanRows = TextFormatter.cleanList(rows);
            if (cleanRows == null)
            {
                return this;
            }

            footerRows.addAll(cleanRows);
            return this;
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to append the footer of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addFooter(final RowFormatter.Builder ... rows)
        {
            if (rows == null)
            {
                return this;
            }

            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return addFooter(rowLines);
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to append the header of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addHeader(final List<RowFormatter.Builder> rows)
        {
            final List<RowFormatter.Builder> cleanRows = TextFormatter.cleanList(rows);
            if (cleanRows == null)
            {
                return this;
            }

            headerRows.addAll(cleanRows);
            return this;
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to append the header of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addHeader(final RowFormatter.Builder ... rows)
        {
            if (rows == null)
            {
                return this;
            }

            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return addHeader(rowLines);
        }

        /**
         * @param lines The {@link List} of {@link String lines} to append the title of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addTitle(final List<String> lines)
        {
            final List<String> cleanLines = TextFormatter.cleanList(lines);
            if (cleanLines == null)
            {
                return this;
            }
            for (final String titleLine : cleanLines)
            {
                final List<String> rowLines = TextFormatter.sanitize(titleLine);
                titleLines.addAll(rowLines);
            }

            return this;
        }

        /**
         * @param lines The array of {@link String lines} to append the title of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder addTitle(final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> rowLines = Lists.newArrayList(lines);
            return addTitle(rowLines);
        }

        @SuppressWarnings("boxing")
        @Override
        public void build()
        {
            criteria.horizontalBorder = MessageFormat.format(LINE_FORMAT,
                                                             criteria.joinerDivToken,
                                                             horizontalBorder.withDivider(criteria.horizontalDivToken)
                                                                             .withJoinerDivider(criteria.joinerDivToken)
                                                                             .withColumnWidths(criteria.columnWidths)
                                                                             .withTotalWidth(criteria.totalCharacterWidth)
                                                                             .finish()
                                                                             .getLines()
                                                                             .stream()
                                                                             .findFirst()
                                                                             .get());
            criteria.sectionBorder = MessageFormat.format(LINE_FORMAT,
                                                          criteria.joinerDivToken,
                                                          sectionBorder.withDivider(criteria.sectionDivToken)
                                                                       .withJoinerDivider(criteria.joinerDivToken)
                                                                       .withColumnWidths(criteria.columnWidths)
                                                                       .withTotalWidth(criteria.totalCharacterWidth)
                                                                       .finish()
                                                                       .getLines()
                                                                       .stream()
                                                                       .findFirst()
                                                                       .get());

            criteria.titleLines.addAll(titleLines.stream()
                                                 .map(line -> MessageFormat.format(LINE_FORMAT, ' ', line))
                                                 .map(line -> StringUtils.center(line, criteria.totalCharacterWidth + 2, criteria.titlePadToken))
                                                 .collect(Collectors.toList()));
            criteria.headerLines.addAll(headerRows.stream()
                                                  .sequential()
                                                  .map(row -> row.withColumnWidths(criteria.columnWidths)
                                                                 .withTotalWidth(criteria.totalCharacterWidth)
                                                                 .finish())
                                                  .flatMap(line -> line.getLines().stream())
                                                  .map(line -> MessageFormat.format(LINE_FORMAT, criteria.verticalDivToken, line))
                                                  .collect(Collectors.toList()));
            criteria.bodyLines.addAll(bodyRows.stream()
                                              .sequential()
                                              .map(row -> row.withColumnWidths(criteria.columnWidths)
                                                             .withTotalWidth(criteria.totalCharacterWidth)
                                                             .finish())
                                              .flatMap(line -> line.getLines().stream())
                                              .map(line -> MessageFormat.format(LINE_FORMAT, criteria.verticalDivToken, line))
                                              .collect(Collectors.toList()));
            criteria.footerLines.addAll(footerRows.stream()
                                                  .sequential()
                                                  .map(row -> row.withColumnWidths(criteria.columnWidths)
                                                                 .withTotalWidth(criteria.totalCharacterWidth)
                                                                 .finish())
                                                  .flatMap(line -> line.getLines().stream())
                                                  .map(line -> MessageFormat.format(LINE_FORMAT, criteria.verticalDivToken, line))
                                                  .collect(Collectors.toList()));
            criteria.captionLines.addAll(captionLines);
        }

        /**
         * @return The non-null, non-empty {@link ImmutableList} of {@link RowFormatter.Builder body rows} for the table.
         */
        public List<RowFormatter.Builder> getBodyRows()
        {
            return ImmutableList.copyOf(bodyRows);
        }

        /**
         * @return The non-null, possibly empty {@link ImmutableList} of {@link String caption lines} for the table.
         */
        public List<String> getCaptionLines()
        {
            return ImmutableList.copyOf(captionLines);
        }

        /**
         * @return The non-null, possibly empty {@link ImmutableList} of {@link RowFormatter.Builder footer rows} for the table.
         */
        public List<RowFormatter.Builder> getFooterRows()
        {
            return ImmutableList.copyOf(footerRows);
        }

        /**
         * @return The non-null, possibly empty {@link ImmutableList} of {@link RowFormatter.Builder header rows} for the table.
         */
        public List<RowFormatter.Builder> getHeaderRows()
        {
            return ImmutableList.copyOf(headerRows);
        }

        /**
         * @return The non-null, possibly empty {@link ImmutableList} of {@link String title lines} for the table.
         */
        public List<String> getTitleLines()
        {
            return ImmutableList.copyOf(titleLines);
        }

        @SuppressWarnings("boxing")
        @Override
        public void validate()
        {
            if (!titleLines.isEmpty())
            {
                criteria.adjustTotalCharacterWidth(titleLines);
            }
            if (!headerRows.isEmpty())
            {
                headerRows.forEach(row -> {
                    criteria.setColumnCount(row.getColumnCount());
                    criteria.adjustColumnCharacterWidths(row.getColumnWidths());
                    criteria.adjustTotalCharacterWidth(row.getTotalWidth());
                });
            }
            if (!bodyRows.isEmpty())
            {
                bodyRows.forEach(row -> {
                    criteria.setColumnCount(row.getColumnCount());
                    criteria.adjustColumnCharacterWidths(row.getColumnWidths());
                    criteria.adjustTotalCharacterWidth(row.getTotalWidth());
                });
            }
            if (!footerRows.isEmpty())
            {
                footerRows.forEach(row -> {
                    criteria.setColumnCount(row.getColumnCount());
                    criteria.adjustColumnCharacterWidths(row.getColumnWidths());
                    criteria.adjustTotalCharacterWidth(row.getTotalWidth());
                });
            }
            if (!captionLines.isEmpty())
            {
                criteria.adjustTotalCharacterWidth(captionLines);
            }

            // Incrementally increase smallest column by 1 until sum of columns match the totalCharactrWidth
            final int totalColumnWidths = criteria.columnWidths.stream().mapToInt(Integer::intValue).sum() + criteria.columnCount - 1;
            IntStream.range(0, criteria.totalCharacterWidth - totalColumnWidths).sequential().forEach(x -> {
                final int minColumnWidth = criteria.columnWidths.stream().mapToInt(Integer::intValue).min().getAsInt();
                final int firstMinValueIndex = IntStream.range(0, criteria.columnCount)
                                                        .boxed()
                                                        .filter(i -> criteria.columnWidths.get(i) == minColumnWidth)
                                                        .findFirst()
                                                        .get()
                                                        .intValue();
                criteria.columnWidths.set(firstMinValueIndex, minColumnWidth + 1);
            });
            criteria.adjustTotalCharacterWidth(totalColumnWidths);
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to populate the body of the table. (Cannot be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withBody(final List<RowFormatter.Builder> rows)
        {
            bodyRows.clear();
            return addBody(rows);
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to populate the body of the table. (Cannot be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withBody(final RowFormatter.Builder ... rows)
        {
            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return withBody(rowLines);
        }

        /**
         * @param lines The {@link List} of {@link String lines} to populate the caption of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withCaption(final List<String> lines)
        {
            if (lines == null)
            {
                return this;
            }

            captionLines.clear();
            return addCaption(lines);
        }

        /**
         * @param lines The array of {@link String lines} to populate the caption of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withCaption(final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> rowLines = Lists.newArrayList(lines);
            return withCaption(rowLines);
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to populate the footer of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withFooter(final List<RowFormatter.Builder> rows)
        {
            if (rows == null)
            {
                return this;
            }

            footerRows.clear();
            return addFooter(rows);
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to populate the footer of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withFooter(final RowFormatter.Builder ... rows)
        {
            if (rows == null)
            {
                return this;
            }

            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return withFooter(rowLines);
        }

        /**
         * @param rows The {@link List} of {@link RowFormatter.Builder rows} to populate the header of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withHeader(final List<RowFormatter.Builder> rows)
        {
            if (rows == null)
            {
                return this;
            }

            headerRows.clear();
            return addHeader(rows);
        }

        /**
         * @param rows The array of {@link RowFormatter.Builder rows} to populate the header of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withHeader(final RowFormatter.Builder ... rows)
        {
            if (rows == null)
            {
                return this;
            }

            final List<RowFormatter.Builder> rowLines = Lists.newArrayList(rows);
            return withHeader(rowLines);
        }

        /**
         * @param horizontalCharacterCount The minimum number of empty columns to space between vertical cell borders for all header/body/footer rows.
         *            This will fill with the character columns with the {@link CellFormatter.Builder#withPaddingSpacer(char) padding spacer
         *            character}. Default ' '. (Must be non-negative)
         * @param verticalLineCount The minimum number of empty rows to space between horizontal cell borders for all header/body/footer rows. This
         *            will fill the lines with the {@link CellFormatter.Builder#withPaddingSpacer(char) padding spacer character}. Default ' '. (Must
         *            be non-negative)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withPadding(final int horizontalCharacterCount, final int verticalLineCount)
        {
            headerRows.stream().forEach(row -> row.withPadding(horizontalCharacterCount, verticalLineCount));
            bodyRows.stream().forEach(row -> row.withPadding(horizontalCharacterCount, verticalLineCount));
            footerRows.stream().forEach(row -> row.withPadding(horizontalCharacterCount, verticalLineCount));

            return this;
        }

        /**
         * @param horizontalDivToken The divider {@link Character} used for defining the table's horizontal borders.
         * @param verticalDivToken The divider {@link Character} used for defining the table's vertical borders.
         * @param joinerDivToken The divider {@link Character} used for joining the table's horizontal and vertical borders.
         * @param sectionDivToken The divider {@link Character} used for separating the rows between table sections.
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withDividers(final char horizontalDivToken, final char verticalDivToken, final char joinerDivToken, final char sectionDivToken)
        {
            criteria.horizontalDivToken = horizontalDivToken;
            criteria.verticalDivToken = verticalDivToken;
            criteria.joinerDivToken = joinerDivToken;
            criteria.sectionDivToken = sectionDivToken;

            return this;
        }

        /**
         * @param paddingToken The padding {@link Character} used for centering the lines of the title phrase.
         * @param lines The {@link List} of {@link String lines} to populate the title of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withTitle(final char paddingToken, final List<String> lines)
        {
            if (lines == null)
            {
                return this;
            }

            titleLines.clear();
            criteria.titlePadToken = paddingToken;
            return addTitle(lines);
        }

        /**
         * @param paddingToken The padding {@link Character} used for centering the lines of the title phrase.
         * @param lines The array of {@link String lines} to populate the title of the table. (May be empty)
         * @return This non-null {@link TableFormatter.Builder}.
         */
        public Builder withTitle(final char paddingToken, final String ... lines)
        {
            if (lines == null)
            {
                return this;
            }

            final List<String> rowLines = Lists.newArrayList(lines);
            return withTitle(paddingToken, rowLines);
        }

    }

    private int                 columnCount         = 0;
    private final List<Integer> columnWidths        = new ArrayList<>(0);
    private int                 totalCharacterWidth = 0;

    private final List<String> bodyLines    = new ArrayList<>(0);
    private final List<String> captionLines = new ArrayList<>(0);
    private final List<String> footerLines  = new ArrayList<>(0);
    private final List<String> headerLines  = new ArrayList<>(0);
    private final List<String> titleLines   = new ArrayList<>(0);

    private String horizontalBorder;
    private String sectionBorder;

    private char horizontalDivToken = '-';
    private char joinerDivToken     = '+';
    private char sectionDivToken    = '=';
    private char titlePadToken      = '~';
    private char verticalDivToken   = '|';

    private TableFormatter()
    {
    }

    @SuppressWarnings("boxing")
    private void adjustColumnCharacterWidths(final List<Integer> widths)
    {
        if (columnWidths.isEmpty())
        {
            columnWidths.addAll(widths);
        }
        else
        {
            IntStream.range(0, widths.size())
                     .boxed()
                     .filter(i -> columnWidths.get(i) < widths.get(i))
                     .forEach(i -> columnWidths.set(i, widths.get(i)));
        }
    }

    private void adjustTotalCharacterWidth(final int maxWidth)
    {
        if (maxWidth > totalCharacterWidth)
        {
            totalCharacterWidth = maxWidth;
        }
    }

    private void adjustTotalCharacterWidth(final List<String> lines)
    {
        final int maxWidth = TextFormatter.maxWidth(lines);
        adjustTotalCharacterWidth(maxWidth);
    }

    /**
     * @return A new non-null, possibly empty {@link List} of {@link String strings} corresponding to the body rows of the table.
     */
    public List<String> getBody()
    {
        if (bodyLines.isEmpty())
        {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(bodyLines);
    }

    /**
     * @return A new non-null, possibly empty {@link List} of {@link String strings} corresponding to the caption of the table.
     */
    public List<String> getCaption()
    {
        if (captionLines.isEmpty())
        {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(captionLines);
    }

    /**
     * @return A new non-null, possibly empty {@link List} of {@link String strings} corresponding to the footer rows of the table.
     */
    public List<String> getFooter()
    {
        if (footerLines.isEmpty())
        {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(footerLines);
    }

    /**
     * @return A new non-null, possibly empty {@link List} of {@link String strings} corresponding to the header rows of the table.
     */
    public List<String> getHeader()
    {
        if (headerLines.isEmpty())
        {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(headerLines);
    }

    @Override
    public List<String> getLines()
    {
        final List<String> lines = new ArrayList<>(titleLines.size() + headerLines.size() + bodyLines.size() + footerLines.size() + captionLines.size() + 4);

        if (!titleLines.isEmpty())
        {
            lines.addAll(titleLines);
        }
        lines.add(horizontalBorder);
        if (!headerLines.isEmpty())
        {
            lines.addAll(headerLines);
            lines.add(sectionBorder);
        }
        if (!bodyLines.isEmpty())
        {
            lines.addAll(bodyLines);
        }
        if (!footerLines.isEmpty())
        {
            lines.add(sectionBorder);
            lines.addAll(footerLines);
        }
        lines.add(horizontalBorder);
        if (!captionLines.isEmpty())
        {
            lines.addAll(captionLines);
        }

        return lines;
    }

    /**
     * @return A new non-null, possibly empty {@link List} of {@link String strings} corresponding to the title of the table.
     */
    public List<String> getTitle()
    {
        if (titleLines.isEmpty())
        {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(titleLines);
    }

    private void setColumnCount(final int columnCount)
    {
        if (this.columnCount == 0)
        {
            this.columnCount = columnCount;
        }
        else
        {
            /**
             * <pre>
             * Verifier.verifyTrue(this.columnCount == columnCount,
             *                     MessageFormat.format("All rows must have the same number of columns within the same table. Expected: {0}; Actual: {1}",
             *                                          this.columnCount,
             *                                          columnCount));
             * </pre>
             */
        }
    }
}
