package de.i0n.burst.scene;

import com.jme.math.Vector3f;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameStateNode;
import com.jmex.game.state.GameStateNode;

public class World extends BasicGameStateNode<GameStateNode<?>> {
    private static final long serialVersionUID = 1L;
    
    private final StandardGame game;
    private final Board board;
    
    public World(StandardGame game) {
        super("game world");
        this.game = game;

        board = new Board();
        attachChild(board);
        board.setActive(true);
        
        game.getCamera().setLocation(new Vector3f(-1.5f, -1, 30));
        game.getCamera().lookAt(new Vector3f(0, 0, 0), new Vector3f(0f, 1f, 0f));
        
        rootNode.updateRenderState();
    }
}
