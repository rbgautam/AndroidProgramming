package com.iaai.onyard.event;

public class ThumbnailGridScrolledEvent {

    public enum ScrollDirection {
        UP, DOWN
    }

    private final ScrollDirection mDirection;

    public ThumbnailGridScrolledEvent(ScrollDirection direction) {
        mDirection = direction;
    }

    public ScrollDirection getScrollDirection() {
        return mDirection;
    }
}
