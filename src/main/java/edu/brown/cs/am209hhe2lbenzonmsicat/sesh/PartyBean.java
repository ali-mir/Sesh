package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.Set;

/**
 * Models a party.
 * @author Matt
 */
public class PartyBean extends Party {
  private int partyId;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Set<Request> requestedSongs;
  private Coordinate location;
  private String name;
  private String time;
  private Status status;

  // private Location location; Google api stuff?

  public PartyBean(int partyId, String name, User host, Playlist playlist,
      Coordinate location, String time, Set<Request> requestedSongs,
      Set<User> guests, Status status) {
    this.partyId = partyId;
    this.host = host;
    this.guests = guests;
    this.requestedSongs = requestedSongs;
    this.playlist = playlist;
    this.location = location;
    this.time = time;
    this.status = status;
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public boolean upvoteSong(User user, Request req) {
    req.upvote(user);
    return true;
  }

  @Override
  public boolean downvoteSong(User user, Request req) {
    req.downvote(user);
    return true;
  }

  @Override
  public boolean approveSong(Request req) {
    if (!requestedSongs.contains(req)) {
      System.out.println("ERROR: Cannot approve song not in requested list");
      return false;
    }
    requestedSongs.remove(req);
    return true;

  }

  @Override
  public boolean removeFromPlaylist(Request req) {
    if (!playlist.getSongs().contains(req)) {
      System.out.println("ERROR: Cannot remove song not in playlist");
      return false;
    }
    playlist.removeSong(req.getSong());
    requestedSongs.add(req);
    return true;

  }

  @Override
  public boolean requestSong(Request req) { // maybe this method should be in
                                            // User?
    if (requestedSongs.contains(req)) {
      return false;
    }
    requestedSongs.add(req);
    return true;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Playlist getPlaylist() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<User> getGuests() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getHost() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTime() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Coordinate getLocation() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Status getStatus() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean addGuest(User guest) {
    // TODO Auto-generated method stub
    return false;
  }

}
