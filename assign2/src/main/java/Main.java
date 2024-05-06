import server.database.Database;
// import org.json.simple.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello, Gradle!");
        Database db = new Database();
        boolean res = db.register("user12", "password2");
        if(!res) System.out.println("shdgfgsh");
        db.save();
    }
}
