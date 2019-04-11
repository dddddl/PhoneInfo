package com.lx.qz.transform.bean;


public class WifiBean {

    private String SSID;

    private MNetworkSelectionStatusBean mNetworkSelectionStatus;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public MNetworkSelectionStatusBean getMNetworkSelectionStatus() {
        return mNetworkSelectionStatus;
    }

    public void setMNetworkSelectionStatus(MNetworkSelectionStatusBean mNetworkSelectionStatus) {
        this.mNetworkSelectionStatus = mNetworkSelectionStatus;
    }

    public static class MNetworkSelectionStatusBean {
        /**
         * mCandidateScore : 0
         * mConnectChoiceTimestamp : -1
         * mHasEverConnected : true
         * mNetworkSeclectionDisableCounter : [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
         * mNetworkSelectionDisableReason : 0
         * mNotRecommended : false
         * mSeenInLastQualifiedNetworkSelection : false
         * mStatus : 0
         * mTemporarilyDisabledTimestamp : -1
         */

        private boolean mHasEverConnected;

        public boolean isMHasEverConnected() {
            return mHasEverConnected;
        }

        public void setMHasEverConnected(boolean mHasEverConnected) {
            this.mHasEverConnected = mHasEverConnected;
        }
    }
}
