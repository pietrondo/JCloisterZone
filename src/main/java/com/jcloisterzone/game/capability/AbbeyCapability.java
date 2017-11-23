package com.jcloisterzone.game.capability;

import com.jcloisterzone.Expansion;
import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.feature.Cloister;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.game.Capability;
import com.jcloisterzone.game.Token;
import com.jcloisterzone.game.state.GameState;

import io.vavr.collection.HashMap;


/**
 * @model Integer: store playerIndex during final abbey placement turn
 */
public class AbbeyCapability extends Capability<Integer> {

    public static Tile ABBEY_TILE;

    static {
        HashMap<Location, Feature> features = io.vavr.collection.HashMap.of(
            Location.CLOISTER, new Cloister()
        );
        ABBEY_TILE = new Tile(Expansion.ABBEY_AND_MAYOR, Tile.ABBEY_TILE_ID, features);
    }


    @Override
    public GameState onStartGame(GameState state) {
        return state.mapPlayers(ps -> ps.setTokenCountForAllPlayers(Token.ABBEY_TILE, 1));
    }
}
