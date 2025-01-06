package game.entities.factories;

import game.entities.mobs.Mob;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public abstract class MobFactory {

    protected AssetManager assetManager;
    protected Node mobsNode;
    protected int id;


    protected MobFactory(int id,AssetManager assetManager, Node mobsNode) {
        this.id = id;
        this.assetManager = assetManager;
        this.mobsNode = mobsNode;
    }

    protected MobFactory(int id,Node mobsNode) {
        this.id = id;
        this.assetManager = Main.getInstance().getAssetManager();
        this.mobsNode = mobsNode;
    }

    public abstract Mob createClientSide(MobSpawnType spawnType,Object... creationData);

    public abstract Mob createServerSide(MobSpawnType spawnType,Object... creationData);






}
