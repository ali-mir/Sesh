package edu.brown.cs.am209hhe2lbenzonmsicat.sesh;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Gui Manager class.
 *
 * @author HE23
 */
public class GuiManager {
  private SpotifyCommunicator comm = new SpotifyCommunicator();

  /**
   * Default constructor.
   *
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();
  }

  private void installRoutes(FreeMarkerEngine fme) {
    Spark.get("/spotifycallback", new CallbackHandler(), fme);
    Spark.get("/sesh", new FrontHandler(), fme);
    Spark.get("/create", new CreateHandler(), fme);
    Spark.get("/join", new JoinHandler(), fme);
    Spark.post("/create/party", new CreatePartyHandler(), fme);
    Spark.post("/join/party", new JoinPartyHandler(), fme);
    Spark.post("/search", new SearchHandler());
  }

  /**
   * Handles displaying existing parties.
   */
  private class JoinPartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // TODO display all existing parties (within set georange)
      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");
      return new ModelAndView(variables, "joinParty.ftl");
    }
  }

  /**
   * Handles displaying newly created party.
   */
  private class CreatePartyHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      // TODO create party given front end data
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh Settings");
      return new ModelAndView(variables, "createParty.ftl");
    }

  }

  /**
   * Homepage end point, where user enters in their login credentials.
   */
  private class CallbackHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String code = qm.value("code");

      User user;
      List<String> userInfo = comm.getAccessToken(code);
      String userId = userInfo.get(0);
      String userEmail = userInfo.get(1);
      String userName = userInfo.get(2);
      try {
        user = User.create(userId, userEmail, userName);
      } catch (SQLException e) {
        /* user already exists */
        user = User.of(userId);
      }
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh", "test",
          " ");
      return new ModelAndView(variables, "test.ftl");
    }
  }

  /**
   * Handles request to front page (join or create a sesh).
   *
   * @author HE23
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Sesh");
      return new ModelAndView(variables, "createJoin.ftl");
    }
  }

  /**
   * Handles request to create a sesh page.
   *
   * @author HE23
   */
  private static class CreateHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String partyName = qm.value("sesh_name"); // required
      String hostName = qm.value("host_name");
      String privacyStatus = qm.value("privacyStatus");
      double lat = Double.valueOf(qm.value("latitute"));
      double lon = Double.valueOf(qm.value("longitude"));
      Coordinate coord = new Coordinate(lat, lon);
      /* TODO: Create a new party, add to db */
      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh");
      return new ModelAndView(variables, "partySettings.ftl");
    }
  }

  /**
   * Handles request to join a sesh page.
   *
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      /*
       * TODO: Get list of parties within certain range (GMaps API). Get party
       * id that user clicks on. Add them to party, update java and db Send back
       * party info (id, name, host, etc.) to display party.
       */
      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");
      return new ModelAndView(variables, "join.ftl");
    }
  }

  /**
   * Handles requests to the search page.
   *
   * @author Matt
   *
   */
  private static class SearchHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String songName = qm.value("searchResult"); // or id?
      // with id, give to spotify api to retrieve song info
      // post song info

      // Map<String, Object> variables = ImmutableMap.of("songId", songId,
      // "songName", songName, "length", length, "artist", artist);
      return new ModelAndView(variables, "search.ftl"); // incomplete
    }

  }

}
