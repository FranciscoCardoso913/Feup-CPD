package server.database.models;
import utils.Security;

import java.util.Objects;
import java.security.NoSuchAlgorithmException;
public class User {
    private String name = null;
    private String password = null;
    private int score = 0;
    /**
     * Constructor for the User class.
     *
     * @param name The name of the user.
     * @param password The password of the user.
     * @param score The score of the user.
     */
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
    /**
     * Changes the score of the user by the specified delta.
     *
     * @param delta The change in score.
     */
    public void changeScore(int delta){
        this.score += delta;
        if(this.score<0) this.score=0;
    }
    /**
     * Checks if the provided password matches the user's password.
     *
     * @param password The password to check.
     * @return True if the passwords match, false otherwise.
     */
    public boolean login( String password ) {
        try {
            return this.password.equals(Security.hash(password));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Encrypts User password
     */
    public void hashPassword(){
        try {
            this.password = Security.hash(this.password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Checks if the user object is empty (i.e., has no name).
     *
     * @return True if the user is empty, false otherwise.
     */
    public boolean isEmpty(){
        return this.name == null;
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
