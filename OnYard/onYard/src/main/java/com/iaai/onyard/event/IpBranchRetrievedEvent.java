package com.iaai.onyard.event;

public class IpBranchRetrievedEvent {

    public enum IpBranchResult {
        EFFECTIVE_BRANCH_CHANGED, NO_IP_BRANCH_FOUND, IP_BRANCH_SET, NO_NETWORK
    }

    private final IpBranchResult mResult;

    public IpBranchRetrievedEvent(IpBranchResult result) {
        mResult = result;
    }

    public IpBranchResult getResult() {
        return mResult;
    }
}
