package net.barcode

import _root_.android.app.Activity
import android.view.View
import android.content.{Context, Intent}
import android.os.{SystemClock, Bundle}
import android.view.animation.{AccelerateInterpolator, TranslateAnimation, AnimationUtils, Animation}
import android.widget.{LinearLayout, Button, TextView}
import android.view.animation.Animation.AnimationListener

class BarCodeActivity extends Activity with TypedActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val button = findView(TR.button)
    button.setOnClickListener(new ReadButtonListener(this))
  }
  override def onActivityResult(requestCode: Int, resultCode: Int,  intent: Intent) {
    Option(intent) match {
      case None =>
      case Some(intent) => {
        setMessage("Success!")
        addResult(intent.getStringExtra("SCAN_RESULT"))
        addResult(intent.getExtras().toString)
      }
    }
  }
  def setMessage(text: String) {
    val message = findView(TR.message)
    message.setVisibility(View.VISIBLE)
    message.setText(text)
    val animation = new TranslateAnimation(0, message.getWidth(), 0, 0)
    animation.setDuration(1000)
    animation.setStartOffset(2000)
    animation.setInterpolator(new AccelerateInterpolator())
    animation.setAnimationListener(new AnimationListener {
      def onAnimationStart(p1: Animation) {}

      def onAnimationEnd(p1: Animation) {
        message.setVisibility(View.GONE)
      }

      def onAnimationRepeat(p1: Animation) {}
    })
    message.startAnimation(animation)
  }
  def addResult(result: String) {
    findView(TR.results).addView(new TextView(this){
      setText("Result: " + result)
    })
  }
}

class ReadButtonListener(activity : BarCodeActivity) extends View.OnClickListener {
  def onClick(v : View) {
    val intent = new Intent("com.google.zxing.client.android.SCAN")
    intent.putExtra("SCAN_MODE", "ONE_D_MODE")
    try {
      activity.startActivityForResult(intent, 0)
    } catch {
      case ex: Exception => activity.setMessage("Error: " + ex.getMessage)
    }
  }

}