package com.iaai.onyard.authenticator;

import com.iaai.onyard.application.OnYard;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {
	
	/**
	 * Object which contains all information about this account's authentication.
	 */
	private static AccountAuthenticatorImpl sAccountAuthenticator = null;

	/**
	 * Default constructor.
	 */
	public AccountAuthenticatorService() {
		super();
	}

	/**
	 * Return the communication channel to the Authenticator service.
	 * 
	 * @param intent The Intent that was used to bind to this service, as given 
	 * to Context.bindService. Note that any extras that were included with the 
	 * Intent at that point will not be seen here.
	 * @return An IBinder through which clients can call on to the service. 
	 */
	public IBinder onBind(Intent intent) 
	{
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
			ret = getAuthenticator().getIBinder();
		return ret;
	}

	/**
	 * Get the current AccountAuthenticator object.
	 * 
	 * @return the current AccountAuthenticator object.
	 */
	private AccountAuthenticatorImpl getAuthenticator() 
	{
		if (sAccountAuthenticator == null)
			sAccountAuthenticator = new AccountAuthenticatorImpl(this);
		return sAccountAuthenticator;
	}

	/**
	 * Class which allows our OnYard account to be authenticated without prompting the user for input.
	 */
	private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
		/**
		 * The context under which this object is created.
		 */
		private Context mContext;

		/**
		 * Default constructor.
		 * 
		 * @param context The current context.
		 */
		public AccountAuthenticatorImpl(Context context) {
			super(context);
			mContext = context;
		}

		/**
		 * Authenticate and create a generic OnYard account.
		 * 
		 * Called when a request is sent to add an OnYard account.
		 * 
		 * @param response Result to send back to the AccountManager, will never be null.
		 * @param accountType The type of account to add, will never be null.
		 * @param authTokenType The type of auth token to retrieve after adding the account, 
		 * may be null.
		 * @param requiredFeatures A String array of authenticator-specific features that 
		 * the added account must support, may be null.
		 * @param options A Bundle of authenticator-specific options, may be null.
		 */
		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response, 
				String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
		{
			Bundle reply = new Bundle();

			Intent i = new Intent(mContext, OnYard.class);
			i.setAction("com.iaai.onyard.sync.LOGIN");
			i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
			reply.putParcelable(AccountManager.KEY_INTENT, i);

			return reply;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, android.os.Bundle)
		 */
		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response, 
				Account account, Bundle options) {
			return null;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#editProperties(android.accounts.AccountAuthenticatorResponse, java.lang.String)
		 */
		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
			return null;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
		 */
		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, 
				String authTokenType, Bundle options) throws NetworkErrorException {
			return null;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.lang.String)
		 */
		@Override
		public String getAuthTokenLabel(String authTokenType) {
			return null;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String[])
		 */
		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, 
				String[] features) throws NetworkErrorException {
			return null;
		}

		/* (non-Javadoc)
		 * @see android.accounts.AbstractAccountAuthenticator#updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, java.lang.String, android.os.Bundle)
		 */
		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, 
				String authTokenType, Bundle options) {
			return null;
		}
	}
}

