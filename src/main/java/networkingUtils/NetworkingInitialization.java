package networkingUtils;

import messages.*;
import messages.cardChoice.CardSelectionMessage;
import messages.cardChoice.ChooseCardMessage;
import messages.gameSetupMessages.MapMessage;
import messages.items.*;
import messages.messageListeners.ServerMessageListener;
import com.jme3.network.serializing.Serializer;
import messages.gameSetupMessages.NextLevelMessage;
import messages.lobby.GameStartedMessage;
import messages.lobby.HostChangedNicknameMessage;
import messages.lobby.HostChangedPlayerClassMessage;
import messages.lobby.HostJoinedLobbyMessage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkingInitialization {

    public static final int PORT = 6000;

    public static void initializeSerializables() {
                Serializer.registerClass(HostJoinedLobbyMessage.class);
                Serializer.registerClass(HostChangedNicknameMessage.class);
                Serializer.registerClass(GameStartedMessage.class);
                Serializer.registerClass(HostChangedPlayerClassMessage.class);
                Serializer.registerClass(MapMessage.class);

                Serializer.registerClass(EntityUpdateMessage.class);
                Serializer.registerClass(MobPosRotUpdateMessage.class);
                Serializer.registerClass(MobRotUpdateMessage.class);
                Serializer.registerClass(SystemHealthUpdateMessage.class);
                Serializer.registerClass(PlayerJoinedMessage.class);
                Serializer.registerClass(SetPlayerMessage.class);
                Serializer.registerClass(NewMobMessage.class);

                Serializer.registerClass(ServerMessageListener.class);
                Serializer.registerClass(NewChestMessage.class);
                Serializer.registerClass(DestructibleHealReceiveMessage.class);

                Serializer.registerClass(DestructibleDamageReceiveMessage.class);
                Serializer.registerClass(GrenadePosUpdateMessage.class);
                Serializer.registerClass(EntitySetIntegerAttributeMessage.class);
                Serializer.registerClass(EntitySetFloatAttributeMessage.class);
                Serializer.registerClass(BatchSetFloatAttributeMessage.class);
                Serializer.registerClass(BatchSetIntegerAttributeMessage.class);
                Serializer.registerClass(NextLevelMessage.class);
                Serializer.registerClass(AnimationPlayedMessage.class);

                Serializer.registerClass(NewMiscItemMessage.class);
                Serializer.registerClass(NewItemMessage.class);
                Serializer.registerClass(NewHelmetMessage.class);
                Serializer.registerClass(NewVestMessage.class);
                Serializer.registerClass(NewBootsMessage.class);
                Serializer.registerClass(NewGlovesMessage.class);
                Serializer.registerClass(NewRangedWeaponMessage.class);
                Serializer.registerClass(NewGrenadeMessage.class);
                Serializer.registerClass(NewAmmoPackMessage.class);
                Serializer.registerClass(GrenadeThrownMessage.class);
                Serializer.registerClass(NewMeleeWeaponMessage.class);
                Serializer.registerClass(MobItemInteractionMessage.class);
                Serializer.registerClass(ChestItemInteractionMessage.class);
                Serializer.registerClass(HitscanTrailMessage.class);
                Serializer.registerClass(SetDefaultItemMessage.class);
                Serializer.registerClass(PlayerPosUpdateRequest.class);
                Serializer.registerClass(InstantEntityPosCorrectionMessage.class);
                Serializer.registerClass(NewDestructibleDecorationMessage.class);
                Serializer.registerClass(NewIndestructibleDecorationMessage.class);
                Serializer.registerClass(DeleteEntityMessage.class);
                Serializer.registerClass(CardSelectionMessage.class);
                Serializer.registerClass(ChooseCardMessage.class);

                Serializer.registerClass(ThrownGrenadeExplodedMessage.class);
    }

}
