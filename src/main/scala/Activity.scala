package net.barcode

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget.TextView
import android.content.Intent

class BarCodeActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(new TextView(this) {
      setText("I read bar codes!")
    })
    val intent = new Intent("com.google.zxing.client.android.SCAN")
    //intent.putExtra("SCAN_MODE", "ONE_D_MODE")
    intent.putExtra("SCAN_FORMATS", "CODE_128")

    startActivityForResult(intent, 0)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int,  intent: Intent) {
    setContentView(new TextView(this) {
      setText("Result: " + intent.getStringExtra("SCAN_RESULT"))
    })
  }
}
