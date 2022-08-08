package fsktm.um.edu.a2fyp.Models;

import java.io.Serializable;

public class Users implements Serializable {


    public String username, img, email, token, id;

    public Users() {

    }

    public Users(String username, String img, String email, String token, String id) {
        this.username = username;
        this.img = img;
        this.email = email;
        this.token = token;
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getImg() {
        return img;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }
}
