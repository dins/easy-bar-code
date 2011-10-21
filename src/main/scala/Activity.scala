package net.barcode

import _root_.android.app.Activity
import _root_.android.os.Bundle
import android.widget.{Button, TextView}
import android.view.View
import android.content.{Context, Intent}

class BarCodeActivity extends Activity with TypedActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val button = findView(TR.button)
    button.setOnClickListener(new ReadButtonListener(this))
  }
  def setText(text: String) {
    setContentView(new TextView(this) {
      setText(text)
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
      case ex: Exception => activity.setText("Error: " + ex.getMessage)
    }
  }

  def onActivityResult(requestCode: Int, resultCode: Int,  intent: Intent) {
    setText("Result: " + intent.getStringExtra("SCAN_RESULT"))
  }
}