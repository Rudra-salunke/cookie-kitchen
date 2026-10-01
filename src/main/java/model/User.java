package model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password_hash;
    private String phone;
    private String role;
    private String created_at;
    public User(int id, String name, String email, String password_hash,
                   String phone , String role, String created_at) {
        this.id = id;
        this.name = name;
        this.email= email;
        this.password_hash=password_hash;
        this.phone=phone;
        this.role=role;
        this.created_at=created_at;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){

        this.name=name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPassword_hash(){

        return password_hash;
    }
    public void setPassword_hash(String password_hash){

        this.password_hash=password_hash;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role=role;
    }
    public String getCreated_at (){
        return created_at;
    }
    public void setCreated_at(String created_at){
        this.created_at=created_at;
    }
}
