package com.iaai.onyard.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.event.AuthCompleteEvent;
import com.iaai.onyard.event.IpBranchRetrievedEvent;
import com.iaai.onyard.event.LoginCompleteEvent;
import com.iaai.onyard.event.ToggleProgressDialogEvent;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.GetIpBranchTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.squareup.otto.Subscribe;


public class LoginDialogFragment extends BaseDialogFragment {

    private static final String IP_BRANCH_NOT_FOUND = "OnYard could not determine the branch for this device.";

    @InjectView(R.id.txt_login)
    EditText mLoginText;
    @InjectView(R.id.txt_password)
    EditText mPasswordText;
    @InjectView(R.id.btn_login)
    Button mLoginButton;
    @InjectView(R.id.btn_cancel)
    Button mCancelButton;
    @InjectView(R.id.lbl_login_failed)
    TextView mLoginErrorLabel;
    @InjectView(R.id.lbl_password_expired)
    TextView mPasswordExpiredLabel;
    @InjectView(R.id.lbl_missing_username)
    TextView mMissingUsername;
    @InjectView(R.id.lbl_missing_password)
    TextView mMissingPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_dialog, container);
        ButterKnife.inject(this, view);

        addInputListeners();
        final WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setCanceledOnTouchOutside(false);

        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String userLogin = mLoginText.getText().toString().trim();
                String userPass = mPasswordText.getText().toString().trim();
                mMissingUsername.setVisibility(View.GONE);
                mMissingPassword.setVisibility(View.GONE);
                mLoginErrorLabel.setVisibility(View.GONE);

                if (userLogin.isEmpty()) {
                    mMissingUsername.setVisibility(View.VISIBLE);
                }
                if (userPass.isEmpty()) {
                    mMissingPassword.setVisibility(View.VISIBLE);
                }

                if (!userLogin.isEmpty() && !userPass.isEmpty()) {
                    showLoginProgressDialog();
                    AuthenticationHelper.logUserIn(getActivity().getApplicationContext(),
                            userLogin, userPass, getEventBus());
                    userPass = null;
                    mPasswordText.setText(null);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissLoginDialog();
            }
        });
        return view;
    }

    private void addInputListeners() {
        mLoginText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                mLoginErrorLabel.setVisibility(View.GONE);
                mMissingUsername.setVisibility(View.GONE);
            }
        });

        mPasswordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                mLoginErrorLabel.setVisibility(View.GONE);
                mMissingPassword.setVisibility(View.GONE);
            }
        });
    }

    protected void dismissLoginDialog() {
        getDialog().dismiss();
    }

    @Subscribe
    public void onAuthComplete(AuthCompleteEvent event) {
        switch (event.getResult()) {
            case NO_NETWORK:
                onNoNetworkConn();
                break;
            case SUCCESS:
                onAuthSuccessful();
                break;
            case FAILURE:
                onAuthFailed();
                break;
            case EXPIRED:
                onPasswordExpired();
                break;
            default:
                onAuthFailed();
                break;
        }
    }

    private void onAuthSuccessful() {
        new GetIpBranchTask().execute(getActivity().getApplicationContext(), getEventBus());
    }

    private void onAuthFailed() {
        dismissLoginProgressDialog();
        mLoginErrorLabel.setVisibility(View.VISIBLE);
    }

    private void onPasswordExpired() {
        dismissLoginProgressDialog();
        mPasswordExpiredLabel.setVisibility(View.VISIBLE);
    }

    private void showLoginProgressDialog() {
        getEventBus().post(new ToggleProgressDialogEvent(true));
    }

    private void dismissLoginProgressDialog() {
        getEventBus().post(new ToggleProgressDialogEvent(false));
    }

    @Subscribe
    public void onIpBranchRetried(IpBranchRetrievedEvent event) {
        switch (event.getResult()) {
            case NO_NETWORK:
                onNoNetworkConn();
                break;
            case NO_IP_BRANCH_FOUND:
                onNoIpBranchFound();
                break;
            case EFFECTIVE_BRANCH_CHANGED:
                onEffectiveBranchChanged();
                break;
            case IP_BRANCH_SET:
                onIpBranchSet();
                break;
            default:
                onNoIpBranchFound();
                break;
        }
    }

    private void onNoNetworkConn() {
        dismissLoginProgressDialog();
        dismissLoginDialog();
        new NetworkErrorOnLoginDialogFragment().show(getFragmentManager(),
                FragmentTag.NETWORK_ERROR_DIALOG);
    }

    private void onEffectiveBranchChanged() {
        dismissLoginProgressDialog();
        dismissLoginDialog();

        final ErrorDialogFragment errorDialog = ErrorDialogFragment
                .newInstance("OnYard has changed this device's branch to "
                        + new OnYardPreferences(getActivity()).getIpBranchName()
                        + ". If this is incorrect, please contact the Support Desk.");
        errorDialog.show(getActivity().getSupportFragmentManager(), FragmentTag.ERROR_DIALOG);

        SyncHelper.requestFullSync(getActivity().getApplicationContext(), false);
        getEventBus().post(new LoginCompleteEvent());
    }

    private void onNoIpBranchFound() {
        dismissLoginProgressDialog();
        dismissLoginDialog();

        final OnYardPreferences preferences = new OnYardPreferences(getActivity());

        String errorDialogString = IP_BRANCH_NOT_FOUND;
        if (preferences.getIpBranchNumber().equals(
                OnYard.DEFAULT_BRANCH_NUMBER)) {
            errorDialogString += " Please contact the support Desk.";
        }
        else {
            errorDialogString += " The branch will remain set to " + preferences.getIpBranchName()
                    + ".";
        }
        final ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(errorDialogString);
        errorDialog.show(getActivity().getSupportFragmentManager(), FragmentTag.ERROR_DIALOG);

        SyncHelper.requestOnDemandSync(getActivity().getApplicationContext());
        getEventBus().post(new LoginCompleteEvent());
    }

    private void onIpBranchSet() {
        dismissLoginProgressDialog();
        dismissLoginDialog();

        SyncHelper.requestOnDemandSync(getActivity().getApplicationContext());
        getEventBus().post(new LoginCompleteEvent());
    }
}
