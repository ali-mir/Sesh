package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyOutOfSyncException;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;

/**
 * Models a party.
 * @author Matt
 */
public class PartyBean extends Party {
  private int partyId;
  private Playlist playlist;
  private User host;
  private Set<User> guests;
  private Map<String, Request> requestIdToRequest;
  private Multiset<User> userToNumApprovedRequests;
  private Multiset<User> userToNumTotalRequests;

  private Coordinate location;
  private String name;
  private LocalDateTime time;
  private Status status;
  private String deviceId;
  private AccessType accessType;
  private String accessCode;

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
   * @param requestedSongs
   *          - requested songs
   * @param guests
   *          - guests
   * @param status
   *          -status
   */
  public PartyBean(int partyId, String name, User host, Playlist playlist,
      Coordinate location, LocalDateTime time, Set<Request> requestedSongs,
      Set<User> guests, Status status, String deviceId, AccessType accessType,
      String accessCode) {
    this.partyId = partyId;
    this.host = host;
    this.guests = guests;
    this.requestIdToRequest = new HashMap<>();

    this.userToNumApprovedRequests = HashMultiset.create();
    this.userToNumTotalRequests = HashMultiset.create();
    for (Request request : requestedSongs) {
      userToNumTotalRequests.add(request.getUserRequestedBy());
      requestIdToRequest.put(request.getId(), request);
    }
    for (Request request : playlist.getSetOfSongs()) {
      userToNumApprovedRequests.add(request.getUserRequestedBy());
    }
    this.playlist = playlist;
    this.location = location;
    this.name = name;
    this.time = time;
    this.status = status;
    this.deviceId = deviceId;
    this.accessType = accessType;
    this.accessCode = accessCode;
  }

  @Override
  public String getDeviceId() {
    return deviceId;
  }

  @Override
  public int getPartyId() {
    return partyId;
  }

