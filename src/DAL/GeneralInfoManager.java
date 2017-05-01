package DAL;


import BE.User;
import BE.Volunteer;
import BE.Admin;
import BE.Manager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class GeneralInfoManager extends ConnectionManager
{
    
    /**
     * Gets user information from the database using userId. Returns volunteer, manager or admin depending on type variable.
     * @param userId
     * @return 
     */
    public User getUserInfo(int userId) 
    {
        //String query = "select [user].[name], [user].[email],[user].phone From [user] where [user].userid ="+userId;
        String query = "select [user].* from [user] where [user].userid =" +userId;
        
        try(Connection con = super.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(query);
            System.out.println(query);
            System.out.println(rs);
            
            
            while(rs.next())
            {
                int id = rs.getInt("userid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int type = rs.getInt("type");
                int phone = rs.getInt("phone");
                String note = rs.getString("note");
                String residence =rs.getString("residence");
                
                //If it's a volunteer
                if(type == 0)
                {
                    System.out.println("type0");
                    Volunteer volunteer = null;
                    //(id, name, email, password, type, phone, note);
                    volunteer = new Volunteer(id, name, email, type, phone, note, residence);
                    
                    System.out.println("Volunteer info: " +volunteer.getName());
                    
                    return volunteer;
                }
                   
                //If it's a manager
                if(type == 1)
                {
                    System.out.println("type1");
                    Manager manager = null; 
                    //(id, name, email, password, type, phone, note);
                    manager = new Manager(id, name, email, type, phone, note, residence);
                    
                    System.out.println("Manager info: " +manager.getName());
                    
                    return manager;
                }
                
                //If it's an admin
                if(type == 2)
                {
                    System.out.println("type2");
                    Admin admin = null;
                    //(id, name, email, password, type, phone, note);
                    admin = new Admin(id, name, email, type, phone, note, residence);
                    
                    System.out.println("Admin info: " +admin.getName());
                    
                    return admin;
                } 
            }
            
        }catch(SQLException e) {
            System.out.println("Exception in DataManager: getUserInfo method.");
            System.out.println(e);
        }
        return null;
    }
    
    public List<User> getAllUsers()
    {
        ArrayList<User> users = new ArrayList<>();
        String query = "select * from [user]";
        try(Connection con = super.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                
                int id = rs.getInt("userid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int type = rs.getInt("type");
                int phone = rs.getInt("phone");
                String note = rs.getString("note");
                String residence = rs.getString("residence");
                

                switch (type)
                {
                    case 0:
                        System.out.println("type0");
                        Volunteer volunteer = null;
                        //(id, name, email, password, type, phone, note);
                        volunteer = new Volunteer(id, name, email, type, phone, note, residence);
                        users.add(volunteer);
                        System.out.println("Volunteer " + volunteer.getName()+" added to the list");
                        break;
                        
                    case 1:
                        System.out.println("type1");
                        Manager manager = null;
                        //(id, name, email, password, type, phone, note);
                        manager = new Manager(id, name, email, type, phone, note, residence);
                        users.add(manager);
                        System.out.println("Manager " +manager.getName()+" added to the list");
                        break;

                    case 2:
                        System.out.println("type2"); 
                        Admin admin = null;
                        //(id, name, email, password, type, phone, note);
                        admin = new Admin(id, name, email, type, phone, note, residence);
                        users.add(admin);
                        System.out.println("Admin "+admin.getName()+" added to the list");
                        break;
                        
                    default:
                        break;
                }
            }
            return users;
        } catch(SQLException e) {
            System.err.println("Exception in: DataManager: getAllUsers method.");
            System.out.println(e);
        }
        
        return null;
    }
    
    public List<User> getAllVolunteers()
    {
        ArrayList<User> volunteers = new ArrayList<>();
        String query = "select * from [user] where [user].[type] = 0";
        try(Connection con = super.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                
                int id = rs.getInt("userid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int type = rs.getInt("type");
                int phone = rs.getInt("phone");
                String note = rs.getString("note");
                String residence = rs.getString("residence");

                Volunteer volunteer = null;
                //(id, name, email, password, type, phone, note);
                volunteers.add(new Volunteer(id, name, email, type, phone, note, residence));
                System.out.println("Volunteer " + volunteer.getName()+" added to the list");

            }
        } catch(SQLException e) {
            System.err.println("Exception in: DataManager: getAllVolunteers method.");
            System.out.println(e);
        }
        
        return volunteers;
    }
    
    public List<User> getAllManagers()
    {
        ArrayList<User> managers = new ArrayList<>();
        String query = "select * from [user] where [user].[type] = 1";
        try(Connection con = super.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                
                int id = rs.getInt("userid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int type = rs.getInt("type");
                int phone = rs.getInt("phone");
                String note = rs.getString("note");
                String residence = rs.getString("residence");

                Manager manager = null;
                //(id, name, email, password, type, phone, note);
                managers.add(new Manager(id, name, email, type, phone, note, residence));
                System.out.println("Volunteer " + manager.getName()+" added to the list");

            }
        } catch(SQLException e) {
            System.err.println("Exception in: DataManager: getAllManagers method.");
            System.out.println(e);
        }
        
        return managers;
    }
    
    public List<User> getAllAdmins()
    {
        ArrayList<User> admins = new ArrayList<>();
        String query = "select * from [user] where [user].[type] = 2";
        try(Connection con = super.getConnection()) {
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(query);
            while(rs.next()) {
                
                int id = rs.getInt("userid");
                String name = rs.getString("name");
                String email = rs.getString("email");
                int type = rs.getInt("type");
                int phone = rs.getInt("phone");
                String note = rs.getString("note");
                String residence = rs.getString("residence");

                Admin admin = null;
                //(id, name, email, password, type, phone, note);
                admins.add(new Admin(id, name, email, type, phone, note, residence));
                System.out.println("Volunteer " + admin.getName()+" added to the list");

            }
        } catch(SQLException e) {
            System.err.println("Exception in: DataManager: getAllManagers method.");
            System.out.println(e);
        }
        
        return admins;
    }
    
    public void updateUserInfo(int userId, String name, String email, int type, int phone, String note, String residence) {
        //TODO
    }
}
