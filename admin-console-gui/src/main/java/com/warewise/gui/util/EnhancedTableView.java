package com.warewise.gui.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;

public class EnhancedTableView {

    private ObservableList<String[]> data; // Data backing the table
    private List<TableColumn<String[], String>> columnList; // List of columns
    private final String name;
    private TableView<String[]> tableView;

    // Fields for handling a new editable row
    private int editingRowIndex = -1;
    private String[] backupForEditingRow = null;

    /**
     * Constructor that builds the table columns and initializes the data.
     * @param tableView The TableView instance.
     * @param columnNo The number of columns.
     * @param columnNames The names of the columns.
     * @param data The initial data (list of String[] rows).
     */
    public EnhancedTableView(TableView<String[]> tableView, int columnNo, String[] columnNames, List<String[]> data) {
        this.tableView = tableView;
        this.name = tableView.getId();
        this.data = FXCollections.observableArrayList(data);
        this.columnList = new ArrayList<>();
        this.buildColumns(columnNo, columnNames);
        this.tableView.setItems(this.data);
        this.tableView.setEditable(true);
    }

    /**
     * Builds the table columns programmatically.
     * @param columnNo Number of columns.
     * @param columnNames Array of column names.
     */
    public void buildColumns(int columnNo, String[] columnNames) {
        // Clear any existing columns
        tableView.getColumns().clear();
        columnList.clear();

        for (int i = 0; i < columnNo; i++) {
            final int colIndex = i; // For lambda capture
            TableColumn<String[], String> col = new TableColumn<>(columnNames[i]);
            // Set cell value factory to extract the i-th element from the String[] row.
            col.setCellValueFactory(cellData -> {
                String[] row = cellData.getValue();
                String cellValue = (row != null && row.length > colIndex) ? row[colIndex] : "";
                return new SimpleStringProperty(cellValue);
            });
            // Enable in-cell editing with a TextField.
            col.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
            col.setOnEditCommit(event -> {
                String[] row = event.getRowValue();
                if (row != null && row.length > colIndex) {
                    row[colIndex] = event.getNewValue();
                }
            });
            columnList.add(col);
        }
        // Add all columns to the table.
        tableView.getColumns().addAll(columnList);
    }

    /**
     * Adds a new row to the table.
     * @param newData The new row data as a String array.
     */
    public void addRow(String[] newData) {
        data.add(newData);
        tableView.refresh();
    }

    /**
     * Updates the currently selected row with new data.
     * @param newData The new row data as a String array.
     */
    public void updateSelectedRow(String[] newData) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            data.set(selectedIndex, newData);
            tableView.refresh();
        }
    }

    /**
     * Deletes the currently selected row.
     */
    public String[] deleteSelectedRow() {
        String[] deletedData = null;
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            deletedData = data.get(selectedIndex);
            data.remove(selectedIndex);
            tableView.refresh();
        }
        return deletedData;
    }

    public String getName() {
        return name;
    }

    /**
     * Creates an empty row (all cells are empty) and sets it up for editing.
     * Only one new row is allowed at a time.
     */
    public void addEmptyRowForEditing() {
        if (editingRowIndex != -1) {
            // There's already an empty row being edited.
            return;
        }
        int columns = columnList.size();
        String[] newRow = new String[columns];
        // Initialize with empty strings.
        for (int i = 0; i < columns; i++) {
            newRow[i] = "";
        }
        data.add(newRow);
        // Store the index and a backup copy of the new row.
        editingRowIndex = data.size() - 1;
        backupForEditingRow = newRow.clone();
        tableView.refresh();
    }

    /**
     * Validates and commits the currently editing row.
     * - All cells must be non-empty.
     * - The row is compared with its initial backup.
     * If changes were made and the row is complete, the method returns the new row.
     * Otherwise, it returns null.
     *
     * @return The new row data as String[] if committed; otherwise, null.
     */
    public String[] commitEditingRow() {
        if (editingRowIndex < 0 || editingRowIndex >= data.size()) {
            return null;
        }
        String[] editedRow = data.get(editingRowIndex);

        // Validate: all cells must be filled (non-null and not just whitespace)
        for (String cell : editedRow) {
            if (cell == null || cell.trim().isEmpty()) {
                // Incomplete row: changes won't be saved.
                return null;
            }
        }

        // Compare the edited row with the backup
        boolean changed = false;
        for (int i = 0; i < editedRow.length; i++) {
            if (!editedRow[i].equals(backupForEditingRow[i])) {
                changed = true;
                break;
            }
        }

        if (changed) {
            // Clear editing markers
            int committedRowIndex = editingRowIndex;
            editingRowIndex = -1;
            backupForEditingRow = null;
            // Optionally, you could perform additional logic here (e.g., mark the row as committed).
            return data.get(committedRowIndex);
        } else {
            // No changes were made compared to the initial empty row.
            return null;
        }
    }

    public void refresh(){
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        tableView.refresh();
    }

}
