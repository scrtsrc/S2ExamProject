package BLL;

import BE.Day;
import BE.Guild;
import BE.User;
import DAL.DALFacade;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class BLLFacade
{

    private final static DALFacade DAL_FAC = new DALFacade();
    private final static LoginHandler LOG_HAND = new LoginHandler();

    public User getUserFromLogin(String username, String password)
    {
        return LOG_HAND.getUserFromLogin(username, password);
    }

    /**
     *
     * @param str
     * @param date
     * @param hours
     * @param guildId
     * @throws SQLException
     */
    public void logHours(String str, String date, int hours, int guildId) throws SQLException
    {
        DAL_FAC.logHours(str, date, hours, guildId);
    }

    public List<Guild> getAllGuilds()
    {
        return DAL_FAC.getAllGuilds();
    }

    public void addUser(String name, String email, String password, int type, int phone, String residence, String residence2, String note)
      {
        DAL_FAC.addUser(name, email, password, type, phone, residence, residence2, note);
      }

    public List<Day> getWorkedDays(User user)
    {
        return DAL_FAC.getWorkedDays(user);
    }


    public int changePassword(User user, String oldPassword, String newPassword)
      {
        return DAL_FAC.changePassword(user, oldPassword, newPassword);
      }

    public HashMap<String, String> loadSession()
      {
        return DAL_FAC.loadSession();
      }

    public Guild getGuild(int id)
      {
        return DAL_FAC.getGuild(id);
      }
    
    public void addGuild(String name)
    {
        DAL_FAC.addGuild(name);
    }
    
    public void deleteGuild(int guildId)
    {
        DAL_FAC.deleteGuild(guildId);
    }
    
    public void updateGuild(int guildId, String name)
    {
        DAL_FAC.updateGuild(guildId, name);
    }
  }
