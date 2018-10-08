/*
 * Copyright 2017 Maalka
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package controllers

import javax.inject.{Inject, Singleton}
import play.api.cache.{AsyncCacheApi, Cached}
import play.api.mvc._

/** Application controller, handles authentication */
@Singleton
class Application @Inject() (cached: Cached, val cache: AsyncCacheApi, cc: ControllerComponents) extends AbstractController(cc) {

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
