package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.Test;

import edu.brown.cs.am209hhe2lbenzonmsicat.models.Coordinate;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Party.AccessType;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PartyProxy;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Playlist;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.PlaylistProxy;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Request;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.Song;
import edu.brown.cs.am209hhe2lbenzonmsicat.models.User;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.DbHandler;
import edu.brown.cs.am209hhe2lbenzonmsicat.utilities.SpotifyCommunicator;

/***
 * This class tests the playlist class.
 *
 * @author Ali
 */
public class PlaylistTest {

  /**
   * This method tests the getID method.
   *
   * @throws SQLException
   *           throws a SQL exception if the db is bad
   * @throws FileNotFoundException
   *           if the db is not found
   */
  @Test
  public void testGetId() throws SQLException, FileNotFoundException {

  }

  /**
   * This method tests the getUrl function. <<<<<<< HEAD
   *
   * @throws SQLException
   *           if db malfunctions
   * @throws FileNotFoundException
   *           if db doesn't exist
   */
  @Test
  public void testGetUrl() throws SQLException, FileNotFoundException {
    // fail("Not yet implemented");

  }

  /**
   * this method tests the remove songs.
   *
   * @throws SQLException
   *           if db doesn't work
   * @throws FileNotFoundException
   *           if db doesn't exist
   */
  @Test
  public void testRemoveSong()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "premium");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId", "testTitle", AccessType.PUBLIC,
        "public");
    Playlist playlist = p.getPlaylist();
    Request r = Request.create(Song.of("7AQAlklmptrrkBSeujkXsD"), l,
        p.getPartyId(), LocalDateTime.now());
    playlist.addSong(r);
    Request r1 = Request.create(Song.of("57tzAvfPHXHzCHUNp9AUBm"), l,
        p.getPartyId(), LocalDateTime.now());
    playlist.addSong(r1);
    assert playlist.getSongs().contains(r);
    assert playlist.getSongs().contains(r1);
    playlist.removeSong(r.getId());
    PlaylistProxy.clearCache();
    Playlist playlist1 = Playlist.of(playlist.getId(), p.getPartyId(), l);
    assert !playlist1.getSongs().contains(r);
    assert playlist1.getSongs().contains(r1);
  }

  /**
   * this tests the add song. <<<<<<< HEAD
   *
   * ======= >>>>>>> bc35e56bdd59f40419aadfc80079750a4699bdff
   *
   * @throws SQLException
   *           if db screws up
   * @throws FileNotFoundException
   *           if the db doesn't exist.
   */
  @Test
  public void testAddSong()
      throws SQLException, FileNotFoundException, SpotifyUserApiException {
    SpotifyCommunicator.setUpTestApi();
    DbHandler.setFromUrl("test.db");
    DbHandler.clearAllTables();
    PartyProxy.clearCache();
    PlaylistProxy.clearCache();
    User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
        "premium");
    Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
        LocalDateTime.now(), "deviceId", "testTitle", AccessType.PUBLIC,
        "public");
    Playlist playlist = p.getPlaylist();
    Request r = p.requestSong(Song.of("7AQAlklmptrrkBSeujkXsD"), l);
    // Somewhere only we know - keane
    playlist.addSong(r);
    Request r1 = p.requestSong(Song.of("57tzAvfPHXHzCHUNp9AUBm"), l);
    // nara - alt j
    playlist.addSong(r1);
    assert playlist.getSongs().contains(r);
    assert playlist.getSongs().contains(r1);
    PlaylistProxy.clearCache();
    int id = p.getPartyId();
    Playlist playlist1 = Playlist.of(playlist.getId(), id, l);
    assert playlist1.getSongs().contains(r);
    assert playlist1.getSongs().contains(r1);
  }

  /**
   * This method tests the Of function. <<<<<<< HEAD
   *
   *
   * @throws SQLException
   *           from the db
   * @throws FileNotFoundException
   *           if the db doesn't exist
   */
  @Test
  public void testOf() throws SQLException, FileNotFoundException {
    // fail("Not yet implemented");
  }

  /**
   * This tests the get songs from the spotify API.
   *
   * @throws MalformedURLException
   *           if the url doesn't work
   * @throws IOException
   *           if there are issues with IO
   */
  @Test
  public void testGetSongs() throws MalformedURLException, IOException {
    // try {
    // SpotifyCommunicator.setUpTestApi();
    // DbHandler.setFromUrl("test.db");
    // DbHandler.clearAllTables();
    // PartyProxy.clearCache();
    // PlaylistProxy.clearCache();
    // SpotifyCommunicator comm = new SpotifyCommunicator();
    // comm.createAuthorizeURL();
    //
    // User host = User.of("s3shteam32", "seshteam32@gmail.com",
    // "Ali Mir");
    // Party p = Party.create("Dope Party", host, new Coordinate(1, 1),
    // LocalDateTime.now());
    // Playlist plist = p.getPlaylist();
    // List<Request> reqs = plist.getSongs();
    //
    // } catch (SQLException e) {
    // System.out.println("ERROR: SQLLLL " + e.getMessage());
    // }

  }

  @Test
  public void testGetCurrentSong() throws MalformedURLException, IOException,
      SQLException, SpotifyUserApiException {
    // SpotifyCommunicator.setUpTestApi();
    // DbHandler.setFromUrl("test.db");
    // DbHandler.clearAllTables();
    // PartyProxy.clearCache();
    // PlaylistProxy.clearCache();
    // User l = User.create("s3shteam32", "seshteam32@gmail.com", "Ali Mir",
    // "deviceId");
    //
    // Party p = Party.create("Dope Party", l, new Coordinate(1, 1),
    // LocalDateTime.now(), "deviceId");
    // Playlist playlist = p.getPlaylist();
    // Request r = p.requestSong(Song.of("7AQAlklmptrrkBSeujkXsD"), l);
    //
    // // Somewhere only we know - keane
    // playlist.addSong(r);
    // assert playlist.getSongs().contains(r);
    // CurrentSongPlaying s = playlist.getCurrentSong();
    // System.out.println(s);
  }

}
