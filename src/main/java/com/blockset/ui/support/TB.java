package com.blockset.ui.support;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TB {
    private DefaultTableModel tableModel;
    private JTable table1;
    private JPanel jPanel;
    // Данные для таблиц
    private Object[][] array = new String[][] {{ "Сахар" , "кг", "1.5" },
            { "Мука"  , "кг", "4.0" },
            { "Молоко", "л" , "2.2" }};
    // Заголовки столбцов
    private Object[] columnsHeader = new String[] {"Наименование", "Ед.измерения", "Количество"};

    public JPanel getjPanel()
    {
        jPanel = new JPanel();
        // Создание стандартной модели
        tableModel = new DefaultTableModel();
        // Определение столбцов
        tableModel.setColumnIdentifiers(columnsHeader);
        // Наполнение модели данными
        for (int i = 0; i < array.length; i++)
            tableModel.addRow(array[i]);

        // Создание таблицы на основании модели данных
        table1 = new JTable(tableModel);
        // Создание кнопки добавления строки таблицы
        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Номер выделенной строки
                int idx = table1.getSelectedRow();
                // Вставка новой строки после выделенной
                tableModel.insertRow(idx + 1, new String[] {
                        "Товар №" + String.valueOf(table1.getRowCount()),
                        "кг", "Цена"});
            }
        });
        // Создание кнопки удаления строки таблицы
        JButton remove = new JButton("Удалить");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Номер выделенной строки
                int idx = table1.getSelectedRow();
                // Удаление выделенной строки
                tableModel.removeRow(idx);
            }
        });
        // Создание таблицы на основе модели данных
//        JTable table2 = new JTable(new SimpleModel());
        // Определение высоты строки
//        table2.setRowHeight(24);

        // Формирование интерфейса
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(table1));
//        contents.add(new JScrollPane(table2));
//        getContentPane().add(contents);

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(remove);
//        getContentPane().add(buttons, "South");
        // Вывод окна на экран
//        setSize(400, 300);
//        setVisible(true);
        jPanel.add(table1);
        return jPanel;
    }
    // Модель данных
    class SimpleModel extends AbstractTableModel
    {
        // Количество строк
        @Override
        public int getRowCount() {
            return 10;
        }
        // Количество столбцов
        @Override
        public int getColumnCount() {
            return 3;
        }
        // Тип хранимых в столбцах данных
        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 1: return Boolean.class;
                case 2: return Icon.class;
                default: return Object.class;
            }
        }
        // Функция определения данных ячейки
        @Override
        public Object getValueAt(int row, int column)
        {
            boolean flag = ( row % 2 == 1 ) ? true : false;
            // Данные для стобцов
            switch (column) {
                case 0: return "" + row;
                case 1: return new Boolean(flag);
                case 2: return new ImageIcon("images/copy.png");
            }
            return "Не определена";
        }
    }
}
