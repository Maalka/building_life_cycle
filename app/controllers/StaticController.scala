package controllers


import javax.inject.{ Inject, Singleton }
import play.api.mvc._

@Singleton
class StaticController @Inject () () extends InjectedController {

  def type1() = Action {
    Ok(views.html.alpacaTest1())
  }

  def type2() = Action {
    Ok(views.html.alpacaTest2())
  }
  def type3() = Action {
    Ok(views.html.alpacaTest3())
  }
  def type4() = Action {
    Ok(views.html.alpacaTest4())
  }
  def type5() = Action {
    Ok(views.html.alpacaTest5())
  }
  def type6() = Action {
    Ok(views.html.alpacaTest6())
  }
}
