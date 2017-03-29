/*
 * Copyright (c) 2017. Maalka Inc. All Rights Reserved
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

