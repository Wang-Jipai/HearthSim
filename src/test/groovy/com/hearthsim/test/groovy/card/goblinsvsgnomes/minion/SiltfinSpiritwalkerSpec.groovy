package com.hearthsim.test.groovy.card.goblinsvsgnomes.minion

import com.hearthsim.card.basic.minion.MurlocRaider
import com.hearthsim.card.basic.minion.MurlocScout
import com.hearthsim.card.basic.spell.Fireball
import com.hearthsim.card.basic.spell.Whirlwind
import com.hearthsim.card.classic.minion.common.StranglethornTiger
import com.hearthsim.card.classic.minion.epic.PatientAssassin
import com.hearthsim.card.classic.spell.epic.TwistingNether
import com.hearthsim.card.goblinsvsgnomes.minion.common.Puddlestomper
import com.hearthsim.card.goblinsvsgnomes.minion.epic.SiltfinSpiritwalker
import com.hearthsim.model.BoardModel
import com.hearthsim.test.groovy.card.CardSpec
import com.hearthsim.test.helpers.BoardModelBuilder
import com.hearthsim.util.tree.CardDrawNode
import com.hearthsim.util.tree.HearthTreeNode

import static com.hearthsim.model.PlayerSide.CURRENT_PLAYER
import static com.hearthsim.model.PlayerSide.WAITING_PLAYER

class SiltfinSpiritwalkerSpec extends CardSpec {

    HearthTreeNode root
    BoardModel startingBoard

    def setup() {
        startingBoard = new BoardModelBuilder().make {
            currentPlayer {
                hand([Fireball, Whirlwind, TwistingNether])
                field([[minion: SiltfinSpiritwalker], [minion: MurlocRaider], [minion: MurlocScout], [minion: PatientAssassin]])
                mana(10)
            }
            waitingPlayer {
                field([[minion: Puddlestomper],[minion: StranglethornTiger]])
            }
        }

        root = new HearthTreeNode(startingBoard)
    }

    def "draws card on friendly murloc death"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(0)
        def ret = theCard.useOn(CURRENT_PLAYER, 2, root)

        expect:
        ret != null
        ret instanceof CardDrawNode

        ((CardDrawNode)ret).numCardsToDraw == 1

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(Fireball)
                mana(6)
                numCardsUsed(1)
                removeMinion(1)
            }
        }
    }

    def "does not trigger on enemy murloc death"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(0)
        def ret = theCard.useOn(WAITING_PLAYER, 1, root)

        expect:
        ret != null
        ret instanceof HearthTreeNode
        !(ret instanceof CardDrawNode)

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(Fireball)
                mana(6)
                numCardsUsed(1)
            }
            waitingPlayer {
                removeMinion(0)
            }
        }
    }

    def "does not trigger on non-murloc death"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(0)
        def ret = theCard.useOn(CURRENT_PLAYER, 4, root)

        expect:
        ret != null
        ret instanceof HearthTreeNode
        !(ret instanceof CardDrawNode)

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(Fireball)
                mana(6)
                numCardsUsed(1)
                removeMinion(3)
            }
        }
    }

    def "draws one card for each death"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(1)
        def ret = theCard.useOn(CURRENT_PLAYER, 0, root)

        expect:
        ret != null
        ret instanceof CardDrawNode

        ((CardDrawNode)ret).numCardsToDraw == 2

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(Whirlwind)
                mana(9)
                numCardsUsed(1)
                removeMinion(3)
                removeMinion(2)
                removeMinion(1)
                updateMinion(0, [deltaHealth: -1])
            }
            waitingPlayer {
                updateMinion(1, [deltaHealth: -1])
                updateMinion(0, [deltaHealth: -1])
            }
        }
    }

    def "does not draw if also died"() {
        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(2)
        def ret = theCard.useOn(CURRENT_PLAYER, 0, root)

        expect:
        ret != null
        !(ret instanceof CardDrawNode)

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(TwistingNether)
                mana(2)
                numCardsUsed(1)
                removeMinion(3)
                removeMinion(2)
                removeMinion(1)
                removeMinion(0)
            }
            waitingPlayer {
                removeMinion(1)
                removeMinion(0)
            }
        }
    }

    def "does not trigger while in hand"() {
        startingBoard.modelForSide(CURRENT_PLAYER).placeCardHand(new SiltfinSpiritwalker())
        startingBoard.removeMinion(CURRENT_PLAYER, 0)

        def copiedBoard = startingBoard.deepCopy()
        def theCard = root.data_.getCurrentPlayer().getHand().get(0)
        def ret = theCard.useOn(CURRENT_PLAYER, 1, root)

        expect:
        ret != null
        ret instanceof HearthTreeNode
        !(ret instanceof CardDrawNode)

        assertBoardDelta(copiedBoard, ret.data_) {
            currentPlayer {
                removeCardFromHand(Fireball)
                mana(6)
                numCardsUsed(1)
                removeMinion(0)
            }
        }
    }
}
