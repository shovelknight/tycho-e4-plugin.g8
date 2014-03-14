package $package$.handlers;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.ProvUIActivator;
import org.eclipse.equinox.internal.p2.ui.sdk.UpdateHandler;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.equinox.p2.ui.ProvisioningUI;

public class P2UpdateHandler {

    @Execute
    public void execute() {
        UpdateHandler updateHandler = new UpdateHandler();
        updateHandler.execute(null);
    }
    
    void init() throws URISyntaxException {
        final ProvisioningUI ui = ProvUIActivator.getDefault().getProvisioningUI();
        IArtifactRepositoryManager artifactManager = ProvUI.getArtifactRepositoryManager(ui.getSession());
        artifactManager.addRepository(new URI("http://localhost:8081/nexus/content/unzip/myvirtual/my/organization/sample.repository/0.2.0/sample.repository-0.2.0.zip-unzip/"));

        IMetadataRepositoryManager metadataManager = ProvUI.getMetadataRepositoryManager(ui.getSession());
        metadataManager.addRepository(new URI("http://localhost:8081/nexus/content/unzip/myvirtual/my/organization/sample.repository/0.2.0/sample.repository-0.2.0.zip-unzip/"));
    }
}
