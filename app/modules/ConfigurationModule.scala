package modules
import com.google.inject.AbstractModule
import configuration.ServiceConfiguration

class ConfigurationModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ServiceConfiguration]).toInstance(ServiceConfiguration)
  }
}
