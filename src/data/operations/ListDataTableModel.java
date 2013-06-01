package data.operations;

import javax.swing.table.DefaultTableModel;

public class ListDataTableModel extends DefaultTableModel{

	@Override
	public boolean isCellEditable(int row, int column) {
		// Always Return False
		return false;
	}

	public ListDataTableModel(Object[][] data, Object[] colNames) {
		super(data, colNames);
	}

}
