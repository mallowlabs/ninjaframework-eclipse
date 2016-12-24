package ninjaframework.eclipse.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NinjaHyperlink implements IHyperlink {

    private IRegion region;
    private String controllerName;
    private String methodName;
    private IJavaProject project;

    public NinjaHyperlink(IRegion region, IMethod method) {
        this.region = region;
        this.controllerName = method.getParent().getElementName();
        this.methodName = method.getElementName();
        this.project = method.getJavaProject();
    }

    @Override
    public IRegion getHyperlinkRegion() {
        return this.region;
    }

    @Override
    public String getHyperlinkText() {
        return "Open " + getViewPath();
    }

    private String getViewPath() {
        return "views/" + this.controllerName + "/" + this.methodName + ".ftl.html";
    }

    @Override
    public String getTypeLabel() {
        return getHyperlinkText();
    }

    public boolean exists() {
        return getViewFile() != null;
    }

    private IFile getViewFile() {
        IClasspathEntry[] entries = new IClasspathEntry[0];
        try {
            entries = project.getRawClasspath();
        } catch (JavaModelException e) {
        }
        for (IClasspathEntry entry : entries) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                IPath path = entry.getPath().append(getViewPath());
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                if (file != null && file.exists()) {
                    return file;
                }
            }
        }
        return null;
    }

    @Override
    public void open() {
        IFile file = getViewFile();
        if (file != null) {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();

            try {
                IDE.openEditor(activePage, file, true);
            } catch (PartInitException pie) {
            }
        }
    }

}
