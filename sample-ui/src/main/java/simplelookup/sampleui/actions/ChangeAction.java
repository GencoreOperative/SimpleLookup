package simplelookup.sampleui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import simplelookup.sampleui.CurrentPath;
import simplelookup.sampleui.FileNode;
import simplelookup.sampleui.FileBrowser;

/**
 * 
 * @author KylaBob
 */
public class ChangeAction extends AbstractAction {
    File file;
    public ChangeAction(File file) {
        putValue(NAME, "Change");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        this.file = file;
        setEnabled(file.isDirectory());
    }

    public void actionPerformed(ActionEvent e) {
        List<FileNode> nodes = new LinkedList<FileNode>();
        for (File f : file.listFiles()) {
            nodes.add(new FileNode(f));
        }
        FileBrowser.lookup.getView(FileNode.class).replaceAllWith(nodes);
        FileBrowser.lookup.getView(CurrentPath.class).replaceAllWith(new CurrentPath(file));
    }
}
