/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author haider-Rajab
 */
public class Report {
    
    private int Id;
    private String firstName;
    private String lastName;
    private String temperature;
    private String pressure;
    private String status;
    
    
    public void setId(int id){
        Id = id;
    }
    
    public void setFirstName(String s){
        firstName = s;
    }
    
    public void setLastName(String s){
        lastName = s;
    }
    
    public void setTemperature(String s){
        temperature = s;
    }
    
    public void setPressure(String s){
        pressure = s;
    }
    
    public void setStatus(String s){
        status = s;
    }
    
    
    
    public int getId(){
      return Id;}
    
    public String getFirstName(){
      return firstName;}
    
    public String getLastName(){
      return lastName;}
    
    public String getTemperature(){
      return temperature;}
    
    public String getPressure(){
      return pressure;}
    
    public String getStatus(){
      return status;}
  
    
    
   
}
