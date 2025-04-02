package dev.vfyjxf.conduitstratus.api.conduit.device;

import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

//TODO:重构为BlockEntity的子类
public abstract class BasicDevice implements Device {

    protected final DeviceType type;
    protected final NetworkNode holder;
    protected final AttachmentHolder.AsField attachmentHolder = new AttachmentHolder.AsField(this);

    protected TickStatus status = new TickStatus(20, 20).sleep();
    protected String identifier = "";

    protected BasicDevice(DeviceType type, NetworkNode holder) {
        this.type = type;
        this.holder = holder;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    @Override
    public NetworkNode getNode() {
        return holder;
    }

    @Override
    public Device setStatus(TickStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public TickStatus getStatus() {
        return status;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /////////////////////////////////////////////////////
    //********          DataAttachment         ********//
    /////////////////////////////////////////////////////

    @Override
    public boolean hasAttachments() {
        return attachmentHolder.hasAttachments();
    }

    @Override
    public boolean hasData(AttachmentType<?> type) {
        return attachmentHolder.hasData(type);
    }

    @Override
    public <T> T getData(AttachmentType<T> type) {
        return attachmentHolder.getData(type);
    }

    @Override
    public <T> Optional<T> getExistingData(AttachmentType<T> type) {
        return attachmentHolder.getExistingData(type);
    }

    @Override
    public <T> @Nullable T setData(AttachmentType<T> type, T data) {
        return attachmentHolder.setData(type, data);
    }

    @Override
    public <T> @Nullable T removeData(AttachmentType<T> type) {
        return attachmentHolder.removeData(type);
    }
}
