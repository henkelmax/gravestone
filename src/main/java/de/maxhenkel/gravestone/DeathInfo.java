package de.maxhenkel.gravestone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;
import java.util.UUID;

public class DeathInfo {

    public static final Codec<DeathInfo> CODEC = RecordCodecBuilder.create(i -> {
        return i.group(
                UUIDUtil.CODEC.fieldOf("PlayerUUID").forGetter(DeathInfo::getPlayerId),
                UUIDUtil.CODEC.fieldOf("DeathID").forGetter(DeathInfo::getDeathId)
        ).apply(i, DeathInfo::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, DeathInfo> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            DeathInfo::getPlayerId,
            UUIDUtil.STREAM_CODEC,
            DeathInfo::getDeathId,
            DeathInfo::new
    );

    private final UUID playerId;
    private final UUID deathId;

    public DeathInfo(UUID playerId, UUID deathId) {
        this.playerId = playerId;
        this.deathId = deathId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getDeathId() {
        return deathId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeathInfo deathInfo = (DeathInfo) o;
        return Objects.equals(playerId, deathInfo.playerId) && Objects.equals(deathId, deathInfo.deathId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(playerId);
        result = 31 * result + Objects.hashCode(deathId);
        return result;
    }
}
