package $package$;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class $plugin_id;format="Camel"$ {
	
    @PostConstruct
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        Label label = new Label(parent, SWT.NONE);
        label.setText("This is $plugin_id$");
    }
}
