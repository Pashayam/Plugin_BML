package com.blockset.ui;

import com.blockset.ui.parser.domain.Block;
import com.blockset.ui.parser.domain.Set;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SetTable {
    private DefaultTableModel tableModel;
    private JPanel jPanel;
    private JTable table1;
    private JButton add;
    private ArrayList<ArrayList<String>> array = new ArrayList<>();
    // Заголовки столбцов
//    private Object[] columnsHeader = new String[] {"Наименование", "Ед.измерения", "Количество"};
    private ArrayList<String> columnsHeader = new ArrayList<>();
    public JPanel getjPanel(Set s)
    {
        columnsHeader.add("Id");
        List<Block> blocks = s.getBlocks();
        for (Block b:blocks) {
            columnsHeader.add(b.getName());
        }
        table1.setFillsViewportHeight(true);
        // Создание стандартной модели
        tableModel = new DefaultTableModel();
        // Определение столбцов
        tableModel.setColumnIdentifiers(columnsHeader.toArray());
        // Наполнение модели данными
        for (ArrayList<String> str: array) {
            tableModel.addRow(str.toArray());
        }
//        for (int i = 0; i < array.length; i++)
//            tableModel.addRow(array[i]);

        // Создание таблицы на основании модели данных
        table1.setModel(tableModel);
        // Создание кнопки добавления строки таблицы
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Номер выделенной строки
                int idx = table1.getSelectedRow();
                // Вставка новой строки после выделенной

                tableModel.insertRow(table1.getRowCount(), new String[] {
                        String.valueOf(table1.getRowCount())});
            }
        });
        // Создание кнопки удаления строки таблицы
//        remove.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
                // Номер выделенной строки
//                int idx = table1.getSelectedRow();
                // Удаление выделенной строки
//                tableModel.removeRow(idx);
//            }
//        });

        return jPanel;
    }
}
