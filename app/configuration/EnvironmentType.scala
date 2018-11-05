package configuration
import com.ruchij.Enum

sealed trait EnvironmentType extends Enum[EnvironmentType]

object EnvironmentType {
  case object LocalDevelopment extends EnvironmentType

  case object IntegratedDevelopment extends EnvironmentType
}
