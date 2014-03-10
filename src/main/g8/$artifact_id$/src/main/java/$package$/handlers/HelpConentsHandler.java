package $package$.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;

public class HelpConentsHandler {

    @Execute
    public void execute(final IWorkbenchWindow window, IWorkbench workbench) {
        // This may take a while, so use the busy indicator
        BusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    window.getWorkbench().getHelpSystem().displayHelp();
                }
            });
    }
}
