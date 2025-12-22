package vn.iuh.util;

import vn.iuh.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestoreDataBase {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void restoreFullAndDiff(String fullBackupPath, String diffBackupPath) {
        try (Connection conn = DatabaseUtil.getConnect()) {
            Statement stmt = conn.createStatement();
            stmt.execute("USE MASTER");
            stmt.execute("ALTER DATABASE [QLKS] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;");
            stmt.execute(
                    "RESTORE DATABASE [QLKS] FROM DISK = '" + fullBackupPath + "' " +
                            "WITH REPLACE, NORECOVERY;"
            );
            stmt.execute(
                    "RESTORE DATABASE [QLKS] FROM DISK = '" + diffBackupPath + "' " +
                            "WITH REPLACE, RECOVERY;"
            );
            stmt.execute("ALTER DATABASE [QLKS] SET MULTI_USER;");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Restore full+diff failed: " + e.getMessage());
        }
    }

    public static boolean canRestoreWithNearestFull(String fullFile, String difFile) {
        LocalDate fullDate = extractFullDate(fullFile);
        LocalDate difDate  = extractDifDate(difFile);

        if (fullDate == null || difDate == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }

        LocalDate nextFullDate = fullDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        return !difDate.isBefore(fullDate) && difDate.isBefore(nextFullDate);
    }

    private static LocalDate extractFullDate(String fileName) {
        if (fileName == null || !fileName.contains("FULL")) return null;
        return extractDate(fileName);
    }

    private static LocalDate extractDifDate(String fileName) {
        if (fileName == null || !fileName.contains("DIF")) return null;
        return extractDate(fileName);
    }

    private static LocalDate extractDate(String fileName) {
        Pattern p = Pattern.compile("(\\d{2}-\\d{2}-\\d{4})");
        Matcher m = p.matcher(fileName);

        if (!m.find()) return null;

        return LocalDate.parse(m.group(1), FORMAT);
    }
}