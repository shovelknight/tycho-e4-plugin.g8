package my.organization.sample.myplugin.handlers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.Update;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

//
// Require-Bundle: org.eclipse.equinox.p2.core|engine|operation|metadata.repository
// Feature: org.eclipse.equinox.p2.core.feature
//
// !!!!!!!!!!!!!!!!!
// do not run the update from for IDE. Update only works in an exported application
// !!!!!!!!!!!!!!!!!
//

public class UpdateHandler {

    @Execute
    public void execute(final IProvisioningAgent agent, final Shell parent,
            final UISynchronize sync, final IWorkbench workbench,
            @Named("org.eclipse.ui.checkupdate.p2url") final String p2url) {
        
        if (p2url == null)
            return;
        
        Job j = new Job("Update Job") {
            private boolean doInstall = false;

            @Override
            protected IStatus run(final IProgressMonitor monitor) {

                /* 1. Prepare update plumbing */
                final ProvisioningSession session = new ProvisioningSession(agent);
                final UpdateOperation operation = new UpdateOperation(session);

                // create uri
                URI uri = null;
                try {
                    uri = new URI(p2url);
                } catch (final URISyntaxException e) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog.openError(parent, "URI invalid", e.getMessage());
                        }
                    });
                    return Status.CANCEL_STATUS;
                }

                // set location of artifact and metadata repo
                operation.getProvisioningContext().setArtifactRepositories(new URI[] {uri});
                operation.getProvisioningContext().setMetadataRepositories(new URI[] {uri});

                /* 2. check for updates */

                // run update checks causing I/O
                final IStatus status = operation.resolveModal(monitor);

                // failed to find updates (inform user and exit)
                if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog.openWarning(parent, "No update",
                                    "No updates for the current installation have been found");
                        }
                    });
                    return Status.CANCEL_STATUS;
                }

                /* 3. Ask if updates should be installed and run installation */

                // found updates, ask user if to install?
                if (status.isOK() && status.getSeverity() != IStatus.ERROR) {
                    sync.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            String updates = "";
                            Update[] possibleUpdates = operation.getPossibleUpdates();
                            for (Update update : possibleUpdates) {
                                updates += update + "\n";
                            }
                            doInstall =
                                    MessageDialog.openQuestion(parent, "Really install updates?",
                                            updates);
                        }
                    });
                }

                // start installation
                if (doInstall) {
                    final ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
                    // updates cannot run from within Eclipse IDE!!!
                    if (provisioningJob == null) {
                        System.err
                                .println("Running update from within Eclipse IDE? This won't work!!!");
                        throw new NullPointerException();
                    }

                    // register a job change listener to track
                    // installation progress and notify user upon success
                    provisioningJob.addJobChangeListener(new JobChangeAdapter() {
                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult().isOK()) {
                                sync.syncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        boolean restart =
                                                MessageDialog
                                                        .openQuestion(parent,
                                                                "Updates installed, restart?",
                                                                "Updates have been installed successfully, do you want to restart?");
                                        if (restart) {
                                            workbench.restart();
                                        }
                                    }
                                });

                            }
                            super.done(event);
                        }
                    });

                    provisioningJob.schedule();
                }
                return Status.OK_STATUS;
            }
        };
        j.schedule();
    }
}
