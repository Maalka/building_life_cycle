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

import javax.inject.Inject


import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader}
import play.api.Logger

class HTTPRequestLoggingFilter extends EssentialFilter {
  def apply(nextFilter: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {

      val startTime = System.currentTimeMillis

      nextFilter(requestHeader).map { result =>

        val endTime = System.currentTimeMillis
        val requestTime = endTime - startTime

        Logger.info(s"${requestHeader.method} ${requestHeader.uri}" +
          s" took ${requestTime}ms and returned ${result.header.status}")

        result.withHeaders(
          "Request-Time" -> requestTime.toString,
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Headers" -> "Content-Type"


        )
      }
    }
  }
}

