import database.DBConnection;
public class Test {

    public static void main(String[] args) {

        var connection = DBConnection.getConnection();

        if (connection != null) {

            System.out.println("Car Rental System is ready!");

            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            System.out.println("Check your database settings!");
        }
    }
}
