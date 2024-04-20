import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Main {

    private final JFrame frame;
    private DefaultTableModel resultTableModel;
    private final JPanel mainPanel;
    private Controller ctl;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main window = new Main();
            window.show();
        });
    }

    public void show() {
        frame.setVisible(true);
    }

    public Main() {

        frame = new JFrame("Modeling framework sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Select model and data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        String folderPathModels = "/Users/alexkononon/Documents/UTP/UTP7_KA_S28228/UTP7_KA_S28228/src/models";
        File folderModels = new File(folderPathModels);
        String folderPathData = System.getProperty("user.home") + "/Modeling/data/";
        File folderData = new File(folderPathData);

        String[] modelsWithExtension = folderModels.list();
        String[] data = folderData.list();

        if (modelsWithExtension != null && data != null) {
            String[] models = new String[modelsWithExtension.length];
            ArrayList<String> filteredModelsList = new ArrayList<>();

            for (int i = 0; i < modelsWithExtension.length; i++) {
                String modelFileName = modelsWithExtension[i];

                if (!modelFileName.equals("Bind.java")) {
                    models[i] = modelFileName.replace(".java", "");
                    filteredModelsList.add(models[i]);
                }
            }

            String[] filteredModels = filteredModelsList.toArray(new String[0]);

            JList<String> modelList = new JList<>(filteredModels);

            DefaultListModel<String> dataListModel = new DefaultListModel<>();

            for (String fileName : data) {
                if (fileName.endsWith(".txt")) {
                    dataListModel.addElement(fileName);
                }
            }
            JList<String> dataList = new JList<>(dataListModel);

            modelList.setPrototypeCellValue("12345670000");
            dataList.setPrototypeCellValue("12345670000");

            JScrollPane modelScrollPane = new JScrollPane(modelList);
            JScrollPane dataScrollPane = new JScrollPane(dataList);

            leftPanel.add(modelScrollPane, BorderLayout.WEST);
            leftPanel.add(dataScrollPane, BorderLayout.EAST);

            JPanel listAndButtonPanel = new JPanel(new BorderLayout());

            listAndButtonPanel.add(leftPanel, BorderLayout.CENTER);

            JButton runButton = new JButton("Run Model");

            resultTableModel = new DefaultTableModel();

            runButton.addActionListener(e -> {
                if (resultTableModel.getRowCount() > 0) {
                    resultTableModel.setRowCount(0);
                    resultTableModel.setColumnIdentifiers(new Object[]{});
                }
                String selectedModel = modelList.getSelectedValue();
                String selectedData = dataList.getSelectedValue();
                if (selectedModel != null && selectedData != null) {
                    ctl = new Controller(selectedModel);
                    ctl.readDataFrom(System.getProperty("user.home") + "/Modeling/data/" + selectedData)
                            .runModel();

                    String res = ctl.getResultsAsTsv();
                    String[] lines = res.split("\\n");
                    String[] columns = lines[0].split("\\t");
                    for (String column : columns) {
                        resultTableModel.addColumn(column);
                    }
                    for (int i = 1; i < lines.length; i++) {
                        resultTableModel.addRow(lines[i].split("\\t"));
                    }
                    createResultTableAndModel();
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select model and data.");
                }
            });

            listAndButtonPanel.add(runButton, BorderLayout.SOUTH);
            mainPanel.add(listAndButtonPanel, BorderLayout.WEST);
            frame.getContentPane().add(mainPanel);
        }
    }

    private void createResultTableAndModel() {
        JTable resultTable = new JTable(resultTableModel);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(Color.BLACK);
        resultTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        resultTable.setEnabled(false);
        JTableHeader header = resultTable.getTableHeader();
        header.setDefaultRenderer(new CustomTableCellRenderer.DefaultTableCellHeaderRenderer());
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton leftButton = new JButton("Run script from file");
        buttonPanel.add(leftButton);
        JButton rightButton = new JButton("Create and run hoc script");
        buttonPanel.add(rightButton);
        leftButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("Groovy files", "groovy");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                ctl.runScriptFromFile(fileChooser.getSelectedFile().getAbsolutePath());
                reprintOurInfoOnTable();

            }
        });
        rightButton.addActionListener(e -> {

            JTextArea textArea = new JTextArea(20, 40);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);


            JScrollPane scrollPane = new JScrollPane(textArea);


            int option = JOptionPane.showOptionDialog(
                    frame,
                    scrollPane,
                    "Script",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);

            if (option == JOptionPane.OK_OPTION) {

                String hocScript = textArea.getText();

                ctl.runScript(hocScript);

                reprintOurInfoOnTable();
            }
        });
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void reprintOurInfoOnTable() {
        String res = ctl.getResultsAsTsv();
        resultTableModel.setRowCount(0);
        resultTableModel.setColumnIdentifiers(new Object[]{});
        String[] lines = res.split("\\n");
        String[] columns = lines[0].split("\\t");
        for (String column : columns) {
            resultTableModel.addColumn(column);
        }
        for (int i = 1; i < lines.length; i++) {
            resultTableModel.addRow(lines[i].split("\\t"));
        }
    }
        ////////////////////////////////// Some design features //////////////////////////////////
        private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               // FORMAT TEXT
                if (value instanceof String stringValue) {
                    try {
                        double numberValue = Double.parseDouble(stringValue);
                        String formattedValue;
                        if (numberValue == (int) numberValue) {
                            formattedValue = formatDecimalNumber((int)  numberValue);
                        } else {
                            formattedValue = formatDecimalNumber(numberValue);
                        }
                        setText(formattedValue);
                    } catch (NumberFormatException e) {
                        setText(stringValue);
                    }
                }
                if (row % 2 == 0) {
                    rendererComponent.setBackground(Color.WHITE);
                } else {
                    rendererComponent.setBackground(new Color(240, 240, 240));
                }
                if (column != 0) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return rendererComponent;
            }
            private String formatDecimalNumber(int numberValue) {
                DecimalFormat threeeDecimalFormat = new DecimalFormat("#,###");
                DecimalFormat twoDecimalFormat = new DecimalFormat("#,###");
                DecimalFormat oneDecimalFormat = new DecimalFormat("#,###");
                if (numberValue < 1) {
                    return threeeDecimalFormat.format(numberValue);
                } else if (numberValue < 100) {
                    return twoDecimalFormat.format(numberValue);
                } else {
                    return oneDecimalFormat.format(numberValue);
                }
            }
            private String formatDecimalNumber(double numberValue) {
                DecimalFormat threeeDecimalFormat = new DecimalFormat("#,###.###");
                DecimalFormat twoDecimalFormat = new DecimalFormat("#,###.##");
                DecimalFormat oneDecimalFormat = new DecimalFormat("#,###.#");
                if (numberValue < 1) {
                    return threeeDecimalFormat.format(numberValue);
                } else if (numberValue < 100) {
                    return twoDecimalFormat.format(numberValue);
                } else {
                    return oneDecimalFormat.format(numberValue);
                }
            }

            private static class DefaultTableCellHeaderRenderer extends DefaultTableCellRenderer {
            public DefaultTableCellHeaderRenderer() {
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(new Color(240, 240, 240));
            }
        }
    }
}
