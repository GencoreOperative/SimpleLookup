package simplelookup.sampleui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import simplelookup.Lookup;
import simplelookup.listener.LookupBasicListener;
import simplelookup.sampleui.actions.BackAction;
import simplelookup.sampleui.actions.ChangeAction;

/**
 * A Test UI to demonstrate a typical usage of a Lookup and to explore/examine
 * its methods in terms of usefulness for a typical project.
 *
 * This UI does not use SwingWorker or SwingLib helper libraries because it
 * does not want to depend on them. UI operations that invoke disk IO or any
 * other potentailly delayed operation should be performed in a background
 * thread.
 * 
 * @author Robert Wapshott
 */
public class FileBrowser extends javax.swing.JFrame {
    public static final Lookup lookup = new Lookup();

    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:SS");
    
    private static final String ACTION_BACK = "Back";
    
    public FileBrowser() {
        initActions();
        initComponents();

        // File List
        jList1.setCellRenderer(new Render());
        jList1.addMouseListener(new MouseAdapter() {
            private int click = 0;
            @Override
            public void mousePressed(MouseEvent e) {
                click++;
                process(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                process(e);
            }
            private void process(MouseEvent e) {
                int row = jList1.locationToIndex(e.getPoint());
                if (jList1.getSelectedIndex() != row) {
                    jList1.setSelectedIndex(row);
                }
                FileNode node = (FileNode) jList1.getSelectedValue();
                if (node == null) return;
                if (e.isPopupTrigger()) {
                    JPopupMenu menu = new JPopupMenu();
                    for (Action a : node.getActions()) {
                        menu.add(new JMenuItem(a));
                    }
                    menu.show(e.getComponent(), e.getX(), e.getY());
                    click = 0;
                } else if (click >= 2 && node.getPreferredAction().isEnabled()) {
                    node.getPreferredAction().actionPerformed(null);
                    click = 0;
                }
            }
        });
        jList1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                FileNode node = (FileNode) jList1.getSelectedValue();
                if (node == null) return;

                // Node Context
                for (Action a : node.getActions()) {
                    jList1.getInputMap().put(
                            (KeyStroke) a.getValue(Action.ACCELERATOR_KEY),
                            a.getValue(Action.NAME));
                    jList1.getActionMap().put(a.getValue(Action.NAME), a);
                }

                lookup.getView(SelectedFile.class).replaceAllWith(new SelectedFile(node.getFile()));
            }
        });
        lookup.register(FileNode.class, new LookupBasicListener<FileNode>(){
            			@Override
			public void resultChanged(Collection<FileNode> result) {
				DefaultListModel model = (DefaultListModel) jList1.getModel();
                model.clear();
                for (FileNode n : result) {
                    model.addElement(n);
                }
                if (model.getSize() > 0) {
                    jList1.setSelectedIndex(0);
                }
			}
        });

        // Details Table
        jTable1.setModel(new DefaultTableModel(new String[]{"Property", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        lookup.register(SelectedFile.class, new LookupBasicListener<SelectedFile>(){
        	@Override
			public void resultChanged(Collection<SelectedFile> result) {
				File selected = result.iterator().next().file;
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }
                model.addRow(new Object[]{"Name", selected.getName()});
                model.addRow(new Object[]{
                    "Modified",
                    format.format(new Date(selected.lastModified()))});
                model.addRow(new Object[]{"Size", Long.toString(selected.length())});
                model.addRow(new Object[]{"Read Only", !selected.canRead()});
			}
        });

        // Path
        lookup.register(CurrentPath.class, new LookupBasicListener<CurrentPath>() {
            @Override
			public void resultChanged(Collection<CurrentPath> result) {
				CurrentPath path = result.iterator().next();
                pathField.setText(path.path.getPath());
			}
        });

        // Configure Frame
        setLocationRelativeTo(null);

        File current = new File(System.getProperty("user.dir"));
        new ChangeAction(current).actionPerformed(null);
    }

    private void initActions() {
        Action back = new BackAction();
        getRootPane().getActionMap().put(ACTION_BACK, back);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        pathField = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Path");

        jButton1.setAction(getRootPane().getActionMap().get(ACTION_BACK));
        jButton1.setText("Up");

        pathField.setEditable(false);

        jSplitPane1.setDividerLocation(175);

        jList1.setModel(new DefaultListModel());
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Property", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jSplitPane1.setRightComponent(jScrollPane2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jButton1)
                    .add(pathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FileBrowser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField pathField;
    // End of variables declaration//GEN-END:variables

    private static class Render extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            FileNode f = (FileNode) value;
            setIcon(f.getIcon());
            setText(f.getName());
            return this;
        }
    }
}
