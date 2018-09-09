package com.derek.chinesewriting;

public class Result {
    private boolean isOuterWriting;
    private int targetSize;
    private int writingSize;

    public boolean isOuterWriting() {
        return isOuterWriting;
    }

    public void setOuterWriting(boolean outerWriting) {
        isOuterWriting = outerWriting;
    }

    public int getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(int targetSize) {
        this.targetSize = targetSize;
    }

    public int getWritingSize() {
        return writingSize;
    }

    public void setWritingSize(int writingSize) {
        this.writingSize = writingSize;
    }
}
