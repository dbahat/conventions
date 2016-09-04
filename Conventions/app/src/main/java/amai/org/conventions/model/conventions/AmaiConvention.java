package amai.org.conventions.model.conventions;

import android.content.Context;

import amai.org.conventions.model.EventToImageResourceIdMapper;

public abstract class AmaiConvention extends Convention {
    private EventToImageResourceIdMapper imageMapper;

    public EventToImageResourceIdMapper getImageMapper() {
        return imageMapper;
    }

    @Override
    public void load(Context context) {
        super.load(context);
        this.imageMapper = initImageMapper();
    }

    protected abstract EventToImageResourceIdMapper initImageMapper();
}
