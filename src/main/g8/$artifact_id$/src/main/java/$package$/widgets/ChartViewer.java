package $package$.widgets;

import org.eclipse.birt.chart.device.ICallBackNotifier;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.swt.SwtRendererImpl;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.script.ChartScriptContext;
import org.eclipse.birt.chart.script.ScriptClassLoaderAdapter;
import org.eclipse.birt.chart.script.ScriptHandler;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChartViewer extends Canvas implements PaintListener, ICallBackNotifier,
        ControlListener {

    private boolean m_bNeedGeneration = true;
    private boolean m_bNeedRefresh = true;

    private Chart m_chart;
    private IDeviceRenderer m_idr;
    private RunTimeContext m_rtc;
    private GeneratedChartState m_gcs;
    private double m_scale;
    
    // ------------------------------------------------------------------------
    public ChartViewer(Composite parent, int style) {
        super(parent, style);
        init();
    }

    // ------------------------------------------------------------------------
    private void init() {
//        try {
//            PluginSettings ps = PluginSettings.instance();
//            m_idr = ps.getDevice("dv.SWT");
            m_idr = new SwtRendererImpl();
            m_idr.setProperty(IDeviceRenderer.UPDATE_NOTIFIER, this);
            
            addControlListener(this);
            addPaintListener(this);
            
            m_rtc = createRuntimeContext();
            m_scale = 72d / m_idr.getDisplayServer().getDpiResolution();
            
//        } catch (ChartException e) {
//            throw new RuntimeException(e);
//        }
    }

    private RunTimeContext createRuntimeContext() {
        ScriptHandler scriptHandler = new ScriptHandler();
        ChartScriptContext scriptContext = new ChartScriptContext();
        ScriptClassLoaderAdapter scriptClassLoader =
                new ScriptClassLoaderAdapter(getClass().getClassLoader());
        scriptHandler.setScriptClassLoader(scriptClassLoader);
        scriptHandler.setScriptContext(scriptContext);
        
        // register event handler
//        try {
//            scriptHandler.register(ChartEventHandlerAdapter.class.getName(), ChartEventHandlerAdapter.class.getName());
//        } catch (ChartException e) {
//            throw new RuntimeException(e);
//        }

        RunTimeContext runTimeContext = new RunTimeContext();
        runTimeContext.setScriptHandler(scriptHandler);
        runTimeContext.setScriptContext(scriptContext);

        return runTimeContext;
    }

    // ------------------------------------------------------------------------

    @Override
    public void regenerateChart() {
        rebuildChart();
    }

    @Override
    public void repaintChart() {
        refreshChart();
//        rebuildChart();
        
    }

    @Override
    public Object peerInstance() {
        return this;
    }

    @Override
    public Chart getDesignTimeModel() {
        return m_chart;
    }

    @Override
    public Chart getRunTimeModel() {
        return m_gcs.getChartModel();
    }

    @Override
    public void callback(Object event, Object source, CallBackValue value) {
//        WrappedStructureSource wss = (WrappedStructureSource) source;
//        DataPointHints dph = (DataPointHints) wss.getSource();
    }

    // ------------------------------------------------------------------------
    @Override
    public void controlMoved(ControlEvent e) {
    }

    @Override
    public void controlResized(ControlEvent e) {
        m_bNeedGeneration = true;
    }

    // ------------------------------------------------------------------------
    @Override
    public void paintControl(PaintEvent e) {
        if (m_chart != null)
            onDrawChart(e, m_chart, m_rtc);
    }
    
    private void onDrawChart(PaintEvent e, Chart chart, RunTimeContext rtc) {
        Rectangle d = getClientArea();
        IDeviceRenderer idr = m_idr;
        Generator gr = Generator.instance();
        idr.setProperty(IDeviceRenderer.GRAPHICS_CONTEXT, e.gc);
        if (m_bNeedGeneration) {
            m_bNeedGeneration = false;
            m_bNeedRefresh = false;

            Bounds bo = BoundsImpl.create(0, 0, d.width, d.height);
            bo.scale(m_scale);

            try {
                m_gcs = gr.build(idr.getDisplayServer(), chart, bo, null, rtc, null);
                gr.render(idr, m_gcs);
            } catch (ChartException ex) {
                ex.printStackTrace();
            }
        } else if (m_bNeedRefresh) {
            m_bNeedRefresh = false;

            try {
                //gr.refresh(m_gcs);
                gr.render(idr, m_gcs);
            } catch (ChartException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                gr.render(idr, m_gcs);
            } catch (ChartException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ------------------------------------------------------------------------
    public void drawChart(Chart chart) {
        m_chart = chart;
        rebuildChart();
    }
    
    public GeneratedChartState getGeneratedChartState() {
        return m_gcs;
    }
    
    public void rebuildChart() {
        m_bNeedGeneration = true;
        redraw();
    }
    
    public void refreshChart() {
        m_bNeedRefresh = true;
        redraw();
    }
}
