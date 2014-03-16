package $package$.parts;

import javax.annotation.PostConstruct;

import my.organization.sample.birt.widgets.ChartViewer;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.*;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class $plugin_id;format="Camel"$ {
    private ChartViewer chartViewer;

    @PostConstruct
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        chartViewer = new ChartViewer(parent, SWT.NONE);
        Chart chart = createDemo();
        chartViewer.drawChart(chart);
    }
    
    @Focus
    void setFocus() {
        chartViewer.setFocus();
    }
    
    // ------------------------------------------------------------------------
    private static final String[] sa = {
        "One", "Two", "Three", "Four", "Five",
        "Six", "Seven", "Eight", "Nine", "Ten" };
    
    private static final double[] da1 = {
            56.99,
            352.95,
            -201.95,
            299.95,
            -95.95,
            25.45,
            129.33,
            -26.5,
            43.5,
            122
    };

    private static final double[] da2 = {
            20, 35, 59, 105, 150, -37, -65, -99, -145, -185
    };
    
    public static final Chart createDemo()
    {
        ChartWithAxes cwaBar = ChartWithAxesImpl.create();

        // Plot
        cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
        Plot p = cwaBar.getPlot();
        p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));

        // Legend
        Legend lg = cwaBar.getLegend();
        LineAttributes lia = lg.getOutline();
        lg.getText().getFont().setSize(16);
        lia.setStyle( LineStyle.SOLID_LITERAL );
        lg.getInsets().setLeft(10);
        lg.getInsets().setRight(10);

        // Title
        cwaBar.getTitle()
                .getLabel()
                .getCaption()
                .setValue("Chart Demo");

        // X-Axis
        Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];

        xAxisPrimary.setType(AxisType.TEXT_LITERAL);
        xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
        xAxisPrimary.getOrigin().setType(IntersectionType.MIN_LITERAL);

        xAxisPrimary.getTitle().getCaption().setValue("Text X-Axis");
        xAxisPrimary.setTitlePosition(Position.BELOW_LITERAL);

        xAxisPrimary.getLabel().getCaption().getFont().setRotation(75);
        xAxisPrimary.setLabelPosition(Position.BELOW_LITERAL);

        xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
        xAxisPrimary.getMajorGrid().getLineAttributes().setStyle(LineStyle.DOTTED_LITERAL);
        xAxisPrimary.getMajorGrid().getLineAttributes().setColor(ColorDefinitionImpl.create(64, 64, 64));
        xAxisPrimary.getMajorGrid().getLineAttributes().setVisible(true);

        // Y-Axis
        Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);

        yAxisPrimary.getLabel().getCaption().setValue("Price Axis");
        yAxisPrimary.getLabel().getCaption().getFont().setRotation(37);
        yAxisPrimary.setLabelPosition(Position.LEFT_LITERAL);

        yAxisPrimary.setTitlePosition(Position.LEFT_LITERAL);
        yAxisPrimary.getTitle().getCaption().setValue("Linear Value Y-Axis");

        yAxisPrimary.setType(AxisType.LINEAR_LITERAL);

        yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
        yAxisPrimary.getMajorGrid().getLineAttributes().setStyle(LineStyle.DOTTED_LITERAL);
        yAxisPrimary.getMajorGrid().getLineAttributes().setColor(ColorDefinitionImpl.RED());
        yAxisPrimary.getMajorGrid().getLineAttributes().setVisible(true);

        // X-Series
        Series seCategory = SeriesImpl.create();
        SeriesDefinition sdX = SeriesDefinitionImpl.create();
        xAxisPrimary.getSeriesDefinitions().add(sdX);
        sdX.getSeries().add(seCategory);

        // Y-Series (1)
        BarSeries bs1 = (BarSeries) BarSeriesImpl.create();
        bs1.setSeriesIdentifier("Unit Price");
        bs1.setRiserOutline(null);
        bs1.setRiser(RiserType.RECTANGLE_LITERAL);
        bs1.setStacked(true);
        
        // Y-Series (2)
        LineSeries ls1 = (LineSeries) LineSeriesImpl.create();
        ls1.setSeriesIdentifier("Quantity");
        ls1.getLineAttributes().setColor(ColorDefinitionImpl.GREEN());
        for (int i = 0; i < ls1.getMarkers().size(); i++)
        {
             ls1.getMarkers().get(i).setType(MarkerType.BOX_LITERAL);
        }
        ls1.setCurve(true);
        ls1.setStacked(true);

        SeriesDefinition sdY = SeriesDefinitionImpl.create();
        yAxisPrimary.getSeriesDefinitions().add(sdY);
        sdY.getSeriesPalette().shift(-1);
        sdY.getSeries().add(bs1);
        sdY.getSeries().add(ls1);

        // Update data
         updateDataSet(cwaBar);
        return cwaBar;
    }
    
    static final void updateDataSet(ChartWithAxes cwaBar)
    {
        // Associate with Data Set
        TextDataSet categoryValues = TextDataSetImpl.create(sa);
        NumberDataSet seriesOneValues = NumberDataSetImpl.create(da1);
        NumberDataSet seriesTwoValues = NumberDataSetImpl.create(da2);

        // X-Axis
        Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];
        SeriesDefinition sdX = (SeriesDefinition) xAxisPrimary.getSeriesDefinitions().get(0);
        sdX.getSeries().get(0).setDataSet(categoryValues);

        // Y-Axis
        Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
        SeriesDefinition sdY = (SeriesDefinition) yAxisPrimary.getSeriesDefinitions().get(0);
        sdY.getSeries().get(0).setDataSet(seriesOneValues);
        sdY.getSeries().get(1).setDataSet(seriesTwoValues);
    }
}
