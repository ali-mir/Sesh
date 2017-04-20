package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The actor proxy class. Deals with the data base to fetch the data about the
 * actor.
 * @author leandro
 */
public class PartyProxy extends Party implements Proxy {
  private static Map<Integer, PartyBean> idToPartyCache = new ConcurrentHashMap<>();
  private PartyBean partyBean;
  private int partyId;
  private Playlist playlist;
  private User host;
  private Coordinate location;
  private String name;
  private String time;
  private Status status;

  // private Location location; Google api stuff?

  /**
   * Constructor.
   * @param partyId
   *          - id
   * @param name
   *          - name
   * @param host
   *          - host
   * @param playlist
   *          - playlist
   * @param location
   *          - location
   * @param time
   *          - time
   * @param status
   *          - status
   */
  public PartyProxy(int partyId, String name, User host, Playlist playlist,
      Coordinate location, String time, Status status) {
    this.partyId = partyId;
    this.name = name;
    this.host = host;
    this.playlist = playlist;
    this.playlist.setPartyId(partyId);
    this.location = location;
    this.time = time;
    this.status = status;
  }

  /**
   * Clears cache.
   */
  public static void clearCache() {
    idToPartyCache.clear();
  }

  @Override
  public void fillBean() {
    assert partyBean == null;
    // if the actor exists in the cache just use that
    PartyBean party = PartyProxy.idToPartyCache.get(partyId);
    if (party != null) {
      partyBean = party;
      return;
    }

    try {
      partyBean = DbHandler.getFullParty(partyId, playlist, name, location,
          time, status);
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToPartyCache.size() > Constants.MAX_CACHE_SIZE) {
      idToPartyCache.clear();
    }
    assert !idToPartyCache.containsKey(partyId);
    idToPartyCache.put(partyId, partyBean);
  }

  @Override
  public boolean isBeanNull() {
    return partyBean == null;
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getRequestedSongs();
  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public Set<User> getGuests() {
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.getGuests();

  }

  @Override
  public User getHost() {
    return host;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getTime() {
    return time;
  }

  @Override
  public Coordinate getLocation() {
    return location;
  }

  @Override
  public Status getStatus() {
    return status;
  }

  @Override
  public boolean upvoteSong(User user, Request req) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.upvoteSong(user, req);
  }

  @Override
  public boolean downvoteSong(User user, Request req) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean != null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.downvoteSong(user, req);
  }

  @Override
  public boolean approveSong(Request req) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.approveSong(req);
  }

  @Override
  public boolean removeFromPlaylist(Request req) {
    // try {
    // // DbHandler.requestSong(req);
    // } catch (SQLException e1) {
    // throw new RuntimeException(e1.getMessage());
    // }
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean != null) {
      return partyBean.removeFromPlaylist(req);
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.removeFromPlaylist(req);
  }

  @Override
  public Request requestSong(Song song, User user) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return partyBean.requestSong(song, user);
  }

  @Override
  public boolean addGuest(User guest) {
    if (!isActive()) {
      throw new IllegalStateException("ERROR: Party has stoped");
    }
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      DbHandler.addPartyGuest(partyId, guest);
      return partyBean.addGuest(guest);

    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public void endParty() {
    // TODO Auto-generated method stub
    if (partyBean != null) {
      partyBean.endParty();
    }
    try {
      DbHandler.endParty(partyId);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e.getMessage());
    }
    status = Status.stopped;
  }

  @Override
  public boolean removeGuest(User guest) {
    // TODO Auto-generated method stub
    if (partyBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      DbHandler.removePartyGuest(partyId, guest);
      return partyBean.removeGuest(guest);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return false;
    }
  }

}
