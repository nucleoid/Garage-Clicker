package com.pragmaticcoder.garageclicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class GarageClickerActivity extends Activity {
	
	private final String URL_KEY = "garageClickerUrl";
	private final String USERNAME_KEY = "garageClickerUsername";
	private final String PASSWORD_KEY = "garageClickerPassword";
	private final String DEFAULT_URL = "http://www.designlogic.net/garage/";
	private final int CHANGE_DIALOG = 99;
	private final int USERNAME_PASSWORD_DIALOG = 98; 
	private WebView _webview;
	private static SharedPreferences _prefs;
	private static boolean _authAttemped = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _prefs = this.getPreferences(Context.MODE_PRIVATE);
        _webview = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = _webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final String username = _prefs.getString(USERNAME_KEY, "");
        final String password = _prefs.getString(PASSWORD_KEY, "");
        _webview.setWebViewClient(new WebViewClient() {
        	@Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        		if(!_authAttemped){
        			_authAttemped = true;
        			handler.proceed(username, password);
        		}
        		else{
        			_authAttemped = false;
        			super.onReceivedHttpAuthRequest(view, handler, host, realm);
        		}
            }
    	 });
        _webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        loadPage();
    }
    
    private void loadPage() {
    	String urlToLoad = _prefs.getString(URL_KEY, DEFAULT_URL);
        _webview.loadUrl(urlToLoad);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflate = getMenuInflater();
		menuInflate.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_quit:
				this.finish();
				return true;
			case R.id.menu_change_url:
				showDialog(CHANGE_DIALOG);
				return true;
			case R.id.menu_credentials:
				showDialog(USERNAME_PASSWORD_DIALOG);
				return true;
		}
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
			case CHANGE_DIALOG:
				dialog = createChangeUrl();
				break;
			case USERNAME_PASSWORD_DIALOG:
				dialog = createUsernamePassword();
				break;
			default:
				dialog = null;
		}
		return dialog;
	}
	
	private Dialog createUsernamePassword() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater li = LayoutInflater.from(this);
		final View view = li.inflate(R.layout.credentials, null);
		dialog.setContentView(view);
		
		final EditText username = (EditText)view.findViewById(R.id.username);
		username.setText(_prefs.getString(USERNAME_KEY, ""));
		username.setSelection(username.getText().length());
		
		final EditText password = (EditText)view.findViewById(R.id.password);
		password.setText(_prefs.getString(PASSWORD_KEY, ""));
		
		Button okButton = (Button)view.findViewById(R.id.usernamepassword_ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View okView) {
				Editor e = _prefs.edit();
		        e.putString(USERNAME_KEY, username.getText().toString());
		        e.putString(PASSWORD_KEY, password.getText().toString());
		        e.commit();
				dialog.dismiss();
			}
		});

		username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	    @Override
    	    public void onFocusChange(View v, boolean hasFocus) {
    	        if (hasFocus) {
    	            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	        }
    	    }
    	});
		
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				loadPage();
			}
		});
		return dialog;
	}
	
	private Dialog createChangeUrl() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater li = LayoutInflater.from(this);
		final View view = li.inflate(R.layout.change_url, null);
		dialog.setContentView(view);
		
		final EditText editor = (EditText)view.findViewById(R.id.editor);
		editor.setText(_prefs.getString(URL_KEY, DEFAULT_URL));
		editor.setSelection(editor.getText().length());
		
		Button okButton = (Button)view.findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View okView) {
				Editor e = _prefs.edit();
		        e.putString(URL_KEY, editor.getText().toString());
		        e.commit();
				dialog.dismiss();
			}
		});

		editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    	    @Override
    	    public void onFocusChange(View v, boolean hasFocus) {
    	        if (hasFocus) {
    	            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	        }
    	    }
    	});
		
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				loadPage();
			}
		});
		return dialog;
	}
}