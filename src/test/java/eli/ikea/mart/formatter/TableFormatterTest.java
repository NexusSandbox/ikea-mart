package eli.ikea.mart.formatter;

import org.junit.Test;

import eli.ikea.mart.formatter.TextFormatter.Alignment;

/**
 * TODO Functional Description
 *
 * @author The Architect
 */
public class TableFormatterTest
{
    @Test
    public void testSingleBodyRow_MultipleColumns()
    {
        System.out.println("** Table: Single Body Row - Multiple Columns **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("x", "xx", "xxx", "xxxx")).finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testMultipleBodyRows_MultipleColumns()
    {
        System.out.println("** Table: Multiple Body Rows - Multiple Columns **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("x", "xx", "xxx", "xxxx"),
                                                               RowFormatter.Builder.of("yyyy", "yyy", "yy", "y"))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testMultipleBodyRows_MultipleColumns_WithHeaderFooter()
    {
        System.out.println("** Table: Multiple Body Rows - Multiple Columns - With Header & Footer **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("x", "xx", "xxx", "xxxx"),
                                                               RowFormatter.Builder.of("yyyy", "yyy", "yy", "y"))
                                                           .withHeader(RowFormatter.Builder.of("Header1", "Header2", "Header3", "Header4"))
                                                           .withFooter(RowFormatter.Builder.of("F1", "F2", "F3", "F4"))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testMultipleBodyRows_MultipleColumns_WithHeaderFooter_Padding()
    {
        System.out.println("** Table: Multiple Body Rows - Multiple Columns - With Header & Footer - Padding **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("x", "xx", "xxx", "xxxx").withPadding(1, 1),
                                                               RowFormatter.Builder.of("yyyy", "yyy", "yy", "y").withPadding(1, 1))
                                                           .withHeader(RowFormatter.Builder.of("Header1", "Header2", "Header3", "Header4")
                                                                                           .withPadding(2, 1))
                                                           .withFooter(RowFormatter.Builder.of("F1", "F2", "F3", "F4").withPadding(0, 3))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithTitle()
    {
        System.out.println("** Table: Single Body Row - With Title **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxxxxxxxxxxx")).withTitle('~', "My Title").finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithTitleLongerThanRows()
    {
        System.out.println("** Table: Single Body Row - With Title Longer Than Rows **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("x")).withTitle('~', "My Long Title").finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_MultipleColumns_WithTitleLongerThanRows()
    {
        System.out.println("** Table: Single Body Row - Multiple Columns - With Title Longer Than Rows **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxx", "x", "xx")).withTitle('~', "My Long Title!").finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithHeader()
    {
        System.out.println("** Table: Single Body Row - With Header **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxxxxxxxxxxx"))
                                                           .withHeader(RowFormatter.Builder.of("Header"))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithHeader_CenteredCells()
    {
        System.out.println("** Table: Single Body Row - With Header - Centered Cells **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxxxxxxxxxxx"))
                                                           .withHeader(RowFormatter.Builder.of("Header").withAlignment(Alignment.CENTER))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithFooter()
    {
        System.out.println("** Table: Single Body Row - With Footer **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxxxxxxxxxxx"))
                                                           .withFooter(RowFormatter.Builder.of("Footer"))
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testSingleBodyRow_WithCaption()
    {
        System.out.println("** Table: Single Body Row - With Caption **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("xxxxxxxxxxxx")).withCaption("My Caption").finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }

    @Test
    public void testFullSQLExample()
    {
        System.out.println("** Table: Full SQL Example **");
        final TableFormatter table = TableFormatter.Builder.of(RowFormatter.Builder.of("1", "A006", "Trevelyan, Alec", "Brussels", "UK", "1.010"),
                                                               RowFormatter.Builder.of("2", "A007", "Bond, James", "London", "UK", "1.234"),
                                                               RowFormatter.Builder.of("3", "A008", "Timothy, Bill", "London", "UK", "1.000"))
                                                           .withHeader(RowFormatter.Builder.of("#",
                                                                                               "AGENT_CODE",
                                                                                               "NAME",
                                                                                               "WORKING_AREA",
                                                                                               "COUNTRY",
                                                                                               "SALARY")
                                                                                           .withAlignment(Alignment.CENTER),
                                                                       RowFormatter.Builder.of("",
                                                                                               "(VARCHAR)",
                                                                                               "(VARCHAR)",
                                                                                               "(VARCHAR)",
                                                                                               "(VARCHAR)",
                                                                                               "(NUMERIC)")
                                                                                           .withAlignment(Alignment.CENTER))
                                                           .withTitle('~', "SECRET_AGENT")
                                                           .withPadding(1, 0)
                                                           .finish();

        table.printToStream(System.out);
        System.out.println("*****");
    }
}
