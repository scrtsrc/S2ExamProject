/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import BE.User;
import DAL.DataManager;
import java.util.List;

/**
 *
 * @author Desmoswal
 */
public class DataHandler
{
    DataManager dataManager = new DataManager();
    
    public User getUserInfo(int userId)
    {
        return dataManager.getUserInfo(userId);
    }
    
    public List<User> getAllUsers()
    {
        return dataManager.getAllUsers();
    }
    
    public List<User> getAllVolunteers()
    {
        return dataManager.getAllVolunteers();
    }
    
    public List<User> getAllManagers()
    {
        return dataManager.getAllManagers();
    }
    
    public List<User> getAllAdmins()
    {
        return dataManager.getAllAdmins();
    }
    
    public void addUser(String name, String email, String password, int type, int phone, String address, String note)
    {
        dataManager.addUser(name, email, password, type, phone, address, note);
    }
    
    public void updateUserInfo(String name, String email, String password, int type, int phone, String address, String note, int userid)
    {
        dataManager.updateUserInfo(name, email, password, type, phone, address, note, userid);
    }
}
