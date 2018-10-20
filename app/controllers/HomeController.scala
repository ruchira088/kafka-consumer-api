package controllers

import configuration.ServiceConfiguration
import javax.inject._
import play.api.libs.json.Json.{toJson => json}
import play.api.mvc._

@Singleton
class HomeController @Inject()(controllerComponents: ControllerComponents)(
  implicit serviceConfiguration: ServiceConfiguration
) extends AbstractController(controllerComponents) {

  def serviceInformation() =
    Action {
      Ok {
        json(serviceConfiguration.serviceInformation())
      }
    }
}
