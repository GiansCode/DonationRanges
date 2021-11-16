package io.alerium.donationranges.donations.objects;

import java.util.List;

public class RangeReward {

    private final int minAmount;
    private final List<String> actions;

    public RangeReward(int minAmount, List<String> actions) {
        this.minAmount = minAmount;
        this.actions = actions;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public List<String> getActions() {
        return actions;
    }

}
