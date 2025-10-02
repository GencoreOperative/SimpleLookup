package simplelookup.sampleui;

import java.io.File;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import simplelookup.sampleui.actions.BackAction;
import simplelookup.sampleui.actions.ChangeAction;

/**
 *
 * @author KylaBob
 */
public class FileNode {
    private static final Icon folderIcon = new ImageIcon(FileBrowser.class.getResource("folder.png"));
    private static final Icon fileIcon = new ImageIcon(FileBrowser.class.getResource("page_white_text.png"));

    private Action[] actions;
    private File file;
    public FileNode(File f) {
        this.file = f;
        actions = new Action[]{new ChangeAction(file), new BackAction()};
    }

    public Icon getIcon() {
        if (file.isDirectory()) {
            return folderIcon;
        } else {
            return fileIcon;
        }
    }

    public String getName() {
        return file.getName();
    }

    public File getFile() {
        return file;
    }

    public Action[] getActions() {
        return actions;
    }

    public Action getPreferredAction() {
        return actions[0];
    }
}
