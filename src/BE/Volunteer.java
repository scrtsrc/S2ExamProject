package BE;

import java.util.List;

public class Volunteer extends User
{

    public Volunteer(int id, String name, String email, int phone, String note, String residence, String residence2, List<Guild> guilds)
    {
        super(id, name, email, phone, note, residence, residence2, guilds);
        setType(0);
    }

}
