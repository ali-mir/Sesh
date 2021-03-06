package edu.brown.cs.am209hhe2lbenzonmsicat.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.Constants;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyOutOfSyncException;
import edu.brown.cs.am209hhe2lbenzonmsicat.sesh.SpotifyUserApiException;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

/**
 * Playlist proxy class.
 */
public class PlaylistProxy extends Playlist implements Proxy {
  private static Map<String, PlaylistBean> idToPlaylistCache = new ConcurrentHashMap<>();
  private PlaylistBean playlistBean;
  private String spotifyId;
  private int partyId;
  private User host;

  /**
   * Constructor.
   * @param spotifyId
   *          - playlist id
   * @param partyId
   *          - party id
   * @param host
   *          the host
   */
  public PlaylistProxy(String spotifyId, int partyId, User host) {
    this.spotifyId = spotifyId;
    this.partyId = partyId;
    this.host = host;
  }

  // /**
  // * Constructor.
  // * @param spotifyId
  // * - id
  // */
  // public PlaylistProxy(String spotifyId, User host) {
  // this.spotifyId = spotifyId;
  // this.host = host;
  // }
  //
  // @Override
  // public void setPartyId(int partyId) {
  // this.partyId = partyId;
  // }

  /**
   * Clears the cache.
   */
  public static void clearCache() {
    idToPlaylistCache.clear();
  }

  @Override
  public String getId() {
    return spotifyId;
  }

  @Override
  public String getUrl() {
    if (playlistBean != null) {
      return playlistBean.getUrl();
    } else {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getUrl();
  }

  @Override
  public List<Request> getSongs() throws SpotifyUserApiException {
    List<Request> results = new ArrayList<Request>();
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    // TODO ADD API REQUEST HERE
    // TODO make a request to the spotify api to get the list of songs, then
    // sort that requsts the same way that the songs in the spotify playlists
    // are
    List<Song> songs = SpotifyCommunicator
        .getPlaylistTracks(host.getSpotifyId(), this.spotifyId, true);
    Map<Song, Request> map = playlistBean.getQueuedRequests();
    for (Song s : songs) {
      Request r = map.get(s);
      results.add(r);
    }
    return results;
  }

  @Override
  public CurrentSongPlaying getCurrentSong()
      throws SpotifyUserApiException, SpotifyOutOfSyncException {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getCurrentSong();
  }

  @Override
  public void play(int offset, String deviceId) throws SpotifyUserApiException {
    SpotifyCommunicator.play(host.getSpotifyId(), this.spotifyId, offset, true,
        deviceId);
  }

  @Override
  public void pause(String deviceId) throws SpotifyUserApiException {
    SpotifyCommunicator.pause(host.getSpotifyId(), true, deviceId);
  }

  // @Override
  // public void nextSong() {
  // SpotifyCommunicator.nextSong(host.getSpotifyId(), true);
  // }
  //
  // @Override
  // public void prevSong() {
  // SpotifyCommunicator.prevSong(host.getSpotifyId(), true);
  // }

  @Override
  public void seek(long position_ms, String deviceId)
      throws SpotifyUserApiException {
    SpotifyCommunicator.seek(host.getSpotifyId(), position_ms, deviceId, true);
  }

  @Override
  public Request removeSong(String requestId) throws SpotifyUserApiException {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      Request request = getRequest(requestId);
      if (request == null) {
        return null;
      }
      SpotifyCommunicator.removeTrack(host.getSpotifyId(), this.spotifyId,
          request, true);
      DbHandler.moveSongRequestOutOfQueue(request);
      return playlistBean.removeSong(requestId);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Request getRequest(String requestId) {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getRequest(requestId);
  }

  @Override
  public boolean addSong(Request request) throws SpotifyUserApiException {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {

      SpotifyCommunicator.addTrack(host.getSpotifyId(), this.spotifyId, request,
          true);
      DbHandler.moveSongRequestToQueue(request);
    } catch (SQLException e) {

      throw new RuntimeException(e.getMessage());
    }
    return playlistBean.addSong(request);
  }

  @Override
  public boolean addSongInPosition(Request request, int pos)
      throws SpotifyUserApiException {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    try {
      // TODO ADD API REQUEST HERE

      SpotifyCommunicator.addTrackInPosition(host.getSpotifyId(),
          this.spotifyId, request, pos, true);
      DbHandler.moveSongRequestToQueue(request);
    } catch (SQLException e) {

      throw new RuntimeException(e.getMessage());
    }
    return playlistBean.addSong(request);
  }

  @Override
  public void reorderPlaylist(int rangeStart, int insertBefore)
      throws SpotifyUserApiException {
    SpotifyCommunicator.reorderPlaylist(host.getSpotifyId(), this.spotifyId,
        rangeStart, insertBefore, true);
  }

  @Override
  public void fillBean() {
    assert playlistBean == null;
    // if the playlist exists in the cache just use that
    PlaylistBean playlist = idToPlaylistCache.get(spotifyId);
    if (playlist != null) {
      playlistBean = playlist;
      return;
    }
    try {
      playlistBean = DbHandler.getQueuedSongsForParty(spotifyId, partyId, host);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    addBeanToCache();
  }

  /**
   * adds the bean to the cache.
   */
  private void addBeanToCache() {
    if (idToPlaylistCache.size() > Constants.MAX_CACHE_SIZE) {
      idToPlaylistCache.clear();
    }
    assert !idToPlaylistCache.containsKey(spotifyId);
    idToPlaylistCache.put(spotifyId, playlistBean);
  }

  @Override
  public boolean isBeanNull() {
    return playlistBean == null;
  }

  @Override
  public Set<Request> getSetOfSongs() {
    if (playlistBean == null) {
      try {
        fill();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return playlistBean.getSetOfSongs();
  }

}
