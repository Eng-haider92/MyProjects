/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package certificateauthority;

import java.util.Date;

/**
 *
 * @author haider-Rajab
 */
public class UserCertificate {
    
    private String emailAddress;
    private String SN;
    private Date startDate;
    private Date expiredDate;
    private String status;
    
    public void setEmailAddress(String e){
        emailAddress = e;   }
    
    public void setSN(String t){
        SN = t;}
    
    public void setStartDate(Date d){
        startDate = d;}
    
    public void setExpiredDate(Date d){
        expiredDate = d;}
    
    public void setStatus(String b){
        status = b;}

    
    public String getEmailAddress(){
        return emailAddress;   }
    
    public String getSN(){
        return SN;}
    
    public Date getStartDate(){
        return startDate;}
    
    public Date getExpiredDate(){
        return expiredDate;}
    
    public String getStatus(){
        return status;}
    
            
}
