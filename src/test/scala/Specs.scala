import net.barcode.BarCodeActivity
import com.github.jbrechtel.robospecs.RoboSpecs
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
  }
}
