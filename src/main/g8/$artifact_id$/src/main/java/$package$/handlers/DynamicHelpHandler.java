package $package$.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.ui.IWorkbenchWindow;

public class DynamicHelpHandler {
    @Execute
    public void execute(final IWorkbenchWindow window) {
        window.getWorkbench().getHelpSystem().displayDynamicHelp();;
    }
}
