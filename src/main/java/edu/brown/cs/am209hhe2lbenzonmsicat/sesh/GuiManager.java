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
 * Gui Manager class. <<<<<<< 0f313668c404bac01ce2a89bc9053e038c93499a
 *
 * =======
 * 
 * >>>>>>> changes to front page handler changes
 * 
 * @author HE23
 */
public class GuiManager {
  private SpotifyCommunicator comm = new SpotifyCommunicator();

  /**
   * Default constructor. <<<<<<< 0f313668c404bac01ce2a89bc9053e038c93499a
   *
   * =======
   * 
   * >>>>>>> changes to front page handler changes
   * 
   * @param freeMarkerEngine
   *          - freemarker engine
   */
  public GuiManager(FreeMarkerEngine freeMarkerEngine) {
    installRoutes(freeMarkerEngine);
    comm.createAuthorizeURL();
  }

  private void installRoutes(FreeMarkerEngine fme) {
<<<<<<< 0f313668c404bac01ce2a89bc9053e038c93499a
    Spark.get("/spotifycallback", new CallbackHandler(), fme);
    Spark.get("/sesh", new FrontHandler(), fme);
    Spark.get("/create", new CreateHandler(), fme);
    Spark.get("/join", new JoinHandler(), fme);
  }

  /**
   * Homepage end point, where user enters in their login credentials.
   */
  private class CallbackHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();

      String code = qm.value("code");

      System.out.println("code is " + code);
      User user;
      List<String> userInfo = comm.getAccessToken(code); // should return list
                                                         // of strings w user
                                                         // info
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
      System.out.println("coming to spotify handler");
      return new ModelAndView(variables, "test.ftl");
    }
  }

  /**
   * Handles request to front page (join or create a sesh). <<<<<<<
   * 0f313668c404bac01ce2a89bc9053e038c93499a
   *
   * =======
   * 
   * >>>>>>> changes to front page handler changes
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
   * Handles request to create a sesh page. <<<<<<<
   * 0f313668c404bac01ce2a89bc9053e038c93499a
   *
   * =======
   * 
   * >>>>>>> changes to front page handler changes
   * 
   * @author HE23
   */
  private static class CreateHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Create a Sesh");
      return new ModelAndView(variables, "create.ftl");
    }
  }

  /**
   * Handles request to join a sesh page. <<<<<<<
   * 0f313668c404bac01ce2a89bc9053e038c93499a
   *
   * =======
   * 
   * >>>>>>> changes to front page handler changes
   * 
   * @author HE23
   */
  private static class JoinHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "Join a Sesh");
      return new ModelAndView(variables, "join.ftl");
    }
  }

}
