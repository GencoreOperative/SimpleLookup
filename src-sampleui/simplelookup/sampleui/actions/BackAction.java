package simplelookup.sampleui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import simplelookup.Lookup.View;
import simplelookup.listener.LookupBasicListener;
import simplelookup.sampleui.CurrentPath;
import simplelookup.sampleui.FileBrowser;

/**
 *
 * @author KylaBob
 */
public class BackAction extends AbstractAction {
    private View<CurrentPath> view;
    public BackAction() {
        putValue(NAME, "Back");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
        LookupBasicListener<CurrentPath> adapter = new LookupBasicListener<CurrentPath>(){
            @Override
			public void resultChanged(Collection<CurrentPath> result) {
				if (result.isEmpty()) {
                    setEnabled(false);
                } else {
                    CurrentPath path = result.iterator().next();
                    if (path.path.getParentFile() != null) {
                        setEnabled(true);
                    } else {
                        setEnabled(false);
                    }
                }
			}
        };
        FileBrowser.lookup.register(CurrentPath.class, adapter);
        view = FileBrowser.lookup.getView(CurrentPath.class);
        adapter.resultChanged(view.list());
    }
    
    public void actionPerformed(ActionEvent e) {
        CurrentPath path = view.list().iterator().next();
        new ChangeAction(path.path.getParentFile()).actionPerformed(null);
    }
}
