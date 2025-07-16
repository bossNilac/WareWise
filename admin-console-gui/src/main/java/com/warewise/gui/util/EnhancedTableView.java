package com.warewise.gui.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A generic TableView builder that discovers all public getters on T
 * and creates one column per getter (minus getClass), with the ID column first.
 * Editable columns support inline text editing via JavaBean setters.
 * Other attributes appear in the exact order as declared in their class.
 */
public class EnhancedTableView<T> {

    private final TableView<T> tableView;
    private final ObservableList<T> data;

    public EnhancedTableView(TableView<T> tableView, List<T> items) {
        this.tableView = tableView;
        this.tableView.setEditable(true);
        this.data = FXCollections.observableArrayList(items);

        this.tableView.getColumns().clear();
        this.tableView.setItems(this.data);
        buildColumns();
    }

    private void buildColumns() {
        if (data.isEmpty()) return;
        Class<?> clazz = data.get(0).getClass();

        // 1) Add ID column first (non-editable)
        try {
            Method idGetter = clazz.getMethod("getID");
            if (isGetter(idGetter)) {
                addColumnFor(idGetter, false);
            }
        } catch (NoSuchMethodException ignored) {}

        // 2) Add other fields in declared order
        for (Field f : clazz.getDeclaredFields()) {
            String fieldName = f.getName();
            // skip static or synthetic or the ID field
            if (Modifier.isStatic(f.getModifiers()) || f.isSynthetic() || fieldName.equalsIgnoreCase("id")) {
                continue;
            }
            // build getter name
            String getterName = "get" + capitalize(fieldName);
            try {
                Method m = clazz.getMethod(getterName);
                if (isGetter(m)) {
                    addColumnFor(m, true);
                }
            } catch (NoSuchMethodException ignored) {
                // no matching getter, skip
            }
        }
    }

    private void addColumnFor(Method getter, boolean editable) {
        String fieldName = getFieldName(getter);
        String displayName = capitalize(fieldName);
        TableColumn<T, String> col = new TableColumn<>(displayName);
        col.setEditable(editable);
        col.setCellValueFactory(cellData -> {
            T row = cellData.getValue();
            try {
                Object value = getter.invoke(row);
                return new SimpleStringProperty(value == null ? "" : value.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                return new SimpleStringProperty("");
            }
        });

        if (editable) {
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(event -> {
                T row = event.getRowValue();
                String newVal = event.getNewValue();
                try {
                    String setterName = "set" + capitalize(fieldName);
                    Method setter = row.getClass().getMethod(setterName, getter.getReturnType());
                    Object castVal = castValue(newVal, getter.getReturnType());
                    setter.invoke(row, castVal);
                } catch (NoSuchMethodException nsme) {
                    System.err.println("No setter for field " + fieldName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tableView.refresh();
            });
        }

        tableView.getColumns().add(col);
    }

    /** Casts the string input to the appropriate type for setter invocation */
    private Object castValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return value.isEmpty() ? 0L : Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return value.isEmpty() ? 0.0 : Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

    public Object getSelectedId() {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        try {
            Method getId = selected.getClass().getMethod("getID");
            return getId.invoke(selected);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve ID from selected item", e);
        }
    }

    public T commitEditingRow() {
        tableView.edit(-1, null);
        return tableView.getSelectionModel().getSelectedItem();
    }

    public void updateSelectedRow(T newItem) {
        int idx = tableView.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            data.set(idx, newItem);
            tableView.getSelectionModel().clearSelection();
            tableView.refresh();
        }
    }

    @SuppressWarnings("unchecked")
    public T addEmptyRowForEditing() {
        try {
            Class<?> modelClass = data.isEmpty() ?
                    tableView.getColumns().get(0).getCellData(0).getClass() : data.get(0).getClass();
            T newItem = (T) modelClass.getDeclaredConstructor().newInstance();
            data.add(newItem);
            tableView.getSelectionModel().select(newItem);
            tableView.scrollTo(newItem);
            return newItem;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create an empty row for editing", e);
        }
    }

    public void refresh() {
        tableView.refresh();
    }

    public void refresh(List<T> newItems) {
        data.setAll(newItems);
        tableView.refresh();
    }

    public T deleteSelectedRow() {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            data.remove(selected);
            tableView.getSelectionModel().clearSelection();
            return selected;
        }
        return null;
    }

    private boolean isGetter(Method m) {
        return Modifier.isPublic(m.getModifiers())
                && m.getParameterCount() == 0
                && m.getName().matches("^get[A-Z].*")
                && !m.getName().equals("getClass");
    }

    private String getFieldName(Method m) {
        String withoutGet = m.getName().substring(3);
        return Character.toLowerCase(withoutGet.charAt(0)) + withoutGet.substring(1);
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public ObservableList<T> getItems() {
        return data;
    }
}
