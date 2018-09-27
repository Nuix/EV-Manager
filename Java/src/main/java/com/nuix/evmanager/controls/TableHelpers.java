package com.nuix.evmanager.controls;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/***
 * A class providing some helper methods regarding JTable controls
 * @author Jason Wells
 *
 */
public class TableHelpers {
	//Attempt to size columns to content.  Only fist 100 rows or less are measured
	//to prevent resizing code to cause the table from paging in the entire
	//set of records
	public static void resizeColumnWidth(JTable table) {
		if(table.getRowCount() > 0){
		    final TableColumnModel columnModel = table.getColumnModel();
		    for (int column = 0; column < table.getColumnCount(); column++) {
		        int width = 40; // Minimum width
		        for (int row = 0; row < table.getRowCount() && row < 100; row++) {
		            TableCellRenderer renderer = table.getCellRenderer(row, column);
		            Component comp = table.prepareRenderer(renderer, row, column);
		            width = Math.max(comp.getPreferredSize().width +1, width);
		        }
		        
		        columnModel.getColumn(column).setPreferredWidth(width);
		    }
		}
	}
	
	public static void scrollToTop(JTable table){
		table.scrollRectToVisible(new Rectangle(table.getCellRect(0, 0, true)));
	}
}
