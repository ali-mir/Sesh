package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator.Time_range;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 *
 * @author leandro
 */
public class UserProxy extends User implements Proxy {
  private static Map<String, UserBean> idToUserCache = new ConcurrentHashMap<>();
  private UserBean userBean;
  private String spotifyId;

  /**
   * Constructor.
   *
   * @param spotifyId
   *          - unique id
   */
  public UserProxy(String spotifyId) {
    this.spotifyId = spotifyId;
  }

  /**
   * this method is a constructor for the full user proxy.
   *
   * @param spotifyId
   *          the user id
   * @param email
   *          their email
   * @param name
   *          their name
   */
  public UserProxy(String spotifyId, String email, String name, Type type) {
    this.spotifyId = spotifyId;
    userBean = new UserBean(spotifyId, email, name, type);
  }

  /**
   * This method clears the cache.
   */
  public static void clearCache() {
    idToUserCache.clear();
  }

  @Override
  public void fillBean() {
    assert userBean == null;
    // if the actor exists in the cache just use that
    UserBean user = idToUserCache.get(spotifyId);
    if (user != null) {
      userBean = user;
      return;
    }
    try {
      userBean = DbHandler.getUserWithId(spotifyId);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToUserCache.size() > Constants.MAX_CACHE_SIZE) {
      idToUserCache.clear();
    }
    assert !idToUserCache.containsKey(spotifyId);
    idToUserCache.put(spotifyId, userBean);
  }

  @Override
  public boolean isBeanNull() {
    return userBean == null;
  }

  @Override
  public String getSpotifyId() {
    return spotifyId;
  }

  @Override
  public String getEmail() {
    if (userBean != null) {
      return userBean.getEmail();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getEmail();
  }

  @Override
  public String getFirstName() {
    if (userBean != null) {
      return userBean.getFirstName();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getFirstName();
  }

  @Override
  public String getLastName() {
    if (userBean != null) {
      return userBean.getLastName();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getLastName();
  }

  @Override
  public String getFullName() {
    if (userBean != null) {
      return userBean.getFullName();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getFullName();
  }

  @Override
  public Map<String, Object> toMap() {
    if (userBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.toMap();
  }

  @Override
  public Type getType() {
    if (userBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getType();
  }

  @Override
  public List<Device> getDevices() throws SpotifyUserApiException {
    return SpotifyCommunicator.getDevices(spotifyId, true);
  }

  @Override
  public List<Song> getUserTopTracks(Time_range time_range)
      throws SpotifyUserApiException {
    if (userBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getUserTopTracks(time_range);
  }

  @Override
  public List<Song> getFavorites() throws SQLException {
    if (userBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return userBean.getFavorites();
  }

}
