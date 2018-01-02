package eli.ikea.mart.formatter;

import org.junit.Test;

/**
 * TODO Functional Description
 *
 * @author The Architect
 */
public class BlockFormatterTest
{
    @Test
    public void testSingleBodyRow()
    {
        System.out.println("** Block: Single Body Row **");
        final BlockFormatter block = BlockFormatter.Builder.of('#', "Texty").finish();

        block.printToStream(System.out);
        System.out.println("******");
    }

    @Test
    public void testMultipleBodyRows()
    {
        System.out.println("** Block: Multiple Body Rows **");
        final BlockFormatter block = BlockFormatter.Builder.of('#', "Texty", "T", "Antidisestablishmentarianism").finish();

        block.printToStream(System.out);
        System.out.println("******");
    }

    @Test
    public void testSingleBodyRow_WithPadding()
    {
        System.out.println("** Block: Single Body Row - With Padding (H:2 x V:1) **");
        final BlockFormatter block = BlockFormatter.Builder.of('#', "Texty", "T", "Antidisestablishmentarianism").withPadding(2, 1).finish();

        block.printToStream(System.out);
        System.out.println("******");
    }

    @Test
    public void testSingleBodyRow_WithTitle()
    {
        System.out.println("** Block: Single Body Row - With Title **");
        final BlockFormatter block = BlockFormatter.Builder.of('#', "Antidisestablishmentarianism").addTitle("Title").finish();

        block.printToStream(System.out);
        System.out.println("******");
    }

    @Test
    public void testSingleBodyRow_WithCaption()
    {
        System.out.println("** Block: Single Body Row - With Caption **");
        final BlockFormatter block = BlockFormatter.Builder.of('#', "Texty").addCaption("Caption").finish();

        block.printToStream(System.out);
        System.out.println("******");
    }
}
