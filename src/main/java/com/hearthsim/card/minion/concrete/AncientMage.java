package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.MinionUntargetableBattlecry;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.PlayerModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;

public class AncientMage extends Minion implements MinionUntargetableBattlecry {

    public AncientMage() {
        super();
    }

    /**
     * Battlecry: Destroy your opponent's weapon
     */
    @Override
    public HearthTreeNode useUntargetableBattlecry_core(
            Minion minionPlacementTarget,
            HearthTreeNode boardState,
            boolean singleRealizationOnly
        ) throws HSException {
        HearthTreeNode toRet = boardState;
        PlayerModel currentPlayer = toRet.data_.modelForSide(PlayerSide.CURRENT_PLAYER);

        int thisMinionIndex = currentPlayer.getMinions().indexOf(this);
        int numMinions = currentPlayer.getNumMinions();
        if (numMinions > 1) {
            int minionToTheLeft = thisMinionIndex > 0 ? thisMinionIndex - 1 : -1;
            int minionToTheRight = thisMinionIndex < numMinions - 1 ? thisMinionIndex + 1 : -1;
            if (minionToTheLeft >= 0) {
                Minion minionToBuff = toRet.data_.getMinion(PlayerSide.CURRENT_PLAYER, minionToTheLeft);
                minionToBuff.addSpellDamage((byte)1);
            }
            if (minionToTheRight >= 0) {
                Minion minionToBuff = toRet.data_.getMinion(PlayerSide.CURRENT_PLAYER, minionToTheRight);
                minionToBuff.addSpellDamage((byte)1);
            }
        }
        return toRet;
    }


}
