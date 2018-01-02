package eli.ikea.mart.formatter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

/**
 * Generalized interface for formatting structured text.
 * <p style="font-style:italic;font-variant;small-caps;font-size:80%">
 * Copyright &copy; Cerner Corporation 2017 All rights reserved.
 * </p>
 *
 * @author Elisha Boozer (EB019254)
 * @author Millennium Platform - Messaging
 * @since X.X
 */
public interface TextFormatter
{
    /**
     * Defines the character justification within a block text.
     */
    @SuppressWarnings("boxing")
    enum Alignment
    {
     CENTER((line, width, spacer) -> StringUtils.center(line, width, spacer)),
     LEFT((line, width, spacer) -> Strings.padEnd(line, width, spacer)),
     RIGHT((line, width, spacer) -> Strings.padStart(line, width, spacer));

        final TriFunction<String, Integer, Character, String> paddifier;

        private Alignment(final TriFunction<String, Integer, Character, String> paddifier)
        {
            this.paddifier = paddifier;
        }

        public String paddify(final String line, final int width, final char spacer)
        {
            return paddifier.apply(line, width, spacer);
        }
    }

    /**
     * Regex pattern for identifying and breaking up new lines.
     */
    static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\R", Pattern.DOTALL);
    /**
     * System agnostic character sequence for generating a line break.
     */
    static final String  NEWLINE            = String.format("%n");
    /**
     * Regex pattern for identifying and replacing tab characters.
     */
    static final Pattern TAB_PATTERN        = Pattern.compile("\\t", Pattern.DOTALL);
    /**
     * Defined static string replacement for tabs.
     */
    static final String  TAB_SPACES         = "  ";

    /**
     * Removes all null elements from the list.
     *
     * @param elements A generic {@link List} of elements. (May be null)
     * @return A possibly null {@link List} of non-null elements.
     */
    static <T> List<T> cleanList(final List<T> elements)
    {
        if (elements == null)
        {
            return elements;
        }
        elements.removeIf((s) -> s == null);

        return elements;
    }

    /**
     * Determines the max number of characters per line amongst all provided lines of text.
     *
     * @param lines A {@link List} of {@link String lines of text}. (Cannot be null)
     * @return A non-negative count of characters per line amongst all provided lines of text.
     */
    static int maxWidth(final List<String> lines)
    {
        return lines.stream().max((line1, line2) -> line1.length() - line2.length()).orElse("").length();
    }

    /**
     * Breaks apart a single {@link String} of text into multiple lines if there are any line breaks, converts all tab characters into spaces, and
     * trims any extra whitespace off the end of each line.
     *
     * @param line Raw {@link String} of text to sanitize. (Cannot be null)
     * @return A new non-null but possibly empty {@link List} of sanitized {@link String strings}.
     */
    static List<String> sanitize(final String line)
    {
        return LINE_BREAK_PATTERN.splitAsStream(line)
                                 .map(s -> StringUtils.stripEnd(s, null))
                                 .map(s -> TAB_PATTERN.matcher(s).replaceAll(TAB_SPACES))
                                 .collect(Collectors.toList());
    }

    /**
     * @return A non-null, non-empty ordered {@link List} of formatted {@link String text}.
     */
    List<String> getLines();

    /**
     * @param stream An {@link OutputStream} used to print all formatted text.
     */
    default void printToStream(final OutputStream stream)
    {
        final String line = getLines().stream().collect(Collectors.joining(NEWLINE, "", NEWLINE));

        try
        {
            stream.write(line.getBytes());
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
}
