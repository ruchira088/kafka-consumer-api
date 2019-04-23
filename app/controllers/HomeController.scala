package controllers

import configuration.ServiceConfiguration
import controllers.Assets.Asset
import javax.inject._
import play.api.libs.json.Json.{toJson => json}
import play.api.mvc._

@Singleton
class HomeController @Inject()(controllerComponents: ControllerComponents, assets: Assets)(
  implicit serviceConfiguration: ServiceConfiguration
) extends AbstractController(controllerComponents) {

  def serviceInformation(): Action[AnyContent] =
    Action {
      Ok {
        json(serviceConfiguration.serviceInformation())
      }
    }

  def index(): Action[AnyContent] =
    Action {
      Ok(views.html.index())
    }

  def publicAssets(file: Asset): Action[AnyContent] = assets.at("/public", file.name)
}
