package wolper.logic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;



//Не пригодилось в игре! НО оставил этот класс "на память" о некоторых удачных решениях


@Service("loggedNames")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionServise {



    private SessionRegistry sessionRegistry;

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }





    public List<SessionInformation> getActiveSessions() {
        List<SessionInformation> activeSessions = new ArrayList();
        for(Object principal : sessionRegistry.getAllPrincipals()) {
            activeSessions.addAll(sessionRegistry.getAllSessions(principal, false));
        }
        return activeSessions;
    }


    public List<String> getLoggedUsersNames() {
        List<SessionInformation> session = getActiveSessions();
        List<String> result = new ArrayList<String>();
        for (SessionInformation sessionInformation : session) {
            Object principalObj = sessionInformation.getPrincipal();
            if (principalObj instanceof User) {
                User user = (User) principalObj;
                result.add(user.getUsername());
            }
        }
    return result;
    }


    public void expireUserSessions(String username) {
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof User) {
                    UserDetails userDetails = (UserDetails) principal;
                    if (userDetails.getUsername().equals(username)) {
                        for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                            information.expireNow();
                        }
                    }
                }
            }
    }

}
