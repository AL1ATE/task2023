import java.sql.*;

public class Main {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String JDBC_USER = "postgres";
    private static final String JDBC_PASSWORD = "123456789";

    public static void main(String[] args) {
        createGroup("GroupA");
        createStudent("John Doe", "GroupA");
        createTask("John Doe", 1, true);
        displayTasks("GroupA");
    }

    public static void createGroup(String groupName) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO groups (group_name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, groupName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createStudent(String studentName, String groupName) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO students (student_name, group_id) VALUES (?, (SELECT group_id FROM groups WHERE group_name = ?))";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, studentName);
                preparedStatement.setString(2, groupName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTask(String studentName, int taskNumber, boolean isCompleted) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "INSERT INTO tasks (student_id, task_number, is_completed) VALUES ((SELECT student_id FROM students WHERE student_name = ?), ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, studentName);
                preparedStatement.setInt(2, taskNumber);
                preparedStatement.setBoolean(3, isCompleted);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayTasks(String groupName) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String sql = "SELECT s.student_name, t.task_number, t.is_completed " +
                    "FROM students s " +
                    "JOIN tasks t ON s.student_id = t.student_id " +
                    "JOIN groups g ON s.group_id = g.group_id " +
                    "WHERE g.group_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, groupName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String studentName = resultSet.getString("student_name");
                        int taskNumber = resultSet.getInt("task_number");
                        boolean isCompleted = resultSet.getBoolean("is_completed");
                        System.out.println(studentName + " - Task " + taskNumber + ": " + (isCompleted ? "Completed" : "Not Completed"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}