  @Override
  public boolean upvoteSong(User user, String requestId) {
    assert isActive() == true;
    try {
      Request request = requestIdToRequest.get(requestId);
      if (request != null && getAttendees().contains(user)) {
        request.upvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public boolean downvoteSong(User user, String requestId) {
    assert isActive() == true;
    try {
      Request request = requestIdToRequest.get(requestId);
      if (request != null && getAttendees().contains(user)) {
        request.downvote(user);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Override
  public boolean approveSong(String requestId) throws SpotifyUserApiException {
    assert isActive() == true;
    Request request = requestIdToRequest.get(requestId);
    if (request == null) {
      return false;
    }
    playlist.addSong(request);
    requestIdToRequest.remove(requestId);
    userToNumApprovedRequests.add(request.getUserRequestedBy());
    return true;
  }

  @Override
  public boolean removeFromPlaylist(String requestId)
      throws SpotifyUserApiException {
    assert isActive() == true;

    Request req = playlist.removeSong(requestId);

    if (req != null) {

      requestIdToRequest.put(req.getId(), req);
      userToNumApprovedRequests.remove(req.getUserRequestedBy());
      return true;
    }
    return false;

  }

  @Override
  public Request requestSong(Song song, User user) {
    assert isActive() == true;
    try {
      if (getAttendees().contains(user)) {
        Request newRequest = Request.create(song, user, partyId,
            LocalDateTime.now());
        newRequest.upvote(user);
        requestIdToRequest.put(newRequest.getId(), newRequest);
        userToNumTotalRequests.add(newRequest.getUserRequestedBy());
        return newRequest;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
    throw new IllegalArgumentException(
        "Error: User who requested song is not a guest");
  }

  @Override
  public List<Request> getRequestedSongsOrderedByRank() {
    RequestComparator comp = new RequestComparator();
    List<Request> rankedRequests = new ArrayList<>(requestIdToRequest.values());
    Collections.sort(rankedRequests, comp.reversed());
    return rankedRequests;
  }

  private class RequestComparator implements Comparator<Request> {
    @Override
    public int compare(Request r1, Request r2) {
      int numApprovedRequestsOfUser1 = userToNumApprovedRequests
          .count(r1.getUserRequestedBy());
      int numTotalRequestOfUser1 = userToNumTotalRequests
          .count(r1.getUserRequestedBy());
      int numApprovedRequestsOfUser2 = userToNumApprovedRequests
          .count(r2.getUserRequestedBy());
      int numTotalRequestOfUser2 = userToNumTotalRequests
          .count(r2.getUserRequestedBy());
      double r1Multiplier = 0.5;
      if (numApprovedRequestsOfUser1 != 0 && numTotalRequestOfUser1 != 0) {
        r1Multiplier += 0.5
            * (numApprovedRequestsOfUser1 / numTotalRequestOfUser1);
      }
      double r2Multiplier = 0.5;
      if (numApprovedRequestsOfUser2 != 0 && numTotalRequestOfUser2 != 0) {
        r2Multiplier += 0.5
            * (numApprovedRequestsOfUser2 / numTotalRequestOfUser2);
      }

      Double r1Rank = r1Multiplier * r1.getRanking();
      Double r2Rank = r2Multiplier * r2.getRanking();
      return r1Rank.compareTo(r2Rank);
    }

  }

  @Override
  public Playlist getPlaylist() {
    return playlist;
  }

  @Override
  public Set<User> getGuests() {
    return new HashSet<>(guests);
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
  public LocalDateTime getTime() {
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
  public boolean addGuest(User guest, String accessCode) {
    assert isActive() == true;

    if (host.equals(guest)) {
      return false;
    }
    return guests.add(guest);
  }

  @Override
  public void endParty() {
    status = Status.stopped;
  }

  @Override
  public boolean removeGuest(User guest) {
    assert isActive() == true;
    return guests.remove(guest);
  }

  @Override
  public Set<User> getAttendees() {
    Set<User> attendees = getGuests();
    attendees.add(getHost());
    return attendees;
  }

  @Override
  public double getDistance(Coordinate coordinate) {
    return location.getDistanceFrom(coordinate);
  }

  @Override
  public JsonElement getRequestsAsJson() {
    Map<String, Object> requestsMap = new LinkedHashMap<>();
    for (Request r : getRequestedSongsOrderedByRank()) {
      requestsMap.put(r.getId(), r.toMap());
    }
    return GSON.toJsonTree(requestsMap);
  }

  @Override
  public JsonElement getPlaylistQueueAsJson() throws SpotifyUserApiException {
    Map<String, Object> requestsMap = new LinkedHashMap<>();
    List<Request> songs = playlist.getSongs();
    for (Request r : songs) {
      requestsMap.put(r.getId(), r.toMap());
    }
    return GSON.toJsonTree(requestsMap);
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> partyMap = new HashMap<>();
    partyMap.put("partyId", partyId);
    try {
      partyMap.put("playlist", getPlaylistQueueAsJson());
    } catch (SpotifyUserApiException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e.getMessage());
    }
    partyMap.put("requests", getRequestsAsJson());
    partyMap.put("playlistUrl", playlist.getUrl());
    partyMap.put("host", host.toJson());
    partyMap.put("location", GSON.toJsonTree(location));
    partyMap.put("name", name);
    partyMap.put("time", time.toString());
    partyMap.put("status", status);
    return partyMap;
  }

  @Override
  public Set<Request> getRequestedSongs() {
    return new HashSet<>(requestIdToRequest.values());
  }

  @Override
  public boolean approveSong(String requestId, int index)
      throws SpotifyUserApiException {
    assert isActive() == true;
    Request request = requestIdToRequest.get(requestId);
    if (request == null) {
      return false;
    }
    playlist.addSongInPosition(request, index);
    requestIdToRequest.remove(requestId);
    userToNumApprovedRequests.add(request.getUserRequestedBy());
    return true;
  }

  @Override
  public boolean reorderSong(int startIndex, int endIndex)
      throws SpotifyUserApiException {
    // TODO Auto-generated method stub
    assert isActive() == true;
    playlist.reorderPlaylist(startIndex, endIndex);
    return true;
  }

  @Override
  public CurrentSongPlaying getSongBeingCurrentlyPlayed()
      throws SpotifyUserApiException, SpotifyOutOfSyncException {
    return playlist.getCurrentSong();
  }

  @Override
  public boolean playPlaylist(int index) throws SpotifyUserApiException {
    playlist.play(index, deviceId);
    return true;
  }

  @Override
  public boolean pause() throws SpotifyUserApiException {
    // TODO Auto-generated method stub
    playlist.pause(deviceId);
    return true;
  }

  @Override
  public boolean seekSong(long seekPosition) throws SpotifyUserApiException {
    // TODO Auto-generated method stub
    playlist.seek(seekPosition, deviceId);
    return true;
  }

  @Override
  public void deletePlaylist() {

  }

  @Override
  public void followPlaylist(String userId) {

  }

  @Override
  public boolean checkAccessCode(String accessCodeAttempt) {
    return accessCode.equals(accessCodeAttempt);
  }

  @Override
  public AccessType getAccessType() {
    return accessType;
  }

  // @Override
  // public boolean nextSong() {
  // // TODO Auto-generated method stub
  // playlist.nextSong();
  // return true;
  // }
  //
  // @Override
  // public boolean prevSong() {
  // // TODO Auto-generated method stub
  // playlist.prevSong();
  // return true;
  // }

}
