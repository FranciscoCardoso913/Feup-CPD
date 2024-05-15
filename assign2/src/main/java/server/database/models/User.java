package server.database.models;
import java.util.Objects;

public class User {
    private String name = "";
    private String password = "";
    private int score = 0;

    public User(String name, String password, int score){
        this.name = name;
        this.password = password;
        this.score = score;
    }

    public User(){}

    public String getName(){
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void changeScore(int delta){
        this.score += delta;
    }

    public boolean login( String password ) {
        return this.password.equals(password);
    }

    public boolean notEmpty(){
        return !this.name.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User myObject = (User) obj;
        return Objects.equals(name, myObject.name);
    }
}