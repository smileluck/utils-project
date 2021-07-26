package top.zsmile.runner;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public class ExcelLoadApplication {

    private int sheetIndex = 1;

    private String url = "C:\\Users\\Admin\\Desktop\\移动内部邮箱资料.xlsx";

    public static void main(String[] args) {
        try {
            ExcelLoadApplication excelLoadApplication = new ExcelLoadApplication();
            XSSFWorkbook workbook = excelLoadApplication.getWorkbook();
            excelLoadApplication.loadSheet3(workbook);
            workbook.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * 第0列为省份
     * 第6列为邮箱
     *
     * @param workbook
     * @throws IOException
     */
    public void loadSheet2(Workbook workbook) throws IOException {
        Sheet sheet = workbook.getSheetAt(this.sheetIndex);
        String dir = System.getProperty("user.dir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String sqlFile = dir + "\\email-" + simpleDateFormat.format(new Date()) + ".sql";

        BufferedWriter out = new BufferedWriter(new FileWriter(sqlFile));
        String provinceStr = null;
        for (Iterator rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = (Row) rowIterator.next();

            Cell provinceCell = row.getCell(0);
            if (provinceStr == null || (provinceCell != null && getCellValue(provinceCell) != null)) {
                provinceStr = getCellValue(provinceCell);
            }

            Cell email1Cell = row.getCell(6);
            if (email1Cell != null && getCellValue(email1Cell) != null) {
                String email1Str = getCellValue(email1Cell);
                String sql = "insert into send_email(email,province) values('" + email1Str + "','" + provinceStr + "');";
                out.write(sql + "\n");
            }

//            Cell email2Cell = row.getCell(11);
//            if (email2Cell != null && getCellValue(email2Cell) != null) {
//                String email2Str = getCellValue(email2Cell);
//                String sql = "insert into send_email(email,province) values('" + email2Str + "','" + provinceStr + "');";
//                out.write(sql + "\n");
//            }
        }
        System.out.println("文件路径：" + sqlFile);
        out.close();
    }


    /**
     * 第0列省份+第1列公司
     * 第5、8列为邮箱
     *
     * @param workbook
     * @throws IOException
     */
    public void loadSheet1(Workbook workbook) throws IOException {
        Sheet sheet = workbook.getSheetAt(this.sheetIndex);
        String dir = System.getProperty("user.dir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String sqlFile = dir + "\\email-" + simpleDateFormat.format(new Date()) + ".sql";

        BufferedWriter out = new BufferedWriter(new FileWriter(sqlFile));
        String provinceStr = null;
        String cityStr = null;
        for (Iterator rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = (Row) rowIterator.next();

            Cell provinceCell = row.getCell(0);
            if (provinceStr == null || (provinceCell != null && getCellValue(provinceCell) != null)) {
                provinceStr = getCellValue(provinceCell);
            }

            Cell cityCell = row.getCell(1);
            if (cityStr == null || (cityCell != null && getCellValue(cityCell) != null)) {
                cityStr = getCellValue(cityCell);
            }

            Cell email1Cell = row.getCell(5);
            if (email1Cell != null && getCellValue(email1Cell) != null) {
                String email1Str = getCellValue(email1Cell);
                email1Str = email1Str.replaceAll("\n", "");
                String sql = "insert into send_email(email,province) values('" + email1Str + "','" + provinceStr + "-" + cityStr + "');";
                out.write(sql + "\n");
            }

            Cell email2Cell = row.getCell(8);
            if (email2Cell != null && getCellValue(email2Cell) != null) {
                String email2Str = getCellValue(email2Cell);
                email2Str = email2Str.replaceAll("\n", "");
                String sql = "insert into send_email(email,province) values('" + email2Str + "','" + provinceStr + "-" + cityStr + "');";
                out.write(sql + "\n");
            }
        }
        System.out.println("文件路径：" + sqlFile);
        out.close();
    }

    /**
     * 第0列为省份
     * 第5、11列为邮箱
     *
     * @param workbook
     * @throws IOException
     */
    public void loadSheet(Workbook workbook) throws IOException {
        Sheet sheet = workbook.getSheetAt(this.sheetIndex);
        String dir = System.getProperty("user.dir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String sqlFile = dir + "\\email-" + simpleDateFormat.format(new Date()) + ".sql";

        BufferedWriter out = new BufferedWriter(new FileWriter(sqlFile));
        String provinceStr = null;
        for (Iterator rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = (Row) rowIterator.next();

            Cell provinceCell = row.getCell(0);
            if (provinceStr == null || (provinceCell != null && getCellValue(provinceCell) != null)) {
                provinceStr = getCellValue(provinceCell);
            }

            Cell email1Cell = row.getCell(5);
            if (email1Cell != null && getCellValue(email1Cell) != null) {
                String email1Str = getCellValue(email1Cell);
                String sql = "insert into send_email(email,province) values('" + email1Str + "','" + provinceStr + "');";
                out.write(sql + "\n");
            }

            Cell email2Cell = row.getCell(11);
            if (email2Cell != null && getCellValue(email2Cell) != null) {
                String email2Str = getCellValue(email2Cell);
                String sql = "insert into send_email(email,province) values('" + email2Str + "','" + provinceStr + "');";
                out.write(sql + "\n");
            }
        }
        System.out.println("文件路径：" + sqlFile);
        out.close();
    }


    /**
     * 从第925行开始
     * 第0列为省份
     * 第5、8列为邮箱
     *
     * @param workbook
     * @throws IOException
     */
    public void loadSheet3(Workbook workbook) throws IOException {
        Sheet sheet = workbook.getSheetAt(this.sheetIndex);
        String dir = System.getProperty("user.dir");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String sqlFile = dir + "\\email-" + simpleDateFormat.format(new Date()) + ".sql";

        BufferedWriter out = new BufferedWriter(new FileWriter(sqlFile));
        String provinceStr = null;
        for (int i = 924; i <= sheet.getLastRowNum(); i++) {

            Row row = (Row) sheet.getRow(i);

            if (row == null) {
                continue;
            }

            System.out.println(i);
            Cell provinceCell = row.getCell(0);
            if (provinceStr == null || (provinceCell != null && getCellValue(provinceCell) != null)) {
                provinceStr = getCellValue(provinceCell);
            }

            Cell email1Cell = row.getCell(5);
            if (email1Cell != null && getCellValue(email1Cell) != null) {
                String email1Str = getCellValue(email1Cell);
                String sql = "insert into send_email(email,province) values('" + email1Str + "','" + provinceStr + "');";
                out.write(sql + "\n");
            }

            Cell email2Cell = row.getCell(8);
            if (email2Cell != null && getCellValue(email2Cell) != null) {
                String email2Str = getCellValue(email2Cell);
                String sql = "insert into send_email(email,province) values('" + email2Str + "','" + provinceStr + "');";
                out.write(sql + "\n");
            }
        }
        System.out.println("文件路径：" + sqlFile);
        out.close();
    }


    public XSSFWorkbook getWorkbook() throws IOException {
        InputStream ips = new FileInputStream(this.url);
        XSSFWorkbook workbook = new XSSFWorkbook(ips);
        return workbook;
    }


    public String getCellValue(Cell cell) {
        String str = null;
        switch (cell.getCellType()) {
            case BOOLEAN:
                //得到Boolean对象的方法
//                System.out.print(cell.getBooleanCellValue() + " ");
                str = cell.getBooleanCellValue() + "";
                break;
            case NUMERIC:
                //先看是否是日期格式
                break;
            case FORMULA:
                //读取公式
//                System.out.print(cell.getCellFormula() + " ");
                str = cell.getCellFormula() + "";
                break;
            case STRING:
                //读取String
//                System.out.print(cell.getRichStringCellValue().toString() + " ");
                str = cell.getRichStringCellValue().toString();
                break;
        }
        return str;
    }

}
