package server.database.models;
import java.util.Objects;

public class Student {
    private String name;
    private String password;
    public Student(String name , String password){
        this.name = name;
        this.password = password;
    }

    public String getName(){
        return this.name;
    }
    public String getPassword() {
        return this.password;
    }

    public boolean login( String password ) {
        return this.password.equals(password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student myObject = (Student) obj;
        return Objects.equals(name, myObject.name);
    }
}
