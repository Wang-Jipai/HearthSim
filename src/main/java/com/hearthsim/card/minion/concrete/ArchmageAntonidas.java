package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.Card;
import com.hearthsim.card.CardPlayBeginInterface;
import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.spellcard.SpellCard;
import com.hearthsim.card.spellcard.concrete.Fireball;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;

public class ArchmageAntonidas extends Minion implements CardPlayBeginInterface {

    public ArchmageAntonidas() {
        super();
    }
    /**
     *
     * Called whenever another card is used
     *
     * When you cast a spell, put a Fireball spell into your hand
     *  @param thisCardPlayerSide The player index of the card receiving the event
     * @param cardUserPlayerSide
     * @param usedCard The card that was used
     * @param boardState The BoardState before this card has performed its action.  It will be manipulated and returned.
     * @param deckPlayer0 The deck of player0
     * @param deckPlayer1 The deck of player1
     * @return The boardState is manipulated and returned
     */
    @Override
    public HearthTreeNode onCardPlayBegin(
            PlayerSide thisCardPlayerSide,
            PlayerSide cardUserPlayerSide,
            Card usedCard,
            HearthTreeNode boardState,
            boolean singleRealizationOnly)
        throws HSException {
        HearthTreeNode toRet = boardState;
        if (thisCardPlayerSide != PlayerSide.CURRENT_PLAYER)
            return toRet;
        if (isInHand_)
            return toRet;
        if (usedCard instanceof SpellCard && toRet.data_.getCurrentPlayer().getHand().size() < 10) {
            toRet.data_.getCurrentPlayer().placeCardHand(new Fireball());
        }
        return toRet;
    }
}
