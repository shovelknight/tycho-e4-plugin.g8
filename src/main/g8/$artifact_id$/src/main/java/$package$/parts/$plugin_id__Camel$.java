package $package$.parts;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class $plugin_id;format="Camel"$ {
	
        
    @PostConstruct
    public void createPartControl(Composite parent, final ECommandService cmdsvc, final EHandlerService handlersvc) {
        GridLayoutFactory.fillDefaults().applyTo(parent);
        
        Label label = new Label(parent, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(parent);
        label.setText("Current Version: " + Platform.getProduct().getDefiningBundle().getVersion().toString());
        
        final Text input = new Text(parent, SWT.NONE);
        input.setText("Enter p2 url");
        GridDataFactory.fillDefaults().applyTo(input);
        
        Button btn = new Button(parent, SWT.PUSH);
        GridDataFactory.fillDefaults().applyTo(input);
        btn.setText("Update");
        btn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (input.getText() != null) {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("org.eclipse.ui.checkupdate.p2url", input.getText());
                    ParameterizedCommand command = cmdsvc.createCommand("org.eclipse.ui.checkupdate.command", param);
System.out.println(command);
                    Object result = handlersvc.executeHandler(command);
System.out.println(result);
                }
            }
        });
    }
}
