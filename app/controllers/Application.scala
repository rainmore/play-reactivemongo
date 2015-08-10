package controllers

import play.api.mvc._


class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def test = Action { request =>
      Ok("Got request [" + request + "]")
  }

  def test1(name: String) = Action { request =>
    Ok("Got request [" + request + " " + name + "]")
  }

}