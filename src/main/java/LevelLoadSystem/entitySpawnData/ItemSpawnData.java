package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.Entity;
import game.items.ItemTemplates;
import server.ServerLevelManager;

public class ItemSpawnData extends EntitySpawnData{
    private boolean isDropped;
    private boolean droppable;

    public ItemSpawnData(int templateIndex, Vector3f position,boolean isDropped, boolean droppable) {
        super(templateIndex, position);
        this.isDropped = isDropped;
        this.droppable = droppable;
    }

    @Override
    public Entity serverSpawn(ServerLevelManager serverLevelManager) {
        var item = serverLevelManager.registerItemLocal(ItemTemplates.getTemplateByIndex(templateIndex),droppable);

        if (isDropped) {
            item.setDroppedOnServer(true);
            item.getNode().getWorldTranslation().set(this.getPosition());
        }
        return item;
    }
}
