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
  private var results = List[String]()
  private val EMAIL_ALERT = 11
  private val ABOUT_ALERT = 22

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    setMessage("Email: " + getEmailPreference)
    checkEmailIsSet()
    findView(TR.send_button).setEnabled(false)
  }
  def startScanButtonOnClick(view: View) {
    startScan
  }
  def sendButtonOnClick(view: View) {
    sendEmail
  }
  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.settings => {
        showPreferences
        true
      }
      case R.id.about => {
        showDialog(ABOUT_ALERT)
        true
      }
      case _ => return false
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
        addResult(intent.getExtras())
        findView(TR.send_button).setEnabled(true)
        //sendToEvernote(intent)
      }
    }
  }

  override def onCreateDialog(id: Int): Dialog = {
    id match {
      case EMAIL_ALERT => createEmailAlert
      case ABOUT_ALERT => createAboutAlert
    }
  }
  def createEmailAlert: AlertDialog = {
    val builder = new AlertDialog.Builder(this)
    builder.setTitle("Please set email address in the settings before continuing!").setPositiveButton("OK",
      new DialogInterface.OnClickListener() {
        override def onClick(dialog: DialogInterface, id: Int) {
          showPreferences
        }
      })
    builder.create()
  }
  def createAboutAlert: AlertDialog = {
    val builder = new AlertDialog.Builder(this)
    builder.setTitle("About").setMessage("Easy Bar Code 0.3\n\nIcons by http://www.androidicons.com")
      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        override def onClick(dialog: DialogInterface, id: Int) {
          dialog.dismiss()
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
    val move   = new TranslateAnimation(0, message.getWidth(), 0, 0)
    move.setDuration(1000)
    move.setStartOffset(2000)
    move.setInterpolator(new AccelerateInterpolator())
    move.setAnimationListener(new AnimationListener {
      def onAnimationStart(p1: Animation) {}

      def onAnimationEnd(p1: Animation) {
        message.setVisibility(View.GONE)
      }

      def onAnimationRepeat(p1: Animation) {}
    })
    message.startAnimation(move)
  }

  private def addResult(extras: Bundle) {
    val code = extras.get("SCAN_RESULT").toString
    results = code :: results
    findView(TR.results).addView(new TextView(this){
      setText("Result: " + code + ", " + extras.toString)
    })
  }

  private def startScan {
    val intent = new Intent("com.google.zxing.client.android.SCAN")
    intent.putExtra("SCAN_MODE", "ONE_D_MODE")
    try {
      startActivityForResult(intent, 0)
    } catch {
      case ex: Exception => setMessage("Error: " + ex.getMessage)
    }
  }

  private def sendEmail {
    val intent = new Intent(Intent.ACTION_SEND)
    intent.setType("plain/text")
    intent.putExtra(Intent.EXTRA_EMAIL, getEmailPreference())
    intent.putExtra(Intent.EXTRA_TEXT, "Bar codes: " + results.toString())
    intent.putExtra(Intent.EXTRA_SUBJECT, "Bar codes")
    startActivity(intent)
  }
  private def checkEmailIsSet() {
    val email = getEmailPreference()
    Option(email) match {
      case None | Some("") => {
        showDialog(EMAIL_ALERT)
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

class Preferences extends PreferenceActivity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.layout.preferences)
  }

}