/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
 */

package controllers

import play.api.mvc._

class Application extends Controller {

  /**
    * Retrieves all routes via reflection.
    * http://stackoverflow.com/questions/12012703/less-verbose-way-of-generating-play-2s-javascript-router
    * @todo If you have controllers in multiple packages, you need to add each package here.
    */
  val routeCache = {
    val jsRoutesClasses = Seq(classOf[routes.javascript]) // TODO add your own packages
    jsRoutesClasses.flatMap { jsRoutesClass =>
      val controllers = jsRoutesClass.getFields.map(_.get(null))
      controllers.flatMap { controller =>
        controller.getClass.getDeclaredMethods.filter(_.getName != "_defaultPrefix").map { action =>
          action.invoke(controller).asInstanceOf[play.api.routing.JavaScriptReverseRoute]
        }
      }
    }
  }

  def index(includeHeader: Boolean = true) = Action {
    Ok(views.html.index(includeHeader))
  }

  /**
    * Returns the JavaScript router that the client can use for "type-safe" routes.
    * Uses browser caching; set duration (in seconds) according to your release cycle.
    * @param varName The name of the global variable, defaults to `jsRoutes`
    */
  def jsRoutes(varName: String = "jsRoutes") = {
    Action { implicit request =>
      Ok(play.api.routing.JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
    }
  }


}
