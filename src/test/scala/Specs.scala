import com.github.jbrechtel.robospecs.RoboSpecs
import net.barcode.{TR, R, BarCodeActivity}
import org.specs2.execute.StandardResults._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.matcher.{Expectable, Matcher}
import org.specs2.specification.BeforeExample
import com.github.jbrechtel.robospecs.RoboSpecs
import com.xtremelabs.robolectric.shadows._

class MainActivitySpecs extends RoboSpecs with Mockito {

  "onCreate" should {
    "not crash" in {
      val activity = new BarCodeActivity()
      activity.onCreate(null)
      done
    }
    "add button" in {
      val activity = new BarCodeActivity()
      activity.onCreate(null)
      activity.findView(TR.read_button) must not beNull
    }
  }
}
