package com.ereader.view;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import org.apache.commons.lang.StringUtils;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class MetadataPanel extends JPanel implements NavigationEventListener {


	private static final long serialVersionUID = -2810193923996466948L;
	private JScrollPane scrollPane; 

	public MetadataPanel(Navigator navigator) {
		super(new GridLayout(1, 0));
		this.scrollPane = (JScrollPane) add(new JScrollPane());
		navigator.addNavigationEventListener(this);
		initBook(navigator.getBook());
	}

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		JTable table = new JTable(createTableData(book.getMetadata()), new String[] {"", ""});
		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
		contentPanel.add(table, BorderLayout.CENTER);

		scrollPane.getViewport().removeAll();
		scrollPane.getViewport().add(contentPanel);
	}


	
	private Object[][] createTableData(Metadata metadata) {
		List<String[]> result = new ArrayList<String[]>();
		addStrings(metadata.getIdentifiers(), "Identifier", result);
		addStrings(metadata.getTitles(), "Title", result);
		addStrings(metadata.getAuthors(), "Author", result);
		result.add(new String[] {"Language", metadata.getLanguage()});
		addStrings(metadata.getContributors(), "Contributor", result);
		addStrings(metadata.getDescriptions(), "Description", result);
		addStrings(metadata.getPublishers(), "Publisher", result);
		addStrings(metadata.getDates(), "Date", result);
		addStrings(metadata.getSubjects(), "Subject", result);
		addStrings(metadata.getTypes(), "Type", result);
		addStrings(metadata.getRights(), "Rights", result);
		result.add(new String[] {"Format", metadata.getFormat()});
		return result.toArray(new Object[result.size()][2]);
	}

	private void addStrings(List<? extends Object> values, String label, List<String[]> result) {
		boolean labelWritten = false;
		for (int i = 0; i < values.size(); i++) {
			Object value = values.get(i);
			if (value == null) {
				continue;
			}
			String valueString = String.valueOf(value);
			if (StringUtils.isBlank(valueString))  {
				continue;
			}
			
			String currentLabel = "";
			if (! labelWritten) {
				currentLabel = label;
				labelWritten = true;
			}
			result.add(new String[] {currentLabel, valueString});
		}

	}
	
	private TableModel createTableModel(Navigator navigator) {
		return new AbstractTableModel() {
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getColumnCount() {
				return 2;
			}
		};
	}

	@Override
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		}
	}
}
