package com.jcloisterzone.action;

import com.jcloisterzone.board.Position;
import com.jcloisterzone.ui.annotations.LinkedGridLayer;
import com.jcloisterzone.ui.annotations.LinkedImage;
import com.jcloisterzone.ui.grid.layer.TileActionLayer;
import com.jcloisterzone.wsio.message.MoveNeutralFigureMessage;
import com.jcloisterzone.wsio.message.WsInGameMessage;

import io.vavr.collection.Set;

//TODO generic NeutralMeepleAction ?
@LinkedImage("actions/dragonmove")
@LinkedGridLayer(TileActionLayer.class)
public class MoveDragonAction extends SelectTileAction {

    private final String figureId;

    public MoveDragonAction(String figureId, Set<Position> options) {
        super(options);
        this.figureId = figureId;
    }

    @Override
    public WsInGameMessage select(Position target) {
        return new MoveNeutralFigureMessage(figureId, target);
    }

    @Override
    public String toString() {
        return "move " + figureId;
    }

}