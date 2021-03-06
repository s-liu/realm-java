/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.internal;

/**
 * The LinkView class represent a core {@link ColumnType#LINK_LIST}.
 */
public class LinkView {

    private final Context context;
    private final long nativeLinkViewPtr;
    private final Table parent;
    private final long columnIndexInParent;

    public LinkView(Context context, Table parent, long columnIndexInParent, long nativeLinkViewPtr) {
        this.context = context;
        this.parent = parent;
        this.columnIndexInParent = columnIndexInParent;
        this.nativeLinkViewPtr = nativeLinkViewPtr;
    }

    public Row get(long pos) {
        long nativeRowPtr = nativeGetRow(nativeLinkViewPtr, pos);
        return new Row(context, parent.getLinkTarget(columnIndexInParent), nativeRowPtr);
    }

    public long getTargetRowIndex(long pos) {
        return nativeGetTargetRowIndex(nativeLinkViewPtr, pos);
    }

    public void add(long rowIndex) {
        checkImmutable();
        nativeAdd(nativeLinkViewPtr, rowIndex);
    }

    public void insert(long pos, long rowIndex) {
        checkImmutable();
        nativeInsert(nativeLinkViewPtr, pos, rowIndex);
    }

    public void set(long pos, long rowIndex) {
        checkImmutable();
        nativeSet(nativeLinkViewPtr, pos, rowIndex);
    }

    public void move(long oldPos, long newPos) {
        checkImmutable();
        nativeMove(nativeLinkViewPtr, oldPos, newPos);
    }

    public void remove(long pos) {
        checkImmutable();
        nativeRemove(nativeLinkViewPtr, pos);
    }

    public void clear() {
        checkImmutable();
        nativeClear(nativeLinkViewPtr);
    }

    public long size() {
        return nativeSize(nativeLinkViewPtr);
    }

    public boolean isEmpty() {
        return nativeIsEmpty(nativeLinkViewPtr);
    }

    public TableQuery where() {
        // Execute the disposal of abandoned realm objects each time a new realm object is created
        this.context.executeDelayedDisposal();
        long nativeQueryPtr = nativeWhere(nativeLinkViewPtr);
        try {
            return new TableQuery(this.context, this.parent, nativeQueryPtr);
        } catch (RuntimeException e) {
            TableQuery.nativeClose(nativeQueryPtr);
            throw e;
        }
    }

    private void checkImmutable() {
        if (parent.isImmutable()) {
            throw new IllegalStateException("Changing Realm data can only be done from inside a transaction.");
        }
    }

    protected static native void nativeClose(long nativeLinkViewPtr);
    private native long nativeGetRow(long nativeLinkViewPtr, long pos);
    private native long nativeGetTargetRowIndex(long nativeLinkViewPtr, long pos);
    private native void nativeAdd(long nativeLinkViewPtr, long rowIndex);
    private native void nativeInsert(long nativeLinkViewPtr, long pos, long rowIndex);
    private native void nativeSet(long nativeLinkViewPtr, long pos, long rowIndex);
    private native void nativeMove(long nativeLinkViewPtr, long oldPos, long newPos);
    private native void nativeRemove(long nativeLinkViewPtr, long pos);
    private native void nativeClear(long nativeLinkViewPtr);
    private native long nativeSize(long nativeLinkViewPtr);
    private native boolean nativeIsEmpty(long nativeLinkViewPtr);
    protected native long nativeWhere(long nativeLinkViewPtr);
}
