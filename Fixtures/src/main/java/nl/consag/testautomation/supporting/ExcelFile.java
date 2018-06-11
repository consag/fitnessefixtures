package nl.consag.testautomation.supporting;

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFile {

    private List<List<String>> tableExcelFile = new ArrayList<List<String>>();
    private List<List<Attribute>> tableExcelFileWithFormat = new ArrayList<List<Attribute>>();


    private String returnMessage = Constants.OK;

    public List<List<String>> readWorksheetWithNumber(String fileName, int worksheetNo)
        //Read a specific worksheet within excel spreadsheet, based on the worksheet number
        {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook based on worksheet no
            XSSFSheet sheet = workbook.getSheetAt(worksheetNo);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row input_row = rowIterator.next();
                tableExcelFile.add(readWorksheetRow(input_row));
            }
            file.close();
            setReturnMessage(Constants.OK);
        } catch (Exception e) {
            e.printStackTrace();
            setReturnMessage(e.toString());
        }
        return tableExcelFile;
    }

    public List<List<Attribute>> readWorksheetWithNumberIncludingFormat(String fileName, int worksheetNo)
        //Read a specific worksheet within excel spreadsheet, including the format of the cell based on the worksheet number
        {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook based on worksheet no
            XSSFSheet sheet = workbook.getSheetAt(worksheetNo);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row input_row = rowIterator.next();
                tableExcelFileWithFormat.add(readWorksheetRowWithFormat(input_row));
            }
            file.close();
            setReturnMessage(Constants.OK);
        } catch (Exception e) {
            e.printStackTrace();
            setReturnMessage(e.toString());
        }
        return tableExcelFileWithFormat;
    }

    public List<List<String>> readWorksheetWithName(String fileName, String worksheetName)
        //Read a specific worksheet within excel spreadsheet, based on the name of the worksheet
        {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook based on worksheet name
            XSSFSheet sheet = workbook.getSheet(worksheetName);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row input_row = rowIterator.next();
                tableExcelFile.add(readWorksheetRow(input_row));
            }
            file.close();
            setReturnMessage(Constants.OK);
        } catch (Exception e) {
            e.printStackTrace();
            setReturnMessage(e.toString());
        }
        return tableExcelFile;
    }

    public List<List<Attribute>> readWorksheetWithNameIncludingFormat(String fileName, String worksheetName)
        //Read a specific worksheet within excel spreadsheet, including the format of the cell based on the name of the worksheet
        {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));

            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first/desired sheet from the workbook based on worksheet no
            XSSFSheet sheet = workbook.getSheet(worksheetName);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row input_row = rowIterator.next();
                tableExcelFileWithFormat.add(readWorksheetRowWithFormat(input_row));
            }
            file.close();
            setReturnMessage(Constants.OK);
        } catch (Exception e) {
            e.printStackTrace();
            setReturnMessage(e.toString());
        }
        return tableExcelFileWithFormat;
    }

    private List<String> readWorksheetRow(Row input_row) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> row = new ArrayList<String>();
        //For each row, iterate through all the cells
        for (int cn = 0; cn < input_row.getLastCellNum(); cn++) {
            // If the cell is missing from the file, generate a blank one
            Cell cell = input_row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
            //Check the cell type and format accordingly
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) { // In case cell value is the result of a formula
                switch (cell.getCachedFormulaResultType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getDateCellValue() == null) {
                            row.add("");
                        } else {
                            row.add(sdf.format(cell.getDateCellValue()).toString());
                        }
                    } else {
                        row.add(Double.toString(cell.getNumericCellValue()));
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    if (cell.getBooleanCellValue()) {
                        row.add("TRUE");
                    } else {
                        row.add("FALSE");
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    String s = cell.getStringCellValue();
                    if (s.length() == 0) {
                        row.add("");
                    } else {
                        if (s.length() < 3) {
                            row.add(s);
                        } else { // Check if escape character was used to bypass formatting of a integer
                            if (s.substring(0, 2).equals("^^") && Character.isDigit(s.charAt(2))) {
                                s = s.substring(2); //remove escape character ^^
                            }
                            row.add(s);
                        }
                    }
                default:
                    row.add("");
                    break;
                }
            } else { // In case cell contains a value.
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getDateCellValue() == null) {
                            row.add("");
                        } else {
                            row.add(sdf.format(cell.getDateCellValue()).toString());
                        }
                    } else {
                        row.add(Double.toString(cell.getNumericCellValue()));
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    if (cell.getBooleanCellValue()) {
                        row.add("TRUE");
                    } else {
                        row.add("FALSE");
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    String s = cell.getStringCellValue();
                    if (s.length() == 0) {
                        row.add("");
                    } else {
                        if (s.length() < 3) {
                            row.add(s);
                        } else { // Check if escape character was used to bypass formatting of a integer
                            if (s.substring(0, 2).equals("^^") && Character.isDigit(s.charAt(2))) {
                                s = s.substring(2); //remove escape character ^^
                            }
                            row.add(s);
                        }
                    }
                    break;
                default:
                    row.add("");
                    break;
                }
            }
        }
        return row;
    }

    private List<Attribute> readWorksheetRowWithFormat(Row input_row) {
        List<Attribute> row = new ArrayList<Attribute>();
        //For each row, iterate through all the cells
        for (int cn = 0; cn < input_row.getLastCellNum(); cn++) {
            Attribute attribute = new Attribute();
            // If the cell is missing from the file, generate a blank one
            Cell cell = input_row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
            //Check the cell type and format accordingly
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) { // In case cell value is the result of a formula
                switch (cell.getCachedFormulaResultType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getDateCellValue() == null) {
                            attribute.setFormat("STRING");
                            attribute.setText("");
                        } else {
                            attribute.setFormat("DATE");
                            attribute.setDate(cell.getDateCellValue());
                        }
                    } else {
                        attribute.setFormat("NUMERIC");
                        attribute.setNumber(cell.getNumericCellValue());
                    }
                    row.add(attribute);
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    attribute.setFormat("BOOLEAN");
                    if (cell.getBooleanCellValue()) {
                        attribute.setText("TRUE");
                    } else {
                        attribute.setText("FALSE");
                    }
                    row.add(attribute);
                    break;
                case Cell.CELL_TYPE_STRING:
                    attribute.setFormat("STRING");
                    String s = cell.getStringCellValue();
                    if (s.length() == 0) {
                        attribute.setText("");
                    } else {
                        if (s.length() < 3) {
                            attribute.setText(s);
                        } else { // Check if escape character was used to bypass formatting of a integer
                            if (s.substring(0, 2).equals("^^") && Character.isDigit(s.charAt(2))) {
                                s = s.substring(2); //remove escape character ^^
                            }
                            attribute.setText(s);
                        }
                        row.add(attribute);
                    }
                    break;
                default:
                    attribute.setFormat("UNKNOWN");
                    attribute.setText("");
                    row.add(attribute);
                    break;
                }
            } else { // In case cell contains a value.
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (cell.getDateCellValue() == null) {
                            attribute.setFormat("STRING");
                            attribute.setText("");
                        } else {
                            attribute.setFormat("DATE");
                            attribute.setDate(cell.getDateCellValue());
                        }
                    } else {
                        attribute.setFormat("NUMERIC");
                        attribute.setNumber(cell.getNumericCellValue());
                    }
                    row.add(attribute);
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    attribute.setFormat("BOOLEAN");
                    if (cell.getBooleanCellValue()) {
                        attribute.setText("TRUE");
                    } else {
                        attribute.setText("FALSE");
                    }
                    row.add(attribute);
                    break;
                case Cell.CELL_TYPE_STRING:
                    attribute.setFormat("STRING");
                    String s = cell.getStringCellValue();
                    if (s.length() == 0) {
                        attribute.setText("");
                    } else {
                        if (s.length() < 3) {
                            attribute.setText(s);
                        } else { // Check if escape character was used to bypass formatting of a integer
                            if (s.substring(0, 2).equals("^^") && Character.isDigit(s.charAt(2))) {
                                s = s.substring(2); //remove escape character ^^
                            }
                            attribute.setText(s);
                        }
                        row.add(attribute);
                    }
                    break;
                default:
                    attribute.setFormat("UNKNOWN");
                    attribute.setText("");
                    row.add(attribute);
                    break;
                }
            }
        }
        return row;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public String getReturnMessage() {
        return returnMessage;
    }
}
