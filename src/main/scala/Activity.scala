package net.barcode

import android.os.{SystemClock, Bundle}
import android.view.animation.{AccelerateInterpolator, TranslateAnimation, AnimationUtils, Animation}
import android.widget.{LinearLayout, Button, TextView}
import android.view.animation.Animation.AnimationListener
import android.view.{MenuItem, MenuInflater, Menu, View}
import android.preference.{PreferenceManager, PreferenceActivity}
import android.app.{AlertDialog, Dialog, Activity}
import android.content.{DialogInterface, Intent, Context}

class BarCodeActivity extends Activity with TypedActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val button = findView(TR.button)
    button.setOnClickListener(new ReadButtonListener(this))
    setMessage("Email: " + getEmailPreference)
    checkEmailIsSet()
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    Option(item) match {
      case None => return false
      case Some(item) => {
        showPreferences
        true
      }
    }
  }
  private def showPreferences {
    startActivity(new Intent(getBaseContext(), classOf[Preferences]))
  }
  override def onActivityResult(requestCode: Int, resultCode: Int,  intent: Intent) {
    Option(intent) match {
      case None =>
      case Some(intent) => {
        setMessage("Success!")
        addResult(intent.getExtras().toString)
        //sendToEvernote(intent)
        sendEmail(intent)
      }
    }
  }
  override def onCreateDialog(id: Int): Dialog ={
    val builder = new AlertDialog.Builder(this)
    builder.setTitle("Please set email address in the settings before continuing!").setPositiveButton("OK",
      new DialogInterface.OnClickListener() {
        override def onClick(dialod: DialogInterface, id: Int) {
          showPreferences
        }
      })
    builder.create()
  }
  private def getEmailPreference(): String = {
    val prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    prefs.getString("emailPref", null)
  }

  def setMessage(text: String) {
    val message = findView(TR.message)
    message.setText(text)
    animateMessage(message)
  }

  private def animateMessage(message: TextView) {
    message.setVisibility(View.VISIBLE)
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

  private def addResult(result: String) {
    findView(TR.results).addView(new TextView(this){
      setText("Result: " + result)
    })
  }
  private def sendEmail(result: Intent) {
    val intent = new Intent(Intent.ACTION_SEND)
    intent.setType("plain/text")
    intent.putExtra(Intent.EXTRA_EMAIL, getEmailPreference())
    intent.putExtra(Intent.EXTRA_TEXT, "Bar code: " + result.getStringExtra("SCAN_RESULT"))
    intent.putExtra(Intent.EXTRA_SUBJECT, "A bill to pay")
    startActivity(intent)
  }
  private def checkEmailIsSet() {
    val email = getEmailPreference()
    Option(email) match {
      case None | Some("") => {
        showDialog(1)
      }
      case _ =>
    }
  }
  private def sendToEvernote(result: Intent) {
    val intent = new Intent("com.evernote.action.CREATE_NEW_NOTE")
    intent.setType("plain/text")
    intent.putExtra("EXTRA_TITLE", "A bill to pay")
    intent.putExtra("EXTRA_TEXT", "Bar code: " + result.getStringExtra("SCAN_RESULT"))
    intent.putExtra("EXTRA_SUBJECT", "A bill to pay")
    startActivity(intent)
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

class Preferences extends PreferenceActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.layout.preferences)
  }

}