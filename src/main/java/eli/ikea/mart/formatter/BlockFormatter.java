package eli.ikea.mart.formatter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * A generalized formatter that constructs a bordered block of formatted text.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public class BlockFormatter implements TextFormatter
{
    /**
     * Builder for creating new {@link BlockFormatter} objects.
     */
    public static class Builder extends AbstractTestingBuilder<BlockFormatter>
    {
        /**
         * @return A new non-null instance of {@link BlockFormatter.Builder}.
         */
        public static Builder of()
        {
            return new BlockFormatter.Builder();
        }

        /**
         * @param lines The {@link List} of {@link String lines} to populate the block text.
         * @return A new non-null instance of {@link BlockFormatter.Builder} with the provided {@link List} of {@link String lines}.
         */
        public static Builder of(final List<String> lines)
        {
            return of().addText(lines);
        }

        /**
         * @param lines The array of {@link String lines} to populate the block text.
         * @return A new non-null instance of {@link BlockFormatter.Builder} with the provided array of {@link String lines}.
         */
        public static Builder of(final String ... lines)
        {
            return of().addText(lines);
        }

        /**
         * @param borderToken The border {@link Character} used for defining the block's outer border.
         * @param lines The {@link List} of {@link String lines} to populate the block text.
         * @return A new non-null instance of {@link BlockFormatter.Builder} with the provided {@link List} of {@link String lines}.
         */
        public static Builder of(final char borderToken, final List<String> lines)
        {
            return of().withText(borderToken, lines);
        }

        /**
         * @param borderToken The border {@link Character} used for defining the block's outer border.
         * @param lines The array of {@link String lines} to populate the block text.
         * @return A new non-null instance of {@link BlockFormatter.Builder} with the provided array of {@link String lines}.
         */
        public static Builder of(final char borderToken, final String ... lines)
        {
            return of().withText(borderToken, lines);
        }

        private final String LINE_FORMAT = "{0}{1}{0}";

        private final CellFormatter.Builder block        = CellFormatter.Builder.of();
        private final List<String>          captionLines = new ArrayList<>(0);
        private final List<String>          titleLines   = new ArrayList<>(0);

        private Builder()
        {
            super(new BlockFormatter());
        }

        /**
         * @param lines The {@link List} of {@link String lines} to append the caption of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param lines The array of {@link String lines} to append the caption of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param lines The {@link List} of {@link String lines} to append the text of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder addText(final List<String> lines)
        {
            block.addLines(lines);

            return this;
        }

        /**
         * @param lines The array of {@link String lines} to append the text of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder addText(final String ... lines)
        {
            block.addLines(lines);

            return this;
        }

        /**
         * @param lines The {@link List} of {@link String lines} to append the title of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param lines The array of {@link String lines} to append the title of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
        protected void build()
        {
            criteria.horizontalBorder = Strings.repeat(String.valueOf(criteria.borderToken), criteria.totalCharacterWidth + 2);
            criteria.titleLines.addAll(titleLines.stream()
                                                 .map(line -> MessageFormat.format(LINE_FORMAT, ' ', line))
                                                 .map(line -> StringUtils.center(line, criteria.totalCharacterWidth + 2, criteria.titlePadToken))
                                                 .collect(Collectors.toList()));
            criteria.blockText.addAll(block.withTotalSize(block.getTotalHeight(), criteria.totalCharacterWidth)
                                           .finish()
                                           .getLines()
                                           .stream()
                                           .map(line -> MessageFormat.format(LINE_FORMAT, criteria.borderToken, line))
                                           .collect(Collectors.toList()));
            criteria.captionLines.addAll(captionLines);
        }

        @Override
        protected void validate()
        {
            /**
             * Verifier.verifyTrue(criteria.block.getTotalHeight() > 0, "Unable to create block of empty text.");
             */

            if (!titleLines.isEmpty())
            {
                criteria.adjustTotalCharacterWidth(titleLines);
            }

            criteria.adjustTotalCharacterWidth(block.getTotalWidth());

            if (!captionLines.isEmpty())
            {
                criteria.adjustTotalCharacterWidth(captionLines);
            }
        }

        /**
         * @param token The border {@link Character} used for defining the block's outer border.
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder withBorder(final char token)
        {
            criteria.borderToken = token;

            return this;
        }

        /**
         * @param horizontalCharacterCount The minimum number of empty columns to space between vertical cell borders. This will fill with the
         *            character columns with the {@link #withPaddingSpacer(char) padding spacer character}. (Must be non-negative)
         * @param verticalLineCount The minimum number of empty rows to space between horizontal cell borders. This will fill the lines with the
         *            {@link #withPaddingSpacer(char) padding spacer character}. (Must be non-negative)
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder withPadding(final int horizontalCharacterCount, final int verticalLineCount)
        {
            block.withPadding(horizontalCharacterCount, verticalLineCount);

            return this;
        }

        /**
         * @param padding The {@link Character} used to pad the spacing between the block text and the cell borders. Default: ' '.
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder withPaddingSpacer(final char padding)
        {
            block.withPaddingSpacer(padding);

            return this;
        }

        /**
         * @param lines The {@link List} of {@link String lines} to populate the caption of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param lines The array of {@link String lines} to populate the caption of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param borderToken The border {@link Character} used to surround the lines of text.
         * @param lines The {@link List} of {@link String lines} to populate the text of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder withText(final char borderToken, final List<String> lines)
        {
            criteria.borderToken = borderToken;
            block.withLines(lines);

            return this;
        }

        /**
         * @param borderToken The border {@link Character} used to surround the lines of text.
         * @param lines The array of {@link String lines} to populate the text of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
         */
        public Builder withText(final char borderToken, final String ... lines)
        {
            criteria.borderToken = borderToken;
            block.withLines(lines);

            return this;
        }

        /**
         * @param paddingToken The padding {@link Character} used for centering the lines of the title phrase.
         * @param lines The {@link List} of {@link String lines} to populate the title of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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
         * @param lines The array of {@link String lines} to populate the title of the block. (May be empty)
         * @return This non-null {@link BlockFormatter.Builder}.
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

        /**
         * @param height The positive total number of lines for the cell.
         * @param width The positive total character width of the cell.
         * @return This non-null {@link CellFormatter.Builder}.
         */
        public Builder withTotalSize(final int height, final int width)
        {
            block.withTotalSize(height, width);

            return this;
        }
    }

    private int totalCharacterWidth = 0;

    private final List<String> blockText    = new ArrayList<>(0);
    private final List<String> captionLines = new ArrayList<>(0);
    private final List<String> titleLines   = new ArrayList<>(0);

    private String horizontalBorder;

    private char borderToken   = '*';
    private char titlePadToken = '~';

    private BlockFormatter()
    {
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

    @Override
    public List<String> getLines()
    {
        final List<String> lines = new ArrayList<>(titleLines.size() + blockText.size() + captionLines.size());

        if (!titleLines.isEmpty())
        {
            lines.addAll(titleLines);
        }
        if (!blockText.isEmpty())
        {
            lines.add(horizontalBorder);
            lines.addAll(blockText);
            lines.add(horizontalBorder);
        }
        if (!captionLines.isEmpty())
        {
            lines.addAll(captionLines);
        }

        return lines;
    }

}
