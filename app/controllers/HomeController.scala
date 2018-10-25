package controllers

import com.google.common.net.HttpHeaders
import configuration.ServiceConfiguration
import controllers.Assets.Asset
import javax.inject._
import play.api.libs.json.Json.{toJson => json}
import play.api.mvc._

@Singleton
class HomeController @Inject()(controllerComponents: ControllerComponents, assets: Assets)(
  implicit serviceConfiguration: ServiceConfiguration
) extends AbstractController(controllerComponents) {

  def serviceInformation() =
    Action {
      Ok {
        json(serviceConfiguration.serviceInformation())
      }
    }

  def index() =
    Action {
      Ok(views.html.index())
    }

  def publicAssets(file: Asset): Action[AnyContent] = assets.at("/public", file.name)
}
