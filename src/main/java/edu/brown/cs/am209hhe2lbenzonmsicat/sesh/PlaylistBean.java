package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Playlist bean class.
 */
public class PlaylistBean extends Playlist {
  private String id; // youtube/spotify id
  private String url;
  private Map<Song, Request> queuedRequests;
  private int partyId;

  /**
   * Constructor.
   * @param id
   *          - playlist id
   * @param partyId
   *          - party id
   * @param queuedRequests
   *          - queued requests
   */
  public PlaylistBean(String id, int partyId, List<Request> queuedRequests) {
    this.setId(id);
    this.setUrl("find out the actual structure " + id);
    this.partyId = partyId;
    this.queuedRequests = new HashMap<>();
    for (Request req : queuedRequests) {
      this.queuedRequests.put(req.getSong(), req);
    }
  }

  @Override
  public String getId() {
    return id;
  }

  /**
   * Set id.
   * @param id
   *          - id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getUrl() {
    return url;
  }

  /**
   * @param url
   *          - to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public List<Request> getSongs() {
    return new ArrayList<>(queuedRequests.values());
  }

  /**
   * @return queued requests
   */
  public Map<Song, Request> getQueuedRequests() {
    return queuedRequests;
  }

  @Override
  public boolean removeSong(Request request) {
    return queuedRequests.remove(request.getSong()) != null;
  }

  @Override
  public boolean addSong(Request request) {
    queuedRequests.put(request.getSong(), request);
    return true;
  }

}